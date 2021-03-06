/*
 *
 *     Battle Minigame.
 *     Copyright (c) 2019 by anhcraft.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package dev.anhcraft.battle.api.arena.game;

import dev.anhcraft.battle.api.arena.Arena;
import dev.anhcraft.battle.api.events.game.GamePhaseChangeEvent;
import dev.anhcraft.battle.impl.Informative;
import dev.anhcraft.battle.impl.Resettable;
import dev.anhcraft.battle.utils.CustomDataContainer;
import dev.anhcraft.battle.utils.info.InfoHolder;
import dev.anhcraft.jvmkit.utils.Condition;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class Game extends CustomDataContainer implements Resettable, Informative {
    private final AtomicLong currentTime = new AtomicLong();
    private final Arena arena;
    protected int playerCount;
    private GamePhase phase = GamePhase.WAITING;

    public Game(@NotNull Arena arena) {
        Condition.notNull(arena, "Arena must be non-null");
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
    public AtomicLong getCurrentTime() {
        return currentTime;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    @NotNull
    public GamePhase getPhase() {
        return phase;
    }

    public void setPhase(@NotNull GamePhase phase) {
        Condition.notNull(phase, "Phase must be non-null");
        if (phase == this.phase) return;
        if (!Bukkit.isPrimaryThread()) {
            try {
                throw new IllegalStateException("Don't call #setPhase from another thread");
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            return;
        }
        Bukkit.getPluginManager().callEvent(new GamePhaseChangeEvent(this, this.phase, phase));
        this.phase = phase;
    }

    @Override
    public void reset() {
        playerCount = 0;
        currentTime.set(0);
        phase = GamePhase.WAITING;
    }

    @Override
    public void inform(@NotNull InfoHolder holder) {
        InfoHolder arenaHolder = new InfoHolder("arena_");
        arena.inform(arenaHolder);
        holder.inform("current_time", getCurrentTime().get())
                .inform("phase", phase.name().toLowerCase())
                .inform("player_count", getPlayerCount())
                .link(arenaHolder);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game localGame = (Game) o;
        return getCurrentTime().equals(localGame.getCurrentTime()) &&
                phase == localGame.phase &&
                arena.equals(localGame.arena);
    }

    @Override
    public int hashCode() {
        return Objects.hash(arena); // arena is unique for each game
    }
}
