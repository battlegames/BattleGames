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

package dev.anhcraft.battle.api.effect;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

public enum EffectType {
    HOLLOW_SPHERE((loc, bf) -> {
        double radius = ((Number) bf.getOptions().getOrDefault(EffectOption.SPHERE_RADIUS, 0)).doubleValue();
        double density = ((Number) bf.getOptions().getOrDefault(EffectOption.PARTICLE_DENSITY, 0)).doubleValue();
        if(density <= 0) density = 1;
        double delta = Math.PI / density;
        for (double rad1 = 0; rad1 <= Math.PI; rad1 += delta) {
            double y = Math.cos(rad1) * radius;
            double r = Math.sin(rad1) * radius;
            for (double rad2 = 0; rad2 < 2 * Math.PI; rad2 += delta) {
                double x = Math.cos(rad2) * r;
                double z = Math.sin(rad2) * r;
                bf.spawn(
                        loc.getWorld(),
                        loc.getX() + x,
                        loc.getY() + y,
                        loc.getZ() + z
                );
            }
        }
    });

    private final BiConsumer<Location, BattleEffect> effectConsumer;

    EffectType(BiConsumer<Location, BattleEffect> effectConsumer) {
        this.effectConsumer = effectConsumer;
    }

    @NotNull
    public BiConsumer<Location, BattleEffect> getEffectConsumer() {
        return effectConsumer;
    }
}
