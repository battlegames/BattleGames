package dev.anhcraft.abm.api.gui;

import com.google.common.collect.Multimap;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface PaginationHandler {
    void pullData(Pagination pagination, Player player, Multimap<ItemStack, GuiListener<? extends SlotReport>> data);
}
