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
package dev.anhcraft.abm.api.storage.data;

import dev.anhcraft.abm.api.storage.tags.*;
import org.apache.commons.lang.ClassUtils;

import java.util.HashMap;
import java.util.List;

public class DataMap<T> extends HashMap<T, DataTag> {
    @SuppressWarnings("unchecked")
    public <C> C readTag(T key, Class<? extends C> clazz){
        clazz = ClassUtils.primitiveToWrapper(clazz);
        DataTag q = get(key);
        if(q == null) return null;
        Object a = q.getValue();
        return clazz.isAssignableFrom(a.getClass()) ? (C) a : null;
    }

    public void writeTag(T key, boolean value){
        put(key, new BoolTag(value));
    }

    public void writeTag(T key, int value){
        put(key, new IntTag(value));
    }

    public void writeTag(T key, double value){
        put(key, new DoubleTag(value));
    }

    public void writeTag(T key, long value){
        put(key, new LongTag(value));
    }

    public void writeTag(T key, float value){
        put(key, new FloatTag(value));
    }

    public void writeTag(T key, String value){
        put(key, new StringTag(value));
    }

    public <C extends DataTag> void writeTag(T key, List<C> value){
        put(key, new ListTag<>(value));
    }
}
