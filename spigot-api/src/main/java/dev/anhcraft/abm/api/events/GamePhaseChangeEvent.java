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
package dev.anhcraft.abm.api.events;

import dev.anhcraft.abm.api.game.GamePhase;
import dev.anhcraft.abm.api.game.Game;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GamePhaseChangeEvent extends Event {
    public static final HandlerList handlers = new HandlerList();
    private Game game;
    private GamePhase oldPhase;
    private GamePhase newPhase;

    public GamePhaseChangeEvent(Game game, GamePhase oldPhase, GamePhase newPhase) {
        this.game = game;
        this.oldPhase = oldPhase;
        this.newPhase = newPhase;
    }

    public GamePhase getOldPhase() {
        return oldPhase;
    }

    public GamePhase getNewPhase() {
        return newPhase;
    }

    public Game getGame() {
        return game;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
