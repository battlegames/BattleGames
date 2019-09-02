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
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

public interface BattleGameManager {
    @NotNull Optional<GamePlayer> getGamePlayer(@NotNull Player player);
    @NotNull Optional<Game> getGame(@NotNull Player player);
    @NotNull Optional<Game> getGame(@NotNull Arena arena);
    boolean join(@NotNull Player player, @NotNull Arena arena);
    boolean forceJoin(@NotNull Player player, @NotNull Arena arena);
    boolean quit(@NotNull Player player);
    void destroy(@NotNull Game game);
    @NotNull Collection<Game> getGames();
}
