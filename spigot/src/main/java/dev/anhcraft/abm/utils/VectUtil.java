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
