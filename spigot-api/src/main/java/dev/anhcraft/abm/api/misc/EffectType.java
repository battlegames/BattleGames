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

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

public enum EffectType {
    SPHERE((loc, bf) -> {
        double radius = ((Number) bf.getOptions().getOrDefault(EffectOption.SPHERE_RADIUS, 0)).doubleValue();
        double delta = Math.PI / (10 * radius);
        // 1 hình cầu bao gồm các hình tròn xếp chồng lên nhau theo trục y
        // các hình tròn này sẽ có bán kinh khác nhau, vì thế, cần tìm ra vị trí y và bán kính
        // đầu tiên, xét hệ yOx (hoặc yOz) với y là trục cos; x, z là trục sin
        // PI (radian) = 180', vì bán kính và y không đổi ở hai mặt phẳng (ngăn cách theo trục cos)
        // vì thế chỉ cần xét trong khoảng 180'
        for(double rad1 = 0; rad1 <= Math.PI; rad1 += delta){
            double y = Math.cos(rad1) * radius; // tìm ra y theo cos
            double mr = Math.sin(rad1) * radius; /// tìm ra bán kính theo x
            // tạo ra các hình tròn với bán kính tăng dần
            for(double r = 0; r < mr; r += delta) {
                // xét hệ xOz với x là trục cos, z là trục sin
                // 2 * PI (radian) = 360'
                for (double rad2 = 0; rad2 < 2 * Math.PI; rad2 += delta) {
                    double x = Math.cos(rad2) * r; // tìm ra x
                    double z = Math.sin(rad2) * r; // tìm ra z
                    bf.getParticle().spawn(loc.clone().add(x, y, z));
                }
            }
        }
    }),
    HOLLOW_SPHERE((loc, bf) -> {
        double radius = ((Number) bf.getOptions().getOrDefault(EffectOption.SPHERE_RADIUS, 0)).doubleValue();
        double delta = Math.PI / (10 * radius);
        for(double rad1 = 0; rad1 <= Math.PI; rad1 += delta){
            double y = Math.cos(rad1) * radius;
            double r = Math.sin(rad1) * radius;
            for(double rad2 = 0; rad2 < 2 * Math.PI; rad2 += delta){
                double x = Math.cos(rad2) * r;
                double z = Math.sin(rad2) * r;
                bf.getParticle().spawn(loc.clone().add(x, y, z));
            }
        }
    });

    private BiConsumer<Location, BattleEffect> effectConsumer;

    EffectType(BiConsumer<Location, BattleEffect> effectConsumer){
        this.effectConsumer = effectConsumer;
    }

    @NotNull
    public BiConsumer<Location, BattleEffect> getEffectConsumer() {
        return effectConsumer;
    }
}
