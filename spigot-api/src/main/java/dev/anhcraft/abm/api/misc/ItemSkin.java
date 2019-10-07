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
package dev.anhcraft.abm.api.misc;

import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.annotation.*;
import dev.anhcraft.craftkit.kits.abif.PreparedItem;
import dev.anhcraft.jvmkit.utils.Condition;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

@Schema
public class ItemSkin {
    public static final ConfigSchema<ItemSkin> SCHEMA = ConfigSchema.of(ItemSkin.class);

    @Key("material")
    @Explanation("Set the material")
    @PrettyEnum
    @IgnoreValue(ifNull = true)
    private Material material = Material.AIR;

    @Key("damage")
    @Explanation("Set the damage")
    private int damage;

    @NotNull
    public Material getMaterial() {
        return material;
    }

    public int getDamage() {
        return damage;
    }

    @NotNull
    public PreparedItem transform(@NotNull PreparedItem preparedItem){
        Condition.argNotNull("preparedItem", preparedItem);
        preparedItem.material(material);
        preparedItem.damage(damage);
        return preparedItem;
    }
}
