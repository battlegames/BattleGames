package dev.anhcraft.abm;

public abstract class BattleComponent {
    protected BattlePlugin plugin;

    protected BattleComponent(BattlePlugin plugin) {
        this.plugin = plugin;
    }
}
