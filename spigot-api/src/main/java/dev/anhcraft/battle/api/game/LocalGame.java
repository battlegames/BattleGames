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
package dev.anhcraft.battle.api.game;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import dev.anhcraft.battle.api.events.GameEndEvent;
import dev.anhcraft.battle.api.misc.DamageReport;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class LocalGame extends Game {
    private final Multimap<Player, DamageReport> damageReports = Multimaps.synchronizedMultimap(HashMultimap.create());
    private final Map<Player, GamePlayer> players = new ConcurrentHashMap<>();
    private final Multimap<String, Player> downstreamServers = Multimaps.synchronizedMultimap(HashMultimap.create());
    private final AtomicInteger bungeeSyncTick = new AtomicInteger();

    public LocalGame(@NotNull Arena arena) {
        super(arena);
    }

    @NotNull
    public Multimap<Player, DamageReport> getDamageReports() {
        return damageReports;
    }

    @NotNull
    public Map<Player, GamePlayer> getPlayers() {
        return players;
    }

    @Override
    public int getPlayerCount() {
        return players.size();
    }

    @Nullable
    public GamePlayer getPlayer(@Nullable Player player) {
        return players.get(player);
    }

    @NotNull
    public Multimap<String, Player> getDownstreamServers() {
        return downstreamServers;
    }

    @NotNull
    public AtomicInteger getBungeeSyncTick() {
        return bungeeSyncTick;
    }

    public void end() {
        if(getPhase() == GamePhase.END) return;
        if(!Bukkit.isPrimaryThread()){
            try {
                throw new IllegalStateException("Don't call #end from another thread");
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            return;
        }
        Bukkit.getPluginManager().callEvent(new GameEndEvent(this));
        setPhase(GamePhase.END);
        getArena().getMode().getController(c -> c.onEnd(this));
    }

    @Override
    public void reset() {
        players.clear();
        downstreamServers.clear();
        bungeeSyncTick.set(0);
        super.reset();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocalGame localGame = (LocalGame) o;
        return damageReports.equals(localGame.damageReports) &&
                players.equals(localGame.players) &&
                getCurrentTime().equals(localGame.getCurrentTime()) &&
                getPhase() == localGame.getPhase() &&
                getArena().equals(localGame.getArena()) &&
                downstreamServers.equals(localGame.downstreamServers) &&
                bungeeSyncTick.equals(localGame.bungeeSyncTick);
    }
}
