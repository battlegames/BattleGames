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

public class Grenade extends BattleItem<GrenadeModel> {
    @Override
    public void save(CompoundTag compound) {
        if(getModel() != null){
            compound.put(ItemTag.GRENADE_ID, getModel().getId());
        }
    }

    @Override
    public void load(CompoundTag compound) {
        setModel(ApiProvider.consume().getGrenadeModel(compound.getValue(ItemTag.GRENADE_ID, StringTag.class)));
    }

    @Override
    public void inform(@NotNull InfoHolder holder) {
        if(getModel() != null) getModel().inform(holder);
    }
}
