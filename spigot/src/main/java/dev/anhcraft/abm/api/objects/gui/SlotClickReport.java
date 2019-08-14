package dev.anhcraft.abm.api.objects.gui;

import dev.anhcraft.jvmkit.utils.Condition;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public class SlotClickReport extends SlotReport {
    private InventoryClickEvent clickEvent;

    public SlotClickReport(@NotNull Player player, @NotNull BattleGui gui, @NotNull BattleGuiSlot slot, @NotNull InventoryClickEvent clickEvent) {
        super(player, gui, slot);
        Condition.argNotNull("clickEvent", clickEvent);
        this.clickEvent = clickEvent;
    }

    @NotNull
    public InventoryClickEvent getClickEvent() {
        return clickEvent;
    }
}
