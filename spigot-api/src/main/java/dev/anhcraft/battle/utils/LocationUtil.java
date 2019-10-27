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

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LocationUtil {
    @NotNull
    public static String toString(@Nullable Location loc) {
        if(loc == null) return "null";
        String n = loc.getWorld() == null ? "~" : loc.getWorld().getName();
        return n +
                " " + loc.getX() +
                " " + loc.getY() +
                " " + loc.getZ() +
                " " + loc.getYaw() +
                " " + loc.getPitch();
    }

    @NotNull
    public static Location fromString(@Nullable String str) {
        if(str == null || (str = str.replaceAll("[^a-zA-Z0-9-. ~]", ""))
                .equalsIgnoreCase("null")) return Bukkit.getWorlds().get(0).getSpawnLocation();
        String[] str2loc = str.split(" ");
        Location loc = new Location(
                str2loc[0].equals("~") ? null : Bukkit.getWorld(str2loc[0]),
                Double.parseDouble(str2loc[1]),
                Double.parseDouble(str2loc[2]),
                Double.parseDouble(str2loc[3]));
        if(str2loc.length == 5) loc.setYaw(Float.parseFloat(str2loc[4]));
        if(str2loc.length > 5) loc.setPitch(Float.parseFloat(str2loc[5]));
        return loc;
    }
}
