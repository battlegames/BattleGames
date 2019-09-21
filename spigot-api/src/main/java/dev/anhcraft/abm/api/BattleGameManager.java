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
package dev.anhcraft.abm.api;

import dev.anhcraft.abm.api.game.Arena;
import dev.anhcraft.abm.api.game.Game;
import dev.anhcraft.abm.api.game.GamePlayer;
import dev.anhcraft.abm.api.game.LocalGame;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;

public interface BattleGameManager {
    @Nullable GamePlayer getGamePlayer(@NotNull Player player);
    @Nullable LocalGame getGame(@NotNull Player player);
    @Nullable LocalGame getGame(@NotNull UUID playerId);
    @Nullable Game getGame(@NotNull Arena arena);

    @Nullable
    default Game join(@NotNull Player player, @NotNull Arena arena){
        return join(player, arena, false);
    }

    @Nullable
    Game join(@NotNull Player player, @NotNull Arena arena, boolean forceLocal);

    @Nullable
    default Game forceJoin(@NotNull Player player, @NotNull Arena arena) {
        return forceJoin(player, arena, false);
    }

    @Nullable
    Game forceJoin(@NotNull Player player, @NotNull Arena arena, boolean forceLocal);

    boolean quit(@NotNull Player player);
    void destroy(@NotNull Game game);
    @NotNull Collection<Game> listGames();
    void listGames(@NotNull Consumer<Game> consumer);
}
