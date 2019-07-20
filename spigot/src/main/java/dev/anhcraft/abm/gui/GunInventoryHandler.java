package dev.anhcraft.abm.gui;

import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.enums.ItemType;
import dev.anhcraft.abm.api.events.ItemChooseEvent;
import dev.anhcraft.abm.api.objects.PlayerData;
import dev.anhcraft.abm.gui.core.*;
import dev.anhcraft.abm.system.handlers.GunHandler;
import dev.anhcraft.abm.utils.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class GunInventoryHandler extends BattleGuiHandler implements PaginationHandler {
    public GunInventoryHandler(BattlePlugin plugin) {
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
                plugin.getGunModel(id.getKey()).ifPresent(g -> items.add(plugin.getHandler(GunHandler.class).draw(
                        g.getPrimarySkin(),
                        plugin.itemManager.makeModel(g),
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

    @SlotClickHandler(SlotClickHandler.PAGINATION)
    public void pagination(InventoryClickEvent event, PlayerGui gui){
        Player p = (Player) event.getWhoClicked();
        plugin.getPlayerData(p).ifPresent(playerData -> {
            Integer dataIndex = gui.getSlot2DataIndexes().get(event.getSlot());
            if(dataIndex == null) return;
            Map.Entry<String, Long> id = playerData.getInventory().getStorage(ItemType.GUN)
                    .list().get(dataIndex);
            if(id == null) return;
            plugin.getGunModel(id.getKey()).ifPresent(gun -> {
                ItemChooseEvent e = new ItemChooseEvent(p, event.getCurrentItem(), gun);
                Bukkit.getPluginManager().callEvent(e);
            });
        });
    }
}
