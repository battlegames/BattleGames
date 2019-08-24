package dev.anhcraft.abm.api.game;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import dev.anhcraft.abm.api.misc.DamageReport;
import dev.anhcraft.abm.api.events.GameEndEvent;
import dev.anhcraft.abm.api.events.GamePhaseChangeEvent;
import dev.anhcraft.abm.api.misc.Resettable;
import dev.anhcraft.abm.api.misc.info.InfoHolder;
import dev.anhcraft.abm.api.misc.info.Informative;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class Game implements Resettable, Informative {
    private final Multimap<Player, DamageReport> damageReports = HashMultimap.create();
    private final Map<Player, GamePlayer> players = new ConcurrentHashMap<>();
    private final AtomicLong currentTime = new AtomicLong();
    private GamePhase phase = GamePhase.WAITING;
    private Arena arena;

    public Game(@NotNull Arena arena) {
        Validate.notNull(arena, "Arena must be non-null");
        this.arena = arena;
    }

    @NotNull
    public Arena getArena() {
        return arena;
    }

    @NotNull
    public Mode getMode() {
        return arena.getMode();
    }

    @NotNull
    public GamePhase getPhase() {
        return phase;
    }

    public void setPhase(@NotNull GamePhase phase) {
        Validate.notNull(phase, "Phase must be non-null");
        if(phase == this.phase) return;
        if(!Bukkit.isPrimaryThread()){
            try {
                throw new Exception("Can't use #setPhase from another thread");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        this.phase = phase;
        Bukkit.getPluginManager().callEvent(new GamePhaseChangeEvent(this, this.phase, phase));
    }

    @NotNull
    public AtomicLong getCurrentTime() {
        return currentTime;
    }

    @Nullable
    public GamePlayer getPlayer(@Nullable Player player) {
        return players.get(player);
    }

    @NotNull
    public Map<Player, GamePlayer> getPlayers() {
        return players;
    }

    public int countPlayers() {
        return players.size();
    }

    public Multimap<Player, DamageReport> getDamageReports() {
        return damageReports;
    }

    public synchronized void end() {
        if(phase == GamePhase.END) return;
        phase = GamePhase.END;
        Bukkit.getPluginManager().callEvent(new GameEndEvent(this));
    }

    @Override
    public void reset() {
        players.clear();
        phase = GamePhase.WAITING;
        currentTime.set(0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return arena.equals(game.arena);
    }

    @Override
    public int hashCode() {
        return Objects.hash(arena);
    }

    @Override
    public void inform(@NotNull InfoHolder holder) {
        InfoHolder arenaHolder = new InfoHolder("arena_");
        arena.inform(arenaHolder);
        holder.inform("current_time", currentTime.get())
                .inform("phase", phase.name().toLowerCase())
                .inform("player_count", players.size())
                .link(arenaHolder);
    }
}
