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

import dev.anhcraft.battle.api.misc.Resettable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class TeamManager<T extends Team> implements Resettable {
    private final Map<Player, T> PLAYER_MAP = new WeakHashMap<>();
    private final Map<T, Integer> PLAYER_COUNTER = new HashMap<>();
    private final Object LOCK = new Object();

    @Nullable
    public Collection<T> getTeams() {
        return PLAYER_COUNTER.keySet();
    }

    public void addPlayer(@NotNull Player player, @NotNull T team){
        synchronized (LOCK){
            if(PLAYER_MAP.putIfAbsent(player, team) == null)
                PLAYER_COUNTER.merge(team, 1, Integer::sum);
        }
    }

    public void addPlayers(@NotNull List<Player> players, @NotNull T team){
        synchronized (LOCK){
            int i = 0;
            for(Player p : players){
                if(PLAYER_MAP.putIfAbsent(p, team) == null) i++;
            }
            PLAYER_COUNTER.merge(team, i, Integer::sum);
        }
    }

    public T removePlayer(@NotNull Player player){
        synchronized (LOCK){
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

    @NotNull
    public Optional<T> nextEmptyTeam() {
        return PLAYER_COUNTER.entrySet().stream().filter(f -> f.getValue() == 0).findFirst().map(Map.Entry::getKey);
    }

    @NotNull
    public List<Player> getPlayers(@NotNull T team) {
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
    public Map<T, List<Player>> reverse(){
        return reverse(UnaryOperator.identity());
    }

    @NotNull
    public <R> Map<T, List<R>> reverse(@NotNull Function<Player, R> valueFunc){
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

    @Override
    public void reset() {
        PLAYER_MAP.clear();
        PLAYER_COUNTER.clear();
    }
}
