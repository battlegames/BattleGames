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

import org.bukkit.util.Vector;

public class VectUtil {
    // https://hub.spigotmc.org/stash/projects/SPIGOT/repos/bukkit/pull-requests/358/overview
    public static Vector rotate(Vector vec, float yaw, float pitch) {
        double yawRadians = Math.toRadians(yaw);
        double pitchRadians = Math.toRadians(-pitch);

        double cosYaw = Math.cos(yawRadians);
        double cosPitch = Math.cos(pitchRadians);
        double sinYaw = Math.sin(yawRadians);
        double sinPitch = Math.sin(pitchRadians);

        double initialX = vec.getX();
        double initialY = vec.getY();
        double initialZ = vec.getZ();
        vec.setX(initialX * cosPitch - initialY * sinPitch);
        vec.setY(initialX * sinPitch + initialY * cosPitch);

        initialX = vec.getX();
        vec.setZ(initialZ * cosYaw + initialX * sinYaw);
        vec.setX(initialX * cosYaw - initialZ * sinYaw);
        return vec;
    }
}
