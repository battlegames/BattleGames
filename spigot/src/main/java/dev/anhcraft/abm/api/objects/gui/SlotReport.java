package dev.anhcraft.abm.api.objects.gui;

import dev.anhcraft.jvmkit.utils.Condition;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SlotReport extends GuiReport {
    private BattleGuiSlot slot;

    public SlotReport(@NotNull Player player, @NotNull BattleGui gui, @NotNull BattleGuiSlot slot) {
        super(player, gui);
        Condition.argNotNull("slot", slot);
        this.slot = slot;
    }

    @NotNull
    public BattleGuiSlot getSlot() {
        return slot;
    }
}
