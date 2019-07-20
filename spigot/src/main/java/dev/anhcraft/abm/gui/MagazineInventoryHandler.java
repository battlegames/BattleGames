package dev.anhcraft.abm.gui;

import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.enums.ItemType;
import dev.anhcraft.abm.api.objects.PlayerData;
import dev.anhcraft.abm.gui.core.BattleGuiHandler;
import dev.anhcraft.abm.gui.core.BattlePagination;
import dev.anhcraft.abm.gui.core.PaginationHandler;
import dev.anhcraft.abm.gui.core.PlayerGui;
import dev.anhcraft.abm.system.handlers.MagazineHandler;
import dev.anhcraft.abm.utils.StringUtil;
import org.bukkit.entity.Player;
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
            List<Map.Entry<String, Long>> x = pd.get().getInventory().getStorage(ItemType.MAGAZINE).list();
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
}
