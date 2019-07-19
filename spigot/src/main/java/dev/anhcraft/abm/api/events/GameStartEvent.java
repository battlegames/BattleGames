package dev.anhcraft.abm.api.events;

import dev.anhcraft.abm.api.objects.Game;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GameStartEvent extends Event {
    public static final HandlerList handlers = new HandlerList();
    private Game game;

    public GameStartEvent(Game game) {
        this.game = game;
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
