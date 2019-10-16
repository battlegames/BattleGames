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
import dev.anhcraft.abm.system.cleaners.WorkSession;
import org.jetbrains.annotations.NotNull;

public interface Work {
    String id();
    void handle(@NotNull BattlePlugin plugin, WorkSession session, @NotNull Arena arena);
}
