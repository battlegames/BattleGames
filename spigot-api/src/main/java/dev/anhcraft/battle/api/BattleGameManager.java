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
package dev.anhcraft.battle.api;

import dev.anhcraft.battle.api.game.Arena;
import dev.anhcraft.battle.api.game.Game;
import dev.anhcraft.battle.api.game.GamePlayer;
import dev.anhcraft.battle.api.game.LocalGame;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public interface BattleGameManager {
    /**
     * Gets the {@link GamePlayer} of the given player.
     * @param player the player
     * @return a unique {@link GamePlayer} represents an in-game player, or null if the player has not joined the game yet
     */
    @Nullable
    GamePlayer getGamePlayer(@NotNull Player player);

    /**
     * Gets the {@link Game} that the given player is currently playing on.
     * @param player the player
     * @return {@link Game} or null if the player has not joined the game yet
     */
    @Nullable
    LocalGame getGame(@NotNull Player player);

    /**
     * Gets the {@link Game} where a certain player is currently playing on.
     * @param playerId the id of the player
     * @return {@link Game} or null if the player has not joined the game yet
     */
    @Nullable
    LocalGame getGame(@NotNull UUID playerId);

    /**
     * Gets the {@link Game} which happens in the given arena.
     * @param arena the arena
     * @return {@link Arena} or null if the arena is inactive
     */
    @Nullable
    Game getGame(@NotNull Arena arena);

    /**
     * Makes the given players joins the arena.
     * @param player the player
     * @param arena the arena
     * @return {@link Game} or null if the action is failed
     */
    @Nullable
    default Game join(@NotNull Player player, @NotNull Arena arena){
        return join(player, arena, false);
    }

    /**
     * Makes the given players joins the arena.
     * @param player the player
     * @param arena the arena
     * @param forceLocal forces the game to be created as {@link LocalGame}
     * @return {@link Game} or null if the action is failed
     */
    @Nullable
    Game join(@NotNull Player player, @NotNull Arena arena, boolean forceLocal);

    /**
     * Makes the given players joins the arena.
     * <br>
     * This method will not send any messages to the player like {@link #join(Player, Arena)}
     * @param player the player
     * @param arena the arena
     * @return {@link Game} or null if the action is failed
     */
    @Nullable
    default Game forceJoin(@NotNull Player player, @NotNull Arena arena) {
        return forceJoin(player, arena, false);
    }

    /**
     * Makes the given players joins the arena.
     * <br>
     * This method will not send any messages to the player like {@link #join(Player, Arena, boolean)}
     * @param player the player
     * @param arena the arena
     * @param forceLocal forces the game to be created as {@link LocalGame}
     * @return {@link Game} or null if the action is failed
     */
    @Nullable
    Game forceJoin(@NotNull Player player, @NotNull Arena arena, boolean forceLocal);

    /**
     * Makes the given player quit the game.
     * @param player the player
     * @return the status of the action, {@code true} if success or {@code false} if not
     */
    boolean quit(@NotNull Player player);

    /**
     * Destroys the given game.
     * @param game the game
     */
    void destroy(@NotNull Game game);

    /**
     * Lists all active games.
     * @return an immutable list of games.
     */
    @NotNull
    List<Game> listGames();

    /**
     * Lists all active games and gets them.
     * @param consumer the consumer
     */
    void listGames(@NotNull Consumer<Game> consumer);
}
