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
import dev.anhcraft.battle.utils.info.InfoHolder;
import dev.anhcraft.jvmkit.utils.Condition;
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
        Condition.notNull(ammo, "AmmoModel must be non-null");
        this.ammo = ammo;
    }

    public void resetAmmo() {
        if (getModel() != null && ammo.getModel() != null) {
            ammoCount = getModel().getAmmunition().getOrDefault(ammo.getModel(), 0);
        }
    }

    @Override
    public void save(NBTCompound compound) {
        if (getModel() != null) {
            compound.setString(ItemTag.MAGAZINE_ID, getModel().getId());
        }
        compound.setInteger(ItemTag.MAGAZINE_AMMO_COUNT, ammoCount);
        NBTCompound c = compound.addCompound(ItemTag.MAGAZINE_AMMO);
        ammo.save(c);
    }

    @Override
    public void load(NBTCompound compound) {
        setModel(ApiProvider.consume().getMagazineModel(compound.getString(ItemTag.MAGAZINE_ID)));
        Integer a = compound.getInteger(ItemTag.MAGAZINE_AMMO_COUNT);
        if (a != null) ammoCount = a;
        NBTCompound am = compound.getCompound(ItemTag.MAGAZINE_AMMO);
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
