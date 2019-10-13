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

import dev.anhcraft.abm.api.misc.ItemSkin;
import dev.anhcraft.confighelper.annotation.Explanation;
import dev.anhcraft.confighelper.annotation.Key;
import dev.anhcraft.confighelper.annotation.Schema;
import dev.anhcraft.confighelper.annotation.Validation;
import org.jetbrains.annotations.NotNull;

@Schema
public abstract class SingleSkinItem extends BattleItemModel {
    @Key("skin")
    @Explanation("Set the item's skin")
    @Validation(notNull = true)
    private ItemSkin skin;

    protected SingleSkinItem(@NotNull String id) {
        super(id);
    }

    @NotNull
    public ItemSkin getSkin() {
        return skin;
    }
}
