package dev.anhcraft.abm.gui;

import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.enums.ItemType;
import dev.anhcraft.abm.api.objects.PlayerData;
import dev.anhcraft.abm.gui.core.*;
import dev.anhcraft.abm.system.handlers.MagazineHandler;
import dev.anhcraft.abm.utils.StringUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MagazineInventoryHandler extends BattleGuiHandler implements PaginationHandler {
    public MagazineInventoryHandler(BattlePlugin plugin) {
        super(plugin);
    }

    @Override
    public void getData(Player p, PlayerGui gui, BattlePagination bg, int fromIndex, int toIndex, List<ItemStack> items) {
        Optional<PlayerData> pd = plugin.getPlayerData(p);
        if(pd.isPresent()) {
            List<Map.Entry<String, Long>> x = pd.get().getInventory().getStorage(ItemType.GUN).list();
            int len = Math.min(toIndex + 1, x.size());
            for(int i = fromIndex; i < len; i++){
                Map.Entry<String, Long> id = x.get(i);
                plugin.getMagazineModel(id.getKey()).ifPresent(m -> items.add(plugin.getHandler(MagazineHandler.class).draw(
                        m.getSkin(),
                        plugin.itemManager.makeModel(m),
                        StringUtil.formatPlaceholders(p,bg.getHeaderLore()
                                .stream()
                                .map(s -> s.replace("{__owning_date__}",
                                        plugin.formatLongFormDate(new Date(id.getValue()))))
                                .collect(Collectors.toList())),
                        StringUtil.formatPlaceholders(p,bg.getFooterLore()
                                .stream()
                                .map(s -> s.replace("{__owning_date__}",
                                        plugin.formatLongFormDate(new Date(id.getValue()))))
                                .collect(Collectors.toList())))));
            }
        }
    }

    @SlotClickHandler("prev")
    public void prev(InventoryClickEvent event, PlayerGui gui){
        int c = gui.getPage();
        if(c == 0) return;
        gui.setPage(c-1);
        plugin.guiManager.openInventory((Player) event.getWhoClicked(), gui);
    }

    @SlotClickHandler("back")
    public void back(InventoryClickEvent event){
        plugin.guiManager.openInventory((Player) event.getWhoClicked(), "inventory_menu");
    }

    @SlotClickHandler("next")
    public void next(InventoryClickEvent event, PlayerGui gui){
        if(gui.isOutOfData()) return;
        gui.setPage(gui.getPage()+1);
        plugin.guiManager.openInventory((Player) event.getWhoClicked(), gui);
    }
}
