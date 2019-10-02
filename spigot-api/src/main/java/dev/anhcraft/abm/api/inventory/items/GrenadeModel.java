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

package dev.anhcraft.abm.api.inventory.items;

import dev.anhcraft.abm.api.misc.ParticleEffect;
import dev.anhcraft.abm.api.misc.Skin;
import dev.anhcraft.abm.api.misc.info.InfoHolder;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GrenadeModel extends WeaponModel {
    private Skin skin;
    private ParticleEffect explosionEffect;
    private long delayTime;
    private double velocityMultiplier;
    private double explosionPower;
    private int inventorySlot;

    public GrenadeModel(@NotNull String id, @NotNull ConfigurationSection conf) {
        super(id, conf);

        skin = new Skin(conf.getConfigurationSection("skin"));
        delayTime = conf.getLong("delay_time");
        ConfigurationSection ee = conf.getConfigurationSection("explosion_effect.particle");
        if(ee != null) explosionEffect = new ParticleEffect(ee);
        velocityMultiplier = conf.getDouble("velocity_multiplier", 2.0);
        explosionPower = conf.getDouble("explosion_power");
        inventorySlot = conf.getInt("inventory_slot");
    }

    @Override
    @NotNull
    public ItemType getItemType() {
        return ItemType.GRENADE;
    }

    public double getVelocityMultiplier() {
        return velocityMultiplier;
    }

    public double getExplosionPower() {
        return explosionPower;
    }

    @NotNull
    public Skin getSkin() {
        return skin;
    }

    @Nullable
    public ParticleEffect getExplosionEffect() {
        return explosionEffect;
    }

    public long getDelayTime() {
        return delayTime;
    }

    public int getInventorySlot() {
        return inventorySlot;
    }

    @Override
    public void inform(@NotNull InfoHolder holder){
        super.inform(holder);
        holder.inform("delay_time", delayTime)
                .inform("velocity_multiplier", velocityMultiplier)
                .inform("explosion_power", explosionPower);
    }
}
