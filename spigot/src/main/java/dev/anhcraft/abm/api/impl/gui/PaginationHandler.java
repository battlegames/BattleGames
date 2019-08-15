package dev.anhcraft.abm.api.impl.gui;

import com.google.common.collect.Multimap;
import dev.anhcraft.abm.api.ext.gui.GuiListener;
import dev.anhcraft.abm.api.objects.gui.Pagination;
import dev.anhcraft.abm.api.objects.gui.SlotReport;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface PaginationHandler {
    void pullData(Pagination pagination, Player player, Multimap<ItemStack, GuiListener<? extends SlotReport>> data);
}
