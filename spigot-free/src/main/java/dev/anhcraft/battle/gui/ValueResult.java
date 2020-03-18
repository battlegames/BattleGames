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

package dev.anhcraft.battle.gui;

import dev.anhcraft.craftkit.abif.PreparedItem;
import org.apache.commons.lang.BooleanUtils;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class ValueResult {
    private Object val;

    public ValueResult(Object val) {
        this.val = val;
    }

    public boolean asBoolean(){
        return BooleanUtils.toBooleanObject(String.valueOf(val));
    }

    public int asInt(){
        return (int) Double.parseDouble(String.valueOf(val));
    }

    public double asDouble(){
        return Double.parseDouble(String.valueOf(val));
    }

    public float asFloat(){
        return Float.parseFloat(String.valueOf(val));
    }

    public long asLong(){
        return (long) Double.parseDouble(String.valueOf(val));
    }

    public ItemStack asItem(){
        return val instanceof PreparedItem ? ((PreparedItem) val).build() : (ItemStack) val;
    }

    public PreparedItem asPreparedItem(){
        return val instanceof ItemStack ? PreparedItem.of((ItemStack) val) : (PreparedItem) val;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValueResult that = (ValueResult) o;
        return val.equals(that.val);
    }

    @Override
    public int hashCode() {
        return Objects.hash(val);
    }
}
