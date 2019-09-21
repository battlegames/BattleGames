/*
 *
 *     Battle Minigame.
 *     Copyright (c) 2019 by anhcraft.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */
package dev.anhcraft.abm.tasks;

import dev.anhcraft.abm.BattleComponent;
import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.BattleModeController;
import dev.anhcraft.abm.api.game.GamePhase;
import dev.anhcraft.abm.api.game.LocalGame;
import dev.anhcraft.abm.api.game.RemoteGame;

public class GameTask extends BattleComponent implements Runnable {
    public GameTask(BattlePlugin plugin) {
        super(plugin);
    }

    @Override
    public void run() {
        plugin.gameManager.getGames().forEach(g -> {
            if(g instanceof LocalGame) {
                LocalGame game = (LocalGame) g;
                BattleModeController mc = game.getMode().getController();
                if (mc != null) mc.onTask(game);

                if (game.getPhase() == GamePhase.PLAYING && game.getArena().getMaxTime() <= game.getCurrentTime().getAndIncrement()) {
                    game.end();
                } else if(plugin.hasBungeecordSupport() && game.getBungeeSyncTick().incrementAndGet() == 60) {
                    game.getBungeeSyncTick().set(0);
                    plugin.bungeeMessenger.sendGameUpdate(game);
                }
            } else if(g instanceof RemoteGame) {
                RemoteGame game = (RemoteGame) g;
                if (game.getPhase() == GamePhase.PLAYING && game.getArena().getMaxTime() <= game.getCurrentTime().getAndIncrement()) {
                    plugin.gameManager.destroy(game);
                }
            }
        });
    }
}
