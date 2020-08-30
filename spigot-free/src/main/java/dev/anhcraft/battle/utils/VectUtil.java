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

import org.bukkit.util.Vector;

public class VectUtil {
    public static Vector rotate(Vector v, float yawDegrees, float pitchDegrees) {
        double yaw = Math.toRadians(-1.0f * (yawDegrees + 90.0f));
        double pitch = Math.toRadians(-pitchDegrees);
        double cosYaw = Math.cos(yaw);
        double cosPitch = Math.cos(pitch);
        double sinYaw = Math.sin(yaw);
        double sinPitch = Math.sin(pitch);
        double initialX = v.getX();
        double initialY = v.getY();
        double x = initialX * cosPitch - initialY * sinPitch;
        double y = initialX * sinPitch + initialY * cosPitch;
        double initialZ = v.getZ();
        initialX = x;
        double z = initialZ * cosYaw - initialX * sinYaw;
        x = initialZ * sinYaw + initialX * cosYaw;
        return new Vector(x, y, z);
    }

    public static void rotateNew(Vector vec, float yawDeg, float pitchDeg) {
        double yaw = Math.toRadians(yawDeg); // trái - phải
        double pitch = Math.toRadians(pitchDeg); // trên - dưới

        double x = vec.getX();
        double z = vec.getZ();
        double y = vec.getY();

        // Xét mặt XZ với X là trục cos, Z là trục sin
        double angleXZ = Math.atan2(z, x) + yaw;
        vec.setX(x * Math.cos(angleXZ));
        vec.setZ(z * Math.sin(angleXZ));

        // Xét mặt YZ với Z là trục cos, Y là trục sin
        double angleYZ = Math.atan2(y, z) + pitch;
        vec.setY(y * Math.sin(angleYZ));
        vec.setZ(z * Math.cos(angleYZ));
    }
}
