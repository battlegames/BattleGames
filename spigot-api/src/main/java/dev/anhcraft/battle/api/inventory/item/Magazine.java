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
import dev.anhcraft.battle.utils.info.InfoHolder;
import dev.anhcraft.craftkit.cb_common.nbt.CompoundTag;
import dev.anhcraft.craftkit.cb_common.nbt.IntTag;
import dev.anhcraft.craftkit.cb_common.nbt.StringTag;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

public class Magazine extends BattleItem<MagazineModel> {
    @NotNull
    private Ammo ammo = new Ammo();
    private int ammoCount;

    public int getAmmoCount() {
        return ammoCount;
    }

    public void setAmmoCount(int ammoCount) {
        this.ammoCount = ammoCount;
    }

    @NotNull
    public Ammo getAmmo() {
        return ammo;
    }

    public void setAmmo(@NotNull Ammo ammo) {
        Validate.notNull(ammo, "AmmoModel must be non-null");
        this.ammo = ammo;
    }

    public void resetAmmo() {
        if (getModel() != null && ammo.getModel() != null) {
            ammoCount = getModel().getAmmunition().getOrDefault(ammo.getModel(), 0);
        }
    }

    @Override
    public void save(CompoundTag compound) {
        if (getModel() != null) {
            compound.put(ItemTag.MAGAZINE_ID, getModel().getId());
        }
        compound.put(ItemTag.MAGAZINE_AMMO_COUNT, ammoCount);
        CompoundTag c = new CompoundTag();
        ammo.save(c);
        compound.put(ItemTag.MAGAZINE_AMMO, c);
    }

    @Override
    public void load(CompoundTag compound) {
        setModel(ApiProvider.consume().getMagazineModel(compound.getValue(ItemTag.MAGAZINE_ID, StringTag.class)));
        Integer a = compound.getValue(ItemTag.MAGAZINE_AMMO_COUNT, IntTag.class);
        if (a != null) ammoCount = a;
        CompoundTag am = compound.get(ItemTag.MAGAZINE_AMMO, CompoundTag.class);
        if (am != null) ammo.load(am);
    }

    @Override
    public void inform(@NotNull InfoHolder holder) {
        if (getModel() != null) {
            getModel().inform(holder);
            if (ammo.getModel() != null) {
                holder.inform("ammo_capacity", getModel().getAmmunition().getOrDefault(ammo.getModel(), 0));
            }
        }
        holder.link(ammo.collectInfo(null)).inform("ammo_count", ammoCount);
    }
}
