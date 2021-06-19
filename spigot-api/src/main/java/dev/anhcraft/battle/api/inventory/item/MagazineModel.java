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

import dev.anhcraft.config.annotations.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("FieldMayBeFinal")
@Configurable
public class MagazineModel extends SingleSkinItem implements Attachable {
    @Setting
    @Path("ammo")
    @Description("All ammo types can be stored in this magazine")
    @Validation(notNull = true, silent = true)
    @Example({
            "ammo:",
            "  7_62mm: 30 # Can hold up to x30 7.62mm ammo"
    })
    private Map<AmmoModel, Integer> ammunition = new HashMap<>();

    public MagazineModel(@NotNull String id) {
        super(id);
    }

    @Override
    public @NotNull ItemType getItemType() {
        return ItemType.MAGAZINE;
    }

    @NotNull
    public Map<AmmoModel, Integer> getAmmunition() {
        return ammunition;
    }

    @Override
    public ItemType[] getHolderTypes() {
        return new ItemType[]{
                ItemType.GUN
        };
    }
}
