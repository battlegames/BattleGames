package dev.anhcraft.abm.api.ext;

import dev.anhcraft.abm.BattlePlugin;

public abstract class BattleComponent {
    protected BattlePlugin plugin;

    protected BattleComponent(BattlePlugin plugin) {
        this.plugin = plugin;
    }
}
