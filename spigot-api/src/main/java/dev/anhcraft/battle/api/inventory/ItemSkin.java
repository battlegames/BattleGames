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
package dev.anhcraft.battle.api.inventory;

import dev.anhcraft.battle.utils.PreparedItem;
import dev.anhcraft.config.annotations.Configurable;
import dev.anhcraft.config.annotations.Description;
import dev.anhcraft.config.annotations.Setting;
import dev.anhcraft.config.annotations.Validation;
import dev.anhcraft.jvmkit.utils.Condition;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

@Configurable
public class ItemSkin {
    public static final ItemSkin EMPTY = new ItemSkin();

    @Setting
    @Description("Set the material")
    @Validation(notNull = true, silent = true)
    private final Material material = Material.AIR;

    @Setting
    @Description("Set the damage")
    private int damage;

    @NotNull
    public Material getMaterial() {
        return material;
    }

    public int getDamage() {
        return damage;
    }

    @NotNull
    public PreparedItem transform(@NotNull PreparedItem preparedItem) {
        Condition.argNotNull("preparedItem", preparedItem);
        preparedItem.material(material);
        preparedItem.damage(damage);
        return preparedItem;
    }
}
