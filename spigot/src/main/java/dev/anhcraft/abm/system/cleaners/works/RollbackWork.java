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

package dev.anhcraft.abm.system.cleaners.works;

import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.game.Arena;
import dev.anhcraft.abm.api.misc.Rollback;
import dev.anhcraft.abm.system.cleaners.WorkSession;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CountDownLatch;

public class RollbackWork implements Work {
    @Override
    public String id() {
        return "rollback";
    }

    @Override
    public void handle(@NotNull BattlePlugin plugin, WorkSession session, @NotNull Arena arena) {
        if(arena.getRollback() == null) return;
        Rollback rollback = arena.getRollback();
        if(rollback.getProvider() == Rollback.Provider.SLIME_WORLD && plugin.hasSlimeWorldManagerSupport()){
            for(String w : rollback.getWorlds()){
                CountDownLatch countDownLatch = new CountDownLatch(1);
                switch (plugin.SWMIntegration.isReadOnly(w)) {
                    case -1: {
                        plugin.getLogger().warning("[Rollback/SWM] World not found: " + w);
                        continue;
                    }
                    case 0: {
                        plugin.getLogger().warning("[Rollback/SWM] World is not in read-only mode: " + w);
                        continue;
                    }
                    case 1: {
                        plugin.getLogger().info("[Rollback/SWM] Reloading world: " + w);
                        plugin.SWMIntegration.reloadWorld(countDownLatch, w);
                    }
                }
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        session.done(this);
    }
}
