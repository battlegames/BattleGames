package dev.anhcraft.abm.api.enums;

/**
 * Different phases in the game
 */
public enum GamePhase {
    /**
     * In this phase, players join the room and wait for others until it is ready.
     */
    WAITING,
    /**
     * Players are playing during this phase.<br>
     * The parameter {@code playable} isn't totally correct since some modes prevent you from joining it in this phase.
     */
    PLAYING,
    /**
     * The game is finished now.<br>
     */
    END,
    /**
     * The server puts the room into the queue and cleans it.
     */
    CLEANING
}
