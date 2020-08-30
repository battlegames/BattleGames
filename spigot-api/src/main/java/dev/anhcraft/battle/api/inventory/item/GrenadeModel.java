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

package dev.anhcraft.battle.api.inventory.item;

import dev.anhcraft.battle.api.effect.BattleEffect;
import dev.anhcraft.battle.utils.info.InfoHolder;
import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.annotation.Explanation;
import dev.anhcraft.confighelper.annotation.Key;
import dev.anhcraft.confighelper.annotation.Schema;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("FieldMayBeFinal")
@Schema
public class GrenadeModel extends SingleSkinWeapon {
    public static final ConfigSchema<GrenadeModel> SCHEMA = ConfigSchema.of(GrenadeModel.class);

    @Key("explosion_effect")
    @Explanation("The explosion effect")
    private BattleEffect explosionEffect;

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

    @Key("fire.block_radius")
    @Explanation({
            "The radius that inner blocks get ignited",
            "Set to 0 to prevent the fire (for blocks)"
    })
    private int fireBlockRadius;

    @Key("fire.mob_radius")
    @Explanation({
            "The radius that inner mobs get ignited",
            "Set to 0 to prevent the fire (for mobs)"
    })
    private double fireMobRadius;

    @Key("fire.mob_ticks")
    @Explanation({
            "How long do mobs get ignited",
            "Set to 0 to prevent the fire (for mobs)"
    })
    private int fireMobTicks;

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
    public BattleEffect getExplosionEffect() {
        return explosionEffect;
    }

    public long getDelayTime() {
        return delayTime;
    }

    @Override
    public void inform(@NotNull InfoHolder holder) {
        super.inform(holder);
        holder.inform("delay_time", delayTime)
                .inform("velocity_multiplier", velocityMultiplier)
                .inform("explosion_power", explosionPower)
                .inform("fire_block_radius", fireBlockRadius)
                .inform("fire_mob_radius", fireMobRadius)
                .inform("fire_mob_ticks", fireMobTicks);
    }

    public int getFireBlockRadius() {
        return fireBlockRadius;
    }

    public double getFireMobRadius() {
        return fireMobRadius;
    }

    public int getFireMobTicks() {
        return fireMobTicks;
    }
}
