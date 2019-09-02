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

import org.bukkit.Particle;

public class Bullet {
    private double damage;
    private double knockback;
    private Particle particle;
    private int count;
    private double offsetX;
    private double offsetY;
    private double offsetZ;
    private double speed;
    private double viewDistance;

    public Bullet(double damage, double knockback, Particle particle, int count, double offsetX, double offsetY, double offsetZ, double speed, double viewDistance) {
        this.damage = damage;
        this.knockback = knockback;
        this.particle = particle;
        this.count = count;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.speed = speed;
        this.viewDistance = viewDistance;
    }

    public double getDamage() {
        return damage;
    }

    public double getKnockback() {
        return knockback;
    }

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

    public double getViewDistance() {
        return viewDistance;
    }
}
