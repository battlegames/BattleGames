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

import de.tr7zw.changeme.nbtapi.NBTCompound;
import dev.anhcraft.battle.ApiProvider;
import dev.anhcraft.battle.api.effect.BattleParticle;
import dev.anhcraft.battle.utils.info.InfoHolder;
import dev.anhcraft.config.annotations.Configurable;
import dev.anhcraft.config.annotations.Description;
import dev.anhcraft.config.annotations.Path;
import dev.anhcraft.config.annotations.Setting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Ammo extends BattleItem<AmmoModel> {
    @Override
    public void save(NBTCompound compound) {
        if (getModel() != null) {
            compound.setString(ItemTag.AMMO_ID, getModel().getId());
        }
    }

    @Override
    public void load(NBTCompound compound) {
        if (compound.hasKey(ItemTag.AMMO_ID)) { // todo: remove this temp fix
            setModel(ApiProvider.consume().getAmmoModel(compound.getString(ItemTag.AMMO_ID)));
        }
    }

    @Override
    public void inform(@NotNull InfoHolder holder) {
        if (getModel() != null)
            getModel().inform(holder);
    }

    @SuppressWarnings("FieldMayBeFinal")
    @Configurable
    public static class Bullet {
        @Setting
        @Description({
                "Amount of damage to the target",
                "This damage may be increased or decreased relying on other",
                "factors like damage buff, damage resistance, etc"
        })
        private double damage;

        @Setting
        @Description({
                "The knockback power to push the target backward",
                "Negative values may create 'pull' effect"
        })
        private double knockback;

        @Setting
        @Path("penetration_power")
        @Description({
                "The penetration power",
                "Every time a bullet digs through a block, its power will be",
                "reduced until reaches zero and stop flying."
        })
        private int penetrationPower;

        @Setting
        @Path("fire_ticks")
        @Description({
                "How long does the fire remain on the target",
                "Set to 0 to turn off the fire"
        })
        private int fireTicks;

        @Setting
        @Path("particle")
        @Description("The particle to be shown when flying")
        private BattleParticle particleEffect;

        @Setting
        @Path("time_offset")
        @Description({
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

        public int getPenetrationPower() {
            return penetrationPower;
        }
    }
}
