package dev.anhcraft.abm.system.managers;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CooldownManager {
    private final Map<Player, Long> MAP = new HashMap<>();

    public void resetTime(Player player){
        MAP.put(player, System.currentTimeMillis());
    }

    public void setTime(Player player, long date){
        MAP.put(player, date);
    }

    public long getTime(Player player){
        return MAP.getOrDefault(player, System.currentTimeMillis());
    }

    public void extendTime(Player player, long duration){
        MAP.put(player, getTime(player) + duration);
    }

    public void contractTime(Player player, long duration){
        MAP.put(player, getTime(player) - duration);
    }

    public long elapsedTime(Player player){
        return System.currentTimeMillis() - getTime(player);
    }

    public boolean isPassed(Player player, long duration){
        return elapsedTime(player) > duration;
    }

    public long remainingTime(Player player, long duration){
        return duration - elapsedTime(player);
    }
}
