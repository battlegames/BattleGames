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

import dev.anhcraft.battle.ApiProvider;
import dev.anhcraft.battle.api.effect.BattleParticle;
import dev.anhcraft.battle.utils.info.InfoHolder;
import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.annotation.Explanation;
import dev.anhcraft.confighelper.annotation.Key;
import dev.anhcraft.confighelper.annotation.Schema;
import dev.anhcraft.craftkit.cb_common.nbt.CompoundTag;
import dev.anhcraft.craftkit.cb_common.nbt.StringTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Ammo extends BattleItem<AmmoModel> {
    @Override
    public void save(CompoundTag compound) {
        if(getModel() != null){
            compound.put(ItemTag.AMMO_ID, getModel().getId());
        }
    }

    @Override
    public void load(CompoundTag compound) {
        setModel(ApiProvider.consume().getAmmoModel(compound.getValue(ItemTag.AMMO_ID, StringTag.class)));
    }

    @Override
    public void inform(@NotNull InfoHolder holder) {
        if(getModel() != null)
            getModel().inform(holder);
    }

    @Schema
    public static class Bullet {
        public static final ConfigSchema<Bullet> SCHEMA = ConfigSchema.of(Bullet.class);

        @Key("damage")
        @Explanation("The damage")
        private double damage;

        @Key("knockback")
        @Explanation("The knockback power")
        private double knockback;

        @Key("fire_ticks")
        @Explanation({
                "How long does the fire remain on the target",
                "Set to 0 to prevent the fire"
        })
        private int fireTicks;

        @Key("particle")
        @Explanation("The particle to be shown when flying")
        private BattleParticle particleEffect;

        @Key("time_offset")
        @Explanation({
                "The bullet's time offset (in milliseconds)",
                "Higher value can help to make the entity bounding check",
                "more accurate, but may result in laggy"
        })
        private double timeOffset = 5;

        public double getDamage() {
            return damage;
        }

        public double getKnockback() {
            return knockback;
        }

        public int getFireTicks() {
            return fireTicks;
        }

        @Nullable
        public BattleParticle getParticleEffect() {
            return particleEffect;
        }

        public double getTimeOffset() {
            return timeOffset;
        }
    }
}
