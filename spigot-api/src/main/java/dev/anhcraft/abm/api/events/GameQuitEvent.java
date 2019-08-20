package dev.anhcraft.abm.api.events;

import dev.anhcraft.abm.api.game.Game;
import dev.anhcraft.abm.api.game.GamePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GameQuitEvent extends Event {
    public static final HandlerList handlers = new HandlerList();

    private GamePlayer gamePlayer;
    private Game game;

    public GameQuitEvent(GamePlayer gamePlayer, Game game) {
        this.gamePlayer = gamePlayer;
        this.game = game;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
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
