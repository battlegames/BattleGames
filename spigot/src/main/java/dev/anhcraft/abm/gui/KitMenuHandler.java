package dev.anhcraft.abm.gui;

import com.google.common.collect.LinkedHashMultimap;
import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.ext.gui.GuiHandler;
import dev.anhcraft.abm.api.ext.gui.GuiListener;
import dev.anhcraft.abm.api.impl.gui.PaginationHandler;
import dev.anhcraft.abm.api.objects.ItemStorage;
import dev.anhcraft.abm.api.objects.gui.Pagination;
import dev.anhcraft.abm.api.objects.gui.SlotClickReport;
import dev.anhcraft.abm.api.objects.gui.SlotReport;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Date;

public class KitMenuHandler extends GuiHandler implements PaginationHandler {
    public KitMenuHandler(BattlePlugin plugin) {
        super(plugin);
    }

    @Override
    public void pullData(Pagination pagination, Player player, LinkedHashMultimap<ItemStack, GuiListener<? extends SlotReport>> data) {
        plugin.getPlayerData(player).ifPresent(pd -> {
            plugin.listKits().forEach(kit -> {
                if(kit.getPermission() != null && !player.hasPermission(kit.getPermission())) {
                    data.put(kit.getNoAccessIcon().build(), new GuiListener<SlotClickReport>(SlotClickReport.class) {
                        @Override
                        public void call(SlotClickReport event) {
                            plugin.chatProvider.sendPlayer(event.getPlayer(), "kit.no_permission");
                            event.getClickEvent().setCancelled(true);
                        }
                    });
                    return;
                }
                long last = pd.getKits().getOrDefault(kit.getId(), 0L);
                if(last != 0){
                    if(kit.getRenewTime() == -1){
                        data.put(kit.getNoAccessIcon().build(), new GuiListener<SlotClickReport>(SlotClickReport.class) {
                            @Override
                            public void call(SlotClickReport event) {
                                plugin.chatProvider.sendPlayer(event.getPlayer(), "kit.one_time_use");
                                event.getClickEvent().setCancelled(true);
                            }
                        });
                        return;
                    }
                    long next = last + kit.getRenewTime()*50;
                    if(next > System.currentTimeMillis()){
                        data.put(kit.getNoAccessIcon().build(), new GuiListener<SlotClickReport>(SlotClickReport.class) {
                            @Override
                            public void call(SlotClickReport event) {
                                String msg = plugin.chatProvider.getFormattedMessage(event.getPlayer(), "kit.unavailable");
                                msg = String.format(msg, plugin.formatLongFormDate(new Date(next)));
                                event.getPlayer().sendMessage(msg);
                                event.getClickEvent().setCancelled(true);
                            }
                        });
                        return;
                    }
                }
                data.put(kit.getIcon().build(), new GuiListener<SlotClickReport>(SlotClickReport.class) {
                    @Override
                    public void call(SlotClickReport event) {
                        event.getPlayer().getInventory().addItem(kit.getVanillaItems()).values().forEach(itemStack -> event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), itemStack));
                        kit.getAbmItems().forEach((type, x) -> {
                            ItemStorage is = pd.getInventory().getStorage(type);
                            x.forEach(is::put);
                        });
                        pd.getKits().put(kit.getId(), System.currentTimeMillis());
                        plugin.guiManager.openTopInventory(event.getPlayer(), "kit_menu");
                    }
                });
            });
        });
    }
}
