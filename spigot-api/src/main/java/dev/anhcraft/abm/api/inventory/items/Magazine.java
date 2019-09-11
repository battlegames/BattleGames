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
import dev.anhcraft.abm.api.misc.info.InfoHolder;
import dev.anhcraft.craftkit.cb_common.kits.nbt.CompoundTag;
import dev.anhcraft.craftkit.cb_common.kits.nbt.IntTag;
import dev.anhcraft.craftkit.cb_common.kits.nbt.StringTag;
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
        getModel().ifPresent(magazineModel -> ammo.getModel().ifPresent(ammoModel -> ammoCount = magazineModel.getAmmunition().getOrDefault(ammoModel, 0)));
    }

    @Override
    public void save(CompoundTag compound) {
        getModel().ifPresent(magazineModel -> compound.put(ItemTag.MAGAZINE_ID, magazineModel.getId()));
        compound.put(ItemTag.MAGAZINE_AMMO_COUNT, ammoCount);
        CompoundTag c = new CompoundTag();
        ammo.save(c);
        compound.put(ItemTag.MAGAZINE_AMMO, c);
    }

    @Override
    public void load(CompoundTag compound) {
        ApiProvider.consume().getMagazineModel(compound.getValue(ItemTag.MAGAZINE_ID, StringTag.class)).ifPresent(this::setModel);
        Integer a = compound.getValue(ItemTag.MAGAZINE_AMMO_COUNT, IntTag.class);
        if(a != null) ammoCount = a;
        CompoundTag am = compound.get(ItemTag.MAGAZINE_AMMO, CompoundTag.class);
        if(am != null) ammo.load(am);
    }

    @Override
    public void inform(@NotNull InfoHolder holder) {
        getModel().ifPresent(magazineModel -> {
            magazineModel.inform(holder);
            ammo.getModel().ifPresent(ammoModel -> holder.inform("ammo_capacity", magazineModel.getAmmunition().getOrDefault(ammoModel, 0)));
        });
        holder.link(ammo.collectInfo(null)).inform("ammo_count", ammoCount);
    }
}
