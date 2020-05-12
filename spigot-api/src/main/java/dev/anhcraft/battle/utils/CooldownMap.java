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
package dev.anhcraft.battle.utils;

import dev.anhcraft.jvmkit.utils.Condition;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CooldownMap extends HashMap<UUID, Long> {
    public CooldownMap(){}

    public CooldownMap(@NotNull List<Player> players){
        Condition.notNull(players);
        players.stream().map(Entity::getUniqueId).forEach(this::resetTime);
    }

    public CooldownMap(Player... players){
        Condition.notNull(players);
        Arrays.stream(players).filter(Objects::nonNull).map(Entity::getUniqueId).forEach(this::resetTime);
    }

    public void resetTime(Player player){
        put(player.getUniqueId(), System.currentTimeMillis()/50);
    }

    public void setTime(Player player, long date){
        put(player.getUniqueId(), date);
    }

    public long getTime(Player player){
        return getOrDefault(player, System.currentTimeMillis()/50);
    }

    public void extendTime(Player player, long duration){
        put(player.getUniqueId(), getTime(player) + duration);
    }

    public void contractTime(Player player, long duration){
        put(player.getUniqueId(), getTime(player) - duration);
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

    public void resetTime(UUID player){
        put(player, System.currentTimeMillis()/50);
    }

    public void setTime(UUID player, long date){
        put(player, date);
    }

    public long getTime(UUID player){
        return getOrDefault(player, System.currentTimeMillis()/50);
    }

    public void extendTime(UUID player, long duration){
        put(player, getTime(player) + duration);
    }

    public void contractTime(UUID player, long duration){
        put(player, getTime(player) - duration);
    }

    public long elapsedTime(UUID player){
        return System.currentTimeMillis()/50 - getTime(player);
    }

    public boolean isPassed(UUID player, long duration){
        return elapsedTime(player) > duration;
    }

    public long remainingTime(UUID player, long duration){
        return duration - elapsedTime(player);
    }
}
