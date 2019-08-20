package dev.anhcraft.abm.api.events;

import dev.anhcraft.abm.api.game.GamePhase;
import dev.anhcraft.abm.api.game.Game;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GamePhaseChangeEvent extends Event {
    public static final HandlerList handlers = new HandlerList();
    private Game game;
    private GamePhase oldPhase;
    private GamePhase newPhase;

    public GamePhaseChangeEvent(Game game, GamePhase oldPhase, GamePhase newPhase) {
        this.game = game;
        this.oldPhase = oldPhase;
        this.newPhase = newPhase;
    }

    public GamePhase getOldPhase() {
        return oldPhase;
    }

    public GamePhase getNewPhase() {
        return newPhase;
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
