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
import de.tr7zw.changeme.nbtapi.NBTContainer;
import dev.anhcraft.battle.ApiProvider;
import dev.anhcraft.battle.utils.info.InfoHolder;
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
        if (getModel() != null) {
            if (++nextZoomLevel == getModel().getZoomLevels().size()) nextZoomLevel = -1;
        }
        return nextZoomLevel;
    }

    @Override
    public void save(NBTCompound compound) {
        if (getModel() != null) {
            compound.setString(ItemTag.SCOPE_ID, getModel().getId());
        }
        compound.setInteger(ItemTag.SCOPE_NEXT_ZOOM_LEVEL, nextZoomLevel);
    }

    @Override
    public void load(NBTCompound compound) {
        setModel(ApiProvider.consume().getScopeModel(compound.getString(ItemTag.SCOPE_ID)));
        Integer nextZoomLv = compound.getInteger(ItemTag.SCOPE_NEXT_ZOOM_LEVEL);
        if (nextZoomLv != null) nextZoomLevel = nextZoomLv;
    }

    @Override
    public void inform(@NotNull InfoHolder holder) {
        if (getModel() != null)
            getModel().inform(holder);
    }
}
