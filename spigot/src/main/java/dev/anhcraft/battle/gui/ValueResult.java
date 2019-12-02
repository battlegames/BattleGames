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

import org.apache.commons.lang.BooleanUtils;

import java.util.Objects;

public class ValueResult {
    private String val;

    public ValueResult(String val) {
        this.val = val;
    }

    public boolean asBoolean(){
        return BooleanUtils.toBooleanObject(val);
    }

    public int asInt(){
        return (int) Double.parseDouble(val);
    }

    public double asDouble(){
        return Double.parseDouble(val);
    }

    public float asFloat(){
        return Float.parseFloat(val);
    }

    public long asLong(){
        return (long) Double.parseDouble(val);
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
