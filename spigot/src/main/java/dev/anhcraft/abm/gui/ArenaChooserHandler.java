package dev.anhcraft.abm.gui;

import com.google.common.collect.LinkedHashMultimap;
import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.ext.gui.GuiListener;
import dev.anhcraft.abm.api.objects.gui.SlotClickReport;
import dev.anhcraft.abm.api.objects.gui.SlotReport;
import dev.anhcraft.abm.api.impl.gui.PaginationHandler;
import dev.anhcraft.abm.api.ext.gui.GuiHandler;
import dev.anhcraft.abm.api.objects.gui.Pagination;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ArenaChooserHandler extends GuiHandler implements PaginationHandler {
    public ArenaChooserHandler(BattlePlugin plugin) {
        super(plugin);
    }

    @Override
    public void pullData(Pagination pagination, Player player, LinkedHashMultimap<ItemStack, GuiListener<? extends SlotReport>> data) {
        plugin.listArenas().forEach(arena -> {
            data.put(arena.getIcon().build(), new GuiListener<SlotClickReport>(SlotClickReport.class) {
                @Override
                public void call(SlotClickReport event) {
                    event.getPlayer().closeInventory();
                    plugin.gameManager.join(event.getPlayer(), arena);
                }
            });
        });
    }
}
