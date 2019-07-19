package dev.anhcraft.abm.gui;

import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.objects.Arena;
import dev.anhcraft.abm.gui.core.*;
import dev.anhcraft.abm.utils.StringUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ArenaChooserHandler extends BattleGuiHandler implements PaginationHandler {
    public ArenaChooserHandler(BattlePlugin plugin) {
        super(plugin);
    }

    @Override
    public void getData(Player p, PlayerGui gui, BattlePagination bg, int fromIndex, int toIndex, List<ItemStack> items) {
        List<Arena> x = plugin.listArenas();
        if(fromIndex < x.size()) x.subList(fromIndex, Math.min(toIndex+1, x.size())).forEach(a -> {
            ItemStack item = new ItemStack(a.getIcon(), 1);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(StringUtil.formatPlaceholders(p, a.getName()));
                List<String> lore = new ArrayList<>();
                lore.addAll(StringUtil.formatPlaceholders(p, bg.getHeaderLore()));
                lore.addAll(StringUtil.formatPlaceholders(p, bg.getFooterLore()));
                meta.setLore(lore);
            }
            item.setItemMeta(meta);
            items.add(item);
        });
    }

    @SlotClickHandler(SlotClickHandler.PAGINATION)
    public void pagination(InventoryClickEvent event, PlayerGui gui){
        Player p = (Player) event.getWhoClicked();
        Integer f = gui.getSlot2DataIndexes().get(event.getSlot());
        if(f == null) return;
        plugin.gameManager.join(p, plugin.listArenas().get(f));
    }
}
