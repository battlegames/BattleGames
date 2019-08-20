package dev.anhcraft.abm.api.impl;

import dev.anhcraft.abm.api.objects.Arena;
import dev.anhcraft.abm.api.objects.Game;
import dev.anhcraft.abm.api.objects.GamePlayer;
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
