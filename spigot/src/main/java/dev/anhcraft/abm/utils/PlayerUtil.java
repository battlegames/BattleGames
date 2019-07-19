package dev.anhcraft.abm.utils;

import org.bukkit.entity.Player;

public class PlayerUtil {
    public static void reduceSpeed(Player player, double w){
        float speed = player.isSneaking() ? 0.1f : 0.2f;
        speed -= w;
        speed = Math.max(speed, 0);
        player.setWalkSpeed(speed);
        player.setFlySpeed(speed);
    }

    public static void increaseSpeed(Player player, double w){
        float speed = player.isSneaking() ? 0.1f : 0.2f;
        speed += w;
        player.setWalkSpeed(speed);
        player.setFlySpeed(speed);
    }
}
