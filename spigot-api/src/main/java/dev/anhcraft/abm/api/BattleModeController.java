package dev.anhcraft.abm.api;

import dev.anhcraft.abm.api.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;

public interface BattleModeController {
    default boolean canJoin(Player player, Game game){
        return true;
    }

    void onJoin(Player player, Game game);

    default void onQuit(Player player, Game game){

    }

    default void onRespawn(PlayerRespawnEvent event, Game game){

    }

    default void onTask(Game game){

    }
}
