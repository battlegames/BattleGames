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
