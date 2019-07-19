package dev.anhcraft.abm.system.workers;

import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.enums.GamePhase;
import dev.anhcraft.abm.api.events.GameEndEvent;
import dev.anhcraft.abm.api.ext.BattleComponent;
import org.bukkit.Bukkit;

public class GameTask extends BattleComponent implements Runnable {
    public GameTask(BattlePlugin plugin) {
        super(plugin);
    }

    @Override
    public void run() {
        plugin.gameManager.getGames().forEach(game -> {
            if(game.getPhase() == GamePhase.PLAYING &&
                    game.getArena().getMaxTime() <= game.getCurrentTime().getAndIncrement()) {
                game.setPhase(GamePhase.END);
                Bukkit.getPluginManager().callEvent(new GameEndEvent(game));
            }
        });
    }
}
