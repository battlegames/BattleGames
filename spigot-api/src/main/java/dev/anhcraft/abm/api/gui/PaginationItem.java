package dev.anhcraft.abm.api.gui;

import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Collections;

public class PaginationItem {
    private ItemStack itemStack;
    private Collection<GuiListener<? extends SlotReport>> guiListeners;

    public PaginationItem(ItemStack itemStack, GuiListener<? extends SlotReport> guiListener) {
        this.itemStack = itemStack;
        this.guiListeners = Collections.singleton(guiListener);
    }

    public PaginationItem(ItemStack itemStack, Collection<GuiListener<? extends SlotReport>> guiListeners) {
        this.itemStack = itemStack;
        this.guiListeners = guiListeners;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public Collection<GuiListener<? extends SlotReport>> getGuiListeners() {
        return guiListeners;
    }
}
