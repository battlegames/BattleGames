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
package dev.anhcraft.battle.api.reports;

import dev.anhcraft.jvmkit.utils.Condition;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class DamageReport {
    protected LivingEntity entity;
    private double damage;
    private boolean headshotDamage;
    private long date;

    public DamageReport(@NotNull LivingEntity entity, double damage) {
        Condition.argNotNull("entity", entity);
        this.entity = entity;
        this.damage = damage;
        this.date = System.currentTimeMillis();
    }

    @NotNull
    public LivingEntity getEntity() {
        return entity;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public boolean isHeadshotDamage() {
        return headshotDamage;
    }

    public void setHeadshotDamage(boolean headshotDamage) {
        this.headshotDamage = headshotDamage;
    }

    public long getDate() {
        return date;
    }
}
