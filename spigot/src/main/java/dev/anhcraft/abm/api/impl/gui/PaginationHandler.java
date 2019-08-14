package dev.anhcraft.abm.api.impl.gui;

import com.google.common.collect.LinkedHashMultimap;
import dev.anhcraft.abm.api.ext.gui.GuiListener;
import dev.anhcraft.abm.api.objects.gui.SlotReport;
import dev.anhcraft.abm.api.objects.gui.Pagination;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface PaginationHandler {
    void pullData(Pagination pagination, Player player, LinkedHashMultimap<ItemStack, GuiListener<? extends SlotReport>> data);
}
