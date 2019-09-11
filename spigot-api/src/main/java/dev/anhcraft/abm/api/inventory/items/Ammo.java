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
import dev.anhcraft.craftkit.cb_common.kits.nbt.StringTag;
import org.jetbrains.annotations.NotNull;

public class Ammo extends BattleItem<AmmoModel> {
    @Override
    public void save(CompoundTag compound) {
        getModel().ifPresent(ammoModel -> compound.put(ItemTag.AMMO_ID, ammoModel.getId()));
    }

    @Override
    public void load(CompoundTag compound) {
        ApiProvider.consume().getAmmoModel(compound.getValue(ItemTag.AMMO_ID, StringTag.class)).ifPresent(this::setModel);
    }

    @Override
    public void inform(@NotNull InfoHolder holder) {
        getModel().ifPresent(ammoModel -> ammoModel.inform(holder));
    }
}
