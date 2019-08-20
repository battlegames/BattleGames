package dev.anhcraft.abm.api.game;

import dev.anhcraft.abm.api.misc.Resettable;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;

public class GamePlayer implements Resettable {
    private final AtomicInteger headshotCounter = new AtomicInteger();
    private final AtomicInteger killCounter = new AtomicInteger();
    private final AtomicInteger deathCounter = new AtomicInteger();
    private final AtomicInteger assistCounter = new AtomicInteger();
    private Player player;
    private boolean spectator;
    private boolean winner;
    private ItemStack[] backupInventory;

    public GamePlayer(@NotNull Player player) {
        Validate.notNull(player, "Player must be non-null");
        this.player = player;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    public boolean isSpectator() {
        return spectator;
    }

    public void setSpectator(boolean spectator) {
        this.spectator = spectator;
    }

    public boolean isWinner() {
        return winner;
    }

    public void setWinner(boolean winner) {
        this.winner = winner;
    }

    @NotNull
    public AtomicInteger getHeadshotCounter() {
        return headshotCounter;
    }

    @NotNull
    public AtomicInteger getAssistCounter() {
        return assistCounter;
    }

    @NotNull
    public AtomicInteger getDeathCounter() {
        return deathCounter;
    }

    @NotNull
    public AtomicInteger getKillCounter() {
        return killCounter;
    }

    @Nullable
    public ItemStack[] getBackupInventory() {
        return backupInventory;
    }

    public void setBackupInventory(@Nullable ItemStack[] backupInventory) {
        this.backupInventory = backupInventory;
    }

    @Override
    public void reset() {
        spectator = false;
        winner = false;
        killCounter.set(0);
        deathCounter.set(0);
        backupInventory = null;
    }
}
