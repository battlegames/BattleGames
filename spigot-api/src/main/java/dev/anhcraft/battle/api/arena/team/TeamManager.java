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
package dev.anhcraft.battle.api.arena.team;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import dev.anhcraft.battle.impl.Resettable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class TeamManager<T extends Team> implements Resettable {
    private final Map<Player, T> PLAYER_MAP = new WeakHashMap<>();
    private final Map<T, Integer> PLAYER_COUNTER = new HashMap<>();
    private final Object LOCK = new Object();

    @NotNull
    public Collection<T> getTeams() {
        return PLAYER_COUNTER.keySet();
    }

    @NotNull
    public Optional<T> findTeam(Predicate<T> teamPredicate) {
        return PLAYER_COUNTER.keySet().stream().filter(teamPredicate).findFirst();
    }

    public void initTeam(@Nullable T team) {
        PLAYER_COUNTER.put(team, 0);
    }

    public void addPlayer(@NotNull Player player, @NotNull T team) {
        synchronized (LOCK) {
            if (PLAYER_MAP.putIfAbsent(player, team) == null)
                PLAYER_COUNTER.merge(team, 1, Integer::sum);
        }
    }

    public void addPlayers(@NotNull List<Player> players, @NotNull T team) {
        synchronized (LOCK) {
            int i = 0;
            for (Player p : players) {
                if (PLAYER_MAP.putIfAbsent(p, team) == null) i++;
            }
            PLAYER_COUNTER.merge(team, i, Integer::sum);
        }
    }

    @Nullable
    public T removePlayer(@NotNull Player player) {
        synchronized (LOCK) {
            T last = PLAYER_MAP.remove(player);
            PLAYER_COUNTER.merge(last, -1, Integer::sum);
            return last;
        }
    }

    @Nullable
    public T getTeam(@Nullable Player player) {
        return PLAYER_MAP.get(player);
    }

    public int countPlayers(@Nullable T team) {
        return PLAYER_COUNTER.getOrDefault(team, 0);
    }

    public long countPresentTeams() {
        return PLAYER_COUNTER.entrySet().stream().filter(f -> f.getValue() > 0).count();
    }

    @NotNull
    public Optional<T> nextEmptyTeam() {
        return PLAYER_COUNTER.entrySet().stream().filter(f -> f.getValue() == 0).findFirst().map(Map.Entry::getKey);
    }

    @NotNull
    public Optional<T> nextAvailableTeam(int maxTeammates) {
        return PLAYER_COUNTER.entrySet().stream()
                .filter(f -> f.getValue() >= 0 && f.getValue() < maxTeammates)
                .findFirst().map(Map.Entry::getKey);
    }

    @NotNull
    public List<Player> getPlayers(@NotNull T team) {
        Preconditions.checkNotNull(team);
        return PLAYER_MAP.entrySet().stream()
                .filter(p -> p.getValue() == team)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @NotNull
    public Set<Map.Entry<Player, T>> getPlayerTeam() {
        return Collections.unmodifiableSet(PLAYER_MAP.entrySet());
    }

    @NotNull
    public Map<T, List<Player>> reverse() {
        return reverse(UnaryOperator.identity());
    }

    @NotNull
    public <R> Map<T, List<R>> reverse(@NotNull Function<Player, R> valueFunc) {
        synchronized (LOCK) {
            return PLAYER_MAP.entrySet()
                    .stream().collect(Collectors.groupingBy(Map.Entry::getValue)).values()
                    .stream().collect(Collectors.toMap(
                            item -> item.get(0).getValue(),
                            item -> new ArrayList<>(item.stream().map(Map.Entry::getKey)
                                    .map(valueFunc).collect(Collectors.toList())))
                    );
        }
    }

    @NotNull
    public Multimap<T, Player> toMultimap() {
        return toMultimap(UnaryOperator.identity());
    }

    @NotNull
    public <R> Multimap<T, R> toMultimap(@NotNull Function<Player, R> valueFunc) {
        synchronized (LOCK) {
            return PLAYER_MAP.entrySet().stream().collect(Multimaps.toMultimap(Map.Entry::getValue, playerTEntry -> valueFunc.apply(playerTEntry.getKey()), (Supplier<Multimap<T, R>>) HashMultimap::create));
        }
    }

    @Override
    public void reset() {
        PLAYER_MAP.clear();
        PLAYER_COUNTER.clear();
    }
}
