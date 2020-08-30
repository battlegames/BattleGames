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

import dev.anhcraft.battle.api.arena.game.Game;
import dev.anhcraft.battle.api.arena.game.GamePlayer;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GameJoinEvent extends GameEvent {
    public static final HandlerList handlers = new HandlerList();
    private final GamePlayer gamePlayer;

    public GameJoinEvent(@NotNull Game game, @NotNull GamePlayer gamePlayer) {
        super(game);
        this.gamePlayer = gamePlayer;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @NotNull
    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
