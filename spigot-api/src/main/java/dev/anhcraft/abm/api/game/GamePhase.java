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
package dev.anhcraft.abm.api.game;

/**
 * Different phases in the game
 */
public enum GamePhase {
    /**
     * In this phase, players join the room and wait for others until it is ready.
     */
    WAITING,
    /**
     * Players are playing during this phase.<br>
     * The parameter {@code playable} isn't totally correct since some modes prevent you from joining it in this phase.
     */
    PLAYING,
    /**
     * The game is finished now.<br>
     */
    END,
    /**
     * The server puts the room into the queue and cleans it.
     */
    CLEANING
}
