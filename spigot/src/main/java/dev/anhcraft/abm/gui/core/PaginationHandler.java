package dev.anhcraft.abm.gui.core;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface PaginationHandler {
    void getData(Player p, PlayerGui gui, BattlePagination bp, int fromIndex, int toIndex, List<ItemStack> items);
}
