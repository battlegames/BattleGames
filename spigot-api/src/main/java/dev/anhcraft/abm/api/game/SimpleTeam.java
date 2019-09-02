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
package dev.anhcraft.abm.api.game;

import dev.anhcraft.abm.api.misc.Resettable;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class SimpleTeam<T extends Team> implements Resettable {
    private final Map<Player, T> PLAYER_MAP = new HashMap<>();
    private final Map<T, Integer> PLAYER_COUNTER = new HashMap<>();
    private final Object LOCK = new Object();

    public void addPlayer(Player player, T team){
        synchronized (LOCK){
            if(PLAYER_MAP.putIfAbsent(player, team) == null)
                PLAYER_COUNTER.merge(team, 1, Integer::sum);
        }
    }

    public void addPlayers(List<Player> players, T team){
        synchronized (LOCK){
            int i = 0;
            for(Player p : players){
                if(PLAYER_MAP.putIfAbsent(p, team) == null) i++;
            }
            PLAYER_COUNTER.merge(team, i, Integer::sum);
        }
    }

    public void removePlayer(Player player){
        synchronized (LOCK){
            PLAYER_COUNTER.merge(PLAYER_MAP.remove(player), -1, Integer::sum);
        }
    }

    public T getTeam(Player player) {
        synchronized (LOCK) {
            return PLAYER_MAP.get(player);
        }
    }

    public int countPlayers(T team) {
        synchronized (LOCK) {
            return PLAYER_COUNTER.getOrDefault(team, 0);
        }
    }

    public Map<T, List<Player>> reverse(){
        return reverse(UnaryOperator.identity());
    }

    public <R> Map<T, List<R>> reverse(Function<Player, R> valueFunc){
        return PLAYER_MAP.entrySet()
                .stream().collect(Collectors.groupingBy(Map.Entry::getValue)).values()
                .stream().collect(Collectors.toMap(
                        item -> item.get(0).getValue(),
                        item -> new ArrayList<>(item.stream().map(Map.Entry::getKey)
                                .map(valueFunc).collect(Collectors.toList())))
                );
    }

    @Override
    public void reset() {
        PLAYER_MAP.clear();
        PLAYER_COUNTER.clear();
    }
}
