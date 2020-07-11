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

package dev.anhcraft.battle.api.arena.mode;

import dev.anhcraft.battle.api.arena.game.LocalGame;
import dev.anhcraft.battle.api.arena.team.MRTeam;
import dev.anhcraft.battle.api.arena.team.TeamManager;
import org.jetbrains.annotations.Nullable;

public interface IMobRescue extends IMode {
    @Nullable
    TeamManager<MRTeam> getTeamManager(@Nullable LocalGame game);
}
