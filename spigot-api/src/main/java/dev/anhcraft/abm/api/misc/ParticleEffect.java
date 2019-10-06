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

import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.annotation.*;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;

@Schema
public class ParticleEffect {
    public static final ConfigSchema<ParticleEffect> SCHEMA = ConfigSchema.of(ParticleEffect.class);

    @Key("type")
    @Explanation("The type of particle")
    @PrettyEnum
    @IgnoreValue(ifNull = true)
    private Particle type = Particle.CLOUD;

    @Key("count")
    @Explanation("The number of particles")
    private int count = 1;

    @Key("offset_x")
    @Explanation({
            "The maximum random offset on the X axis",
            "Or the Red value in RGB (with colored dust particle)"
    })
    private double offsetX;

    @Key("offset_y")
    @Explanation({
            "The maximum random offset on the Y axis",
            "Or the Green value in RGB (with colored dust particle)"
    })
    private double offsetY;

    @Key("offset_z")
    @Explanation({
            "The maximum random offset on the Z axis",
            "Or the Blue value in RGB (with colored dust particle)"
    })
    private double offsetZ;

    @Key("speed")
    @Explanation({
            "Particle speed"
    })
    private double speed;

    @NotNull
    public Particle getType() {
        return type;
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
        location.getWorld().spawnParticle(type, location, count, offsetX, offsetY, offsetZ, speed, null);
    }
}
