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
import dev.anhcraft.abm.api.misc.info.InfoHolder;
import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.annotation.Explanation;
import dev.anhcraft.confighelper.annotation.Key;
import dev.anhcraft.confighelper.annotation.Schema;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Schema
public class GrenadeModel extends SingleSkinWeapon {
    public static final ConfigSchema<GrenadeModel> SCHEMA = ConfigSchema.of(GrenadeModel.class);

    @Key("explosion_effect.particle")
    @Explanation("The explosion effect")
    private ParticleEffect explosionEffect;

    @Key("delay_time")
    @Explanation({
            "The delay time before the grenade is going to 'boom'",
            "Set to 0 to disable this feature"
    })
    private long delayTime;

    @Key("velocity_multiplier")
    @Explanation("The multiplier applies to the directional velocity")
    private double velocityMultiplier = 2.0;

    @Key("explosion_power")
    @Explanation({
            "The power of the explosion",
            "Set to 0 to prevent the explosion"
    })
    private double explosionPower;

    @Key("inventory_slot")
    @Explanation({
            "The slot where the grenade is put into",
            "Only supported by a few game modes"
    })
    private int inventorySlot;

    public GrenadeModel(@NotNull String id) {
        super(id);
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
