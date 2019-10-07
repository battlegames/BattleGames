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

import dev.anhcraft.abm.api.ApiProvider;
import dev.anhcraft.abm.api.misc.BattleParticle;
import dev.anhcraft.abm.api.misc.info.InfoHolder;
import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.annotation.Explanation;
import dev.anhcraft.confighelper.annotation.Key;
import dev.anhcraft.confighelper.annotation.Schema;
import dev.anhcraft.craftkit.cb_common.kits.nbt.CompoundTag;
import dev.anhcraft.craftkit.cb_common.kits.nbt.StringTag;
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
        @Explanation("The damage that this bullet will cause")
        private double damage;

        @Key("knockback")
        @Explanation("The knockback power that applied for the target")
        private double knockback;

        @Key("particle")
        @Explanation("The particle to be shown when flying")
        private BattleParticle particleEffect;

        public double getDamage() {
            return damage;
        }

        public double getKnockback() {
            return knockback;
        }

        @Nullable
        public BattleParticle getParticleEffect() {
            return particleEffect;
        }
    }
}
