package dev.anhcraft.abm.gui;

import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.objects.ItemStorage;
import dev.anhcraft.abm.api.objects.Kit;
import dev.anhcraft.abm.gui.core.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Date;
import java.util.List;

public class KitMenuHandler extends BattleGuiHandler implements PaginationHandler {
    public KitMenuHandler(BattlePlugin plugin) {
        super(plugin);
    }

    @Override
    public void getData(Player p, PlayerGui gui, BattlePagination bg, int fromIndex, int toIndex, List<ItemStack> items) {
        plugin.getPlayerData(p).ifPresent(pd -> {
            List<Kit> x = plugin.listKits();
            if(fromIndex < x.size()) x.subList(fromIndex, Math.min(toIndex+1, x.size())).forEach(kit -> {
                if(kit.getPermission() != null && !p.hasPermission(kit.getPermission())) {
                    items.add(kit.getNoAccessIcon());
                    return;
                }
                long last = pd.getKits().getOrDefault(kit.getId(), 0L);
                if(last != 0 && (kit.getRenewTime() == -1 || last + kit.getRenewTime()*50 > System.currentTimeMillis())){
                    items.add(kit.getNoAccessIcon());
                    return;
                }
                items.add(kit.getIcon());
            });
        });
    }

    @SlotClickHandler(SlotClickHandler.PAGINATION)
    public void pagination(InventoryClickEvent event, PlayerGui gui){
        Player p = (Player) event.getWhoClicked();
        plugin.getPlayerData(p).ifPresent(pd -> {
            Integer f = gui.getSlot2DataIndexes().get(event.getSlot());
            if(f == null) return;
            Kit kit = plugin.listKits().get(f);
            if(kit == null) return;
            if(kit.getPermission() != null && !p.hasPermission(kit.getPermission())) {
                plugin.chatProvider.sendPlayer(p, "kit.no_permission");
                return;
            }
            long last = pd.getKits().getOrDefault(kit.getId(), 0L);
            if(kit.getRenewTime() == -1 && last != 0){
                plugin.chatProvider.sendPlayer(p, "kit.one_time_use");
                return;
            }
            long next = last + kit.getRenewTime()*50;
            if(next > System.currentTimeMillis()) {
                p.sendMessage(String.format(plugin.chatProvider.getFormattedMessage(p, "kit.unavailable"), plugin.formatLongFormDate(new Date(next))));
                return;
            }
            p.getInventory().addItem(kit.getVanillaItems());
            kit.getAbmItems().forEach((type, x) -> {
                ItemStorage is = pd.getInventory().getStorage(type);
                x.forEach(is::put);
            });
            pd.getKits().put(kit.getId(), System.currentTimeMillis());
            plugin.guiManager.openInventory(p, "kit_menu");
        });
    }
}
