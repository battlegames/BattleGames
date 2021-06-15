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
package dev.anhcraft.battle.gui.menu.backpack;

import dev.anhcraft.battle.api.BattleApi;
import dev.anhcraft.battle.api.inventory.item.BattleItemModel;
import dev.anhcraft.battle.api.inventory.item.ItemType;

public class MagazineCompartment extends ItemCompartment {
    @Override
    public ItemType getItemType() {
        return ItemType.MAGAZINE;
    }

    @Override
    public BattleItemModel getItem(String id, BattleApi api) {
        return api.getMagazineModel(id);
    }

    @Override
    public boolean isObtainable() {
        return true;
    }
}
