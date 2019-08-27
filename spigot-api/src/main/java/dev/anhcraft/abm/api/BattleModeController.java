package dev.anhcraft.abm.api;

import dev.anhcraft.abm.api.game.Game;
import dev.anhcraft.abm.api.game.Mode;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.jetbrains.annotations.NotNull;

public interface BattleModeController {
    default boolean canJoin(Player player, Game game){
        return true;
    }

    void onJoin(Player player, Game game);

    void onEnd(Game game);

    default void onQuit(Player player, Game game){

    }

    default void onRespawn(PlayerRespawnEvent event, Game game){

    }

    default void onTask(Game game){

    }

    default void onDeath(PlayerDeathEvent event, Game game){

    }

    default void onSwapHand(PlayerSwapHandItemsEvent event, Game game){

    }

    @NotNull Mode getMode();
}
