package dev.anhcraft.abm.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class LocationUtil {
    public static String toString(@Nullable Location loc) {
        if(loc == null) return "null";
        return Objects.requireNonNull(loc.getWorld()).getName() +
                " " + loc.getX() +
                " " + loc.getY() +
                " " + loc.getZ() +
                " " + loc.getYaw() +
                " " + loc.getPitch();
    }

    public static Location fromString(@Nullable String str) {
        if(str == null || (str = str.replaceAll("[^a-zA-Z0-9-. ]", ""))
                .equalsIgnoreCase("null")) return Bukkit.getWorlds().get(0).getSpawnLocation();
        String[] str2loc = str.split(" ");
        Location loc = new Location(
                Bukkit.getWorld(str2loc[0]),
                Double.parseDouble(str2loc[1]),
                Double.parseDouble(str2loc[2]),
                Double.parseDouble(str2loc[3]));
        if(str2loc.length == 5) loc.setYaw(Float.parseFloat(str2loc[4]));
        if(str2loc.length > 5) loc.setPitch(Float.parseFloat(str2loc[5]));
        return loc;
    }
}
