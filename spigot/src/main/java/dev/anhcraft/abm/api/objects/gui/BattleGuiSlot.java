package dev.anhcraft.abm.api.objects.gui;

import dev.anhcraft.abm.api.ext.gui.GuiListener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class BattleGuiSlot {
    private int index;
    private GuiSlot slot;
    private Collection<GuiListener<? extends GuiReport>> events;
    private ItemStack cachedItem;

    public BattleGuiSlot(int index, GuiSlot slot, Collection<GuiListener<? extends GuiReport>> events) {
        this.index = index;
        this.slot = slot;
        this.events = events;
    }

    public GuiSlot getSlot() {
        return slot;
    }

    public Collection<GuiListener<? extends GuiReport>> getEvents() {
        return events;
    }

    @Nullable
    public ItemStack getCachedItem() {
        return cachedItem;
    }

    public void setCachedItem(@Nullable ItemStack cachedItem) {
        this.cachedItem = cachedItem;
    }

    public int getIndex() {
        return index;
    }
}
