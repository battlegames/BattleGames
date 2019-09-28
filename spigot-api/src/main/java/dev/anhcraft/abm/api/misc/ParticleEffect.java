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
package dev.anhcraft.abm.api.misc;

import dev.anhcraft.abm.utils.EnumUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class ParticleEffect {
    private Particle particle;
    private int count;
    private double offsetX;
    private double offsetY;
    private double offsetZ;
    private double speed;

    public ParticleEffect(@NotNull ConfigurationSection section) {
        this(
                EnumUtil.getEnum(Particle.values(), section.getString("type")),
                section.getInt("count", 1),
                section.getDouble("offset_x"),
                section.getDouble("offset_y"),
                section.getDouble("offset_z"),
                section.getDouble("speed")
        );
    }

    public ParticleEffect(@NotNull Particle particle, int count, double offsetX, double offsetY, double offsetZ, double speed) {
        this.particle = particle;
        this.count = count;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.speed = speed;
    }

    @NotNull
    public Particle getParticle() {
        return particle;
    }

    public int getCount() {
        return count;
    }

    public double getOffsetX() {
        return offsetX;
    }

    public double getOffsetY() {
        return offsetY;
    }

    public double getOffsetZ() {
        return offsetZ;
    }

    public double getSpeed() {
        return speed;
    }

    public void spawn(@NotNull Location location){
        location.getWorld().spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, speed, null);
    }
}
