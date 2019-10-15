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
import org.jetbrains.annotations.NotNull;

public class RollbackWork implements Work {
    @Override
    public void handle(@NotNull BattlePlugin plugin, @NotNull Arena arena) {
        if(arena.getRollback() == null) return;
        Rollback rollback = arena.getRollback();
        if(rollback.getProvider() == Rollback.Provider.SLIME_WORLD && plugin.hasSlimeWorldManagerSupport()){
            for(String w : rollback.getWorlds()){
                switch (plugin.SWMIntegration.isReadOnly(w)){
                    case -1: {
                        plugin.getLogger().warning("[Rollback/SWM] World not found: "+w);
                    }
                    case 0: {
                        plugin.getLogger().warning("[Rollback/SWM] World is not in read-only mode: "+w);
                    }
                    case 1: {
                        plugin.getLogger().info("[Rollback/SWM] Reloading world: "+w);
                        plugin.SWMIntegration.reloadWorld(w);
                    }
                }
            }
        }
    }
}
