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

package dev.anhcraft.battle.api.events.game;

import dev.anhcraft.battle.api.game.ABTeam;
import dev.anhcraft.battle.api.game.LocalGame;
import dev.anhcraft.battle.api.game.TeamFlag;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class FlagUpdateEvent extends GameEvent {
    public static final HandlerList handlers = new HandlerList();

    private Player player;
    private ABTeam team;
    private TeamFlag<ABTeam> flag;

    public FlagUpdateEvent(@NotNull LocalGame game, @NotNull Player player, @NotNull ABTeam team, @NotNull TeamFlag<ABTeam> flag) {
        super(game);
        this.player = player;
        this.team = team;
        this.flag = flag;
    }

    @Override
    @NotNull
    public LocalGame getGame() {
        return (LocalGame) game;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    @NotNull
    public TeamFlag<ABTeam> getFlag() {
        return flag;
    }

    @NotNull
    public ABTeam getTeam() {
        return team;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
