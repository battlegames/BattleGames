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

import dev.anhcraft.abm.utils.EnumUtil;
import dev.anhcraft.craftkit.kits.abif.PreparedItem;
import dev.anhcraft.jvmkit.utils.Condition;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Skin {
    private Material material;
    private int damage;

    public Skin(@Nullable ConfigurationSection section){
        if(section == null){
            material = Material.AIR;
            return;
        }
        String material = section.getString("material");
        this.material = material == null ? Material.AIR : EnumUtil.getEnum(Material.values(), material);
        damage = section.getInt("damage");
    }

    public Skin(@Nullable Material material, int damage) {
        this.material = (material == null ? Material.AIR : material);
        this.damage = damage;
    }

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
