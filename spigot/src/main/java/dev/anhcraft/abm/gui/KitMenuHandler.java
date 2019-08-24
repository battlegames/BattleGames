package dev.anhcraft.abm.gui;

import dev.anhcraft.abm.api.APIProvider;
import dev.anhcraft.abm.api.BattleAPI;
import dev.anhcraft.abm.api.gui.*;
import dev.anhcraft.abm.api.inventory.ItemStorage;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.List;

public class KitMenuHandler extends GuiHandler implements PaginationHandler {
    @Override
    public void pullData(Pagination pagination, Player player, List<PaginationItem> data) {
        BattleAPI api = APIProvider.get();
        api.getPlayerData(player).ifPresent(pd -> {
            api.listKits().forEach(kit -> {
                if(kit.getPermission() != null && !player.hasPermission(kit.getPermission())) {
                    data.add(new PaginationItem(kit.getNoAccessIcon().build(), new GuiListener<SlotClickReport>(SlotClickReport.class) {
                        @Override
                        public void call(SlotClickReport event) {
                            api.getChatManager().sendPlayer(event.getPlayer(), "kit.no_permission");
                            event.getClickEvent().setCancelled(true);
                        }
                    }));
                    return;
                }
                long last = pd.getKits().getOrDefault(kit.getId(), 0L);
                if(last != 0){
                    if(kit.getRenewTime() == -1){
                        data.add(new PaginationItem(kit.getNoAccessIcon().build(), new GuiListener<SlotClickReport>(SlotClickReport.class) {
                            @Override
                            public void call(SlotClickReport event) {
                                api.getChatManager().sendPlayer(event.getPlayer(), "kit.one_time_use");
                                event.getClickEvent().setCancelled(true);
                            }
                        }));
                        return;
                    }
                    long next = last + kit.getRenewTime()*50;
                    if(next > System.currentTimeMillis()){
                        data.add(new PaginationItem(kit.getNoAccessIcon().build(), new GuiListener<SlotClickReport>(SlotClickReport.class) {
                            @Override
                            public void call(SlotClickReport event) {
                                String msg = api.getChatManager().getFormattedMessage(event.getPlayer(), "kit.unavailable");
                                msg = String.format(msg, api.formatLongFormDate(new Date(next)));
                                event.getPlayer().sendMessage(msg);
                                event.getClickEvent().setCancelled(true);
                            }
                        }));
                        return;
                    }
                }
                data.add(new PaginationItem(kit.getIcon().build(), new GuiListener<SlotClickReport>(SlotClickReport.class) {
                    @Override
                    public void call(SlotClickReport event) {
                        event.getClickEvent().setCancelled(true);
                        event.getPlayer().getInventory().addItem(kit.getVanillaItems()).values().forEach(itemStack -> event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), itemStack));
                        kit.getAbmItems().forEach((type, x) -> {
                            ItemStorage is = pd.getInventory().getStorage(type);
                            x.forEach(is::put);
                        });
                        pd.getKits().put(kit.getId(), System.currentTimeMillis());
                        api.getGuiManager().openTopInventory(event.getPlayer(), "kit_menu");
                    }
                }));
            });
        });
    }
}
