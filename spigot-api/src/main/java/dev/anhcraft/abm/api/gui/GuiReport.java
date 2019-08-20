package dev.anhcraft.abm.api.gui;

import dev.anhcraft.jvmkit.utils.Condition;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GuiReport {
    private Player player;
    private BattleGui gui;

    public GuiReport(@NotNull Player player, @NotNull BattleGui gui) {
        Condition.argNotNull("player", player);
        Condition.argNotNull("gui", gui);
        this.player = player;
        this.gui = gui;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    @NotNull
    public BattleGui getGui() {
        return gui;
    }
}
