package dev.anhcraft.abm.tasks;

import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.enums.GamePhase;
import dev.anhcraft.abm.api.ext.BattleComponent;
import dev.anhcraft.abm.system.controllers.ModeController;

public class GameTask extends BattleComponent implements Runnable {
    public GameTask(BattlePlugin plugin) {
        super(plugin);
    }

    @Override
    public void run() {
        plugin.gameManager.getGames().forEach(game -> {
            ModeController mc = game.getMode().getController();
            if(mc != null) mc.onTask(game);

            if(game.getPhase() == GamePhase.PLAYING &&
                    game.getArena().getMaxTime() <= game.getCurrentTime().getAndIncrement()) {
                game.end();
            }
        });
    }
}
