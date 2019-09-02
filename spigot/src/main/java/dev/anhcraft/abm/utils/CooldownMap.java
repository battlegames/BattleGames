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
package dev.anhcraft.abm.utils;

import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CooldownMap extends HashMap<Player, Long> {
    public CooldownMap(){}

    public CooldownMap(List<Player> players){
        players.forEach(this::resetTime);
    }

    public CooldownMap(Player... players){
        Arrays.stream(players).forEach(this::resetTime);
    }

    public void resetTime(Player player){
        put(player, System.currentTimeMillis()/50);
    }

    public void setTime(Player player, long date){
        put(player, date);
    }

    public long getTime(Player player){
        return getOrDefault(player, System.currentTimeMillis()/50);
    }

    public void extendTime(Player player, long duration){
        put(player, getTime(player) + duration);
    }

    public void contractTime(Player player, long duration){
        put(player, getTime(player) - duration);
    }

    public long elapsedTime(Player player){
        return System.currentTimeMillis()/50 - getTime(player);
    }

    public boolean isPassed(Player player, long duration){
        return elapsedTime(player) > duration;
    }
    
    public long remainingTime(Player player, long duration){
        return duration - elapsedTime(player);
    }
}
