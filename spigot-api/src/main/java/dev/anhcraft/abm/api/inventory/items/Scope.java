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
import org.jetbrains.annotations.NotNull;

public class Scope extends BattleItem<ScopeModel> {
    private int nextZoomLevel = -1;

    public int getNextZoomLevel() {
        return nextZoomLevel;
    }

    public void setNextZoomLevel(int nextZoomLevel) {
        this.nextZoomLevel = nextZoomLevel;
    }

    public int nextZoomLevel() {
        getModel().ifPresent(scopeModel -> {
            if(++nextZoomLevel == scopeModel.getZoomLevels().size()) nextZoomLevel = -1;
        });
        return nextZoomLevel;
    }

    @Override
    public void save(CompoundTag compound) {
        getModel().ifPresent(m -> compound.put(ItemTag.SCOPE_ID, m.getId()));
        compound.put(ItemTag.SCOPE_NEXT_ZOOM_LEVEL, nextZoomLevel);
    }

    @Override
    public void load(CompoundTag compound) {
        ApiProvider.consume().getScopeModel(compound.getValue(ItemTag.SCOPE_ID, StringTag.class)).ifPresent(this::setModel);
        Integer nextZoomLv = compound.getValue(ItemTag.SCOPE_NEXT_ZOOM_LEVEL, IntTag.class);
        if(nextZoomLv != null) nextZoomLevel = nextZoomLv;
    }

    @Override
    public void inform(@NotNull InfoHolder holder) {
        getModel().ifPresent(m -> m.inform(holder));
    }
}
