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
package dev.anhcraft.abm.api.entity;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BulletEntity extends VirtualEntity {
    private Bullet bullet;

    public BulletEntity(@NotNull Location location, @NotNull Bullet bullet) {
        super(location);
        Validate.notNull(bullet, "Bullet must be non-null");
        this.bullet = bullet;
    }

    @NotNull
    public Bullet getBullet() {
        return bullet;
    }

    public void spawnParticle(){
        Objects.requireNonNull(getLocation().getWorld())
                .getNearbyEntities(getLocation(), bullet.getViewDistance(), bullet.getViewDistance(), bullet.getViewDistance()).stream().filter(f -> f instanceof Player)
                .forEach(entity -> ((Player) entity).spawnParticle(bullet.getParticle(), getLocation(), bullet.getCount(), bullet.getOffsetX(), bullet.getOffsetY(), bullet.getOffsetZ(), bullet.getSpeed()));
    }
}
