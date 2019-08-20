package dev.anhcraft.abm.api.gui;

import dev.anhcraft.jvmkit.utils.Condition;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

public class SlotCancelReport extends SlotReport {
    private Cancellable cancelEvent;

    public SlotCancelReport(@NotNull Player player, @NotNull BattleGui gui, @NotNull BattleGuiSlot slot, @NotNull Cancellable clickEvent) {
        super(player, gui, slot);
        Condition.argNotNull("clickEvent", clickEvent);
        this.cancelEvent = clickEvent;
    }

    @NotNull
    public Cancellable getCancelEvent() {
        return cancelEvent;
    }
}
