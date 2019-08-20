package dev.anhcraft.abm.gui;

import com.google.common.collect.Multimap;
import dev.anhcraft.abm.api.APIProvider;
import dev.anhcraft.abm.api.BattleAPI;
import dev.anhcraft.abm.api.gui.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ArenaChooserHandler extends GuiHandler implements PaginationHandler {
    @Override
    public void pullData(Pagination pagination, Player player, Multimap<ItemStack, GuiListener<? extends SlotReport>> data) {
        BattleAPI api = APIProvider.get();
        api.listArenas().forEach(arena -> {
            data.put(arena.getIcon().build(), new GuiListener<SlotClickReport>(SlotClickReport.class) {
                @Override
                public void call(SlotClickReport event) {
                    event.getPlayer().closeInventory();
                    api.getGameManager().join(event.getPlayer(), arena);
                }
            });
        });
    }
}
