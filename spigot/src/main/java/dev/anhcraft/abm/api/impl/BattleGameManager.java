package dev.anhcraft.abm.api.impl;

import dev.anhcraft.abm.api.objects.Arena;
import dev.anhcraft.abm.api.objects.Game;
import dev.anhcraft.abm.api.objects.GamePlayer;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Optional;

public interface BattleGameManager {
    Optional<GamePlayer> getGamePlayer(Player player);
    Optional<Game> getGame(Player player);
    Optional<Game> getGame(Arena arena);
    boolean join(Player player, Arena arena);
    boolean forceJoin(Player player, Arena arena);
    boolean quit(Player player);
    void destroy(Game game);
    Collection<Game> getGames();
}
