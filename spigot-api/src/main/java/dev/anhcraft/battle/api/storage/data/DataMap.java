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
package dev.anhcraft.battle.api.storage.data;

import dev.anhcraft.battle.api.storage.tags.*;
import dev.anhcraft.jvmkit.utils.DataTypeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

public class DataMap<T> {
    private Map<T, DataTag> map = new HashMap<>();
    private AtomicBoolean changed = new AtomicBoolean();

    @Nullable
    public <C> C readTag(T key, Class<? extends C> clazz){
        return readTag(key, clazz, null);
    }

    @NotNull
    public <C> C readTag(T key, Class<? extends C> clazz, C def){
        Class<?> c = DataTypeUtil.getObjectClass(clazz);
        DataTag q = map.get(key);
        if(q == null) return def;
        Object a = q.getValue();
        return c.isAssignableFrom(a.getClass()) ? (C) a : def;
    }

    @NotNull
    public <C> C readRequiredTag(T key, Class<? extends C> clazz){
        C v = readTag(key, clazz);
        return Objects.requireNonNull(v);
    }

    public void forEach(BiConsumer<T, DataTag> consumer){
        map.forEach(consumer);
    }

    public void put(T key, DataTag tag){
        if(!Objects.equals(tag, map.put(key, tag))) changed.set(true);
    }

    /**
     * @deprecated INTERNAL ONLY!
     */
    @Deprecated
    public void fastPut(T key, DataTag tag){
        map.put(key, tag);
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

    public AtomicBoolean hasChanged() {
        return changed;
    }

    public int size() {
        return map.size();
    }

    public Set<Map.Entry<T, DataTag>> entrySet() {
        return map.entrySet();
    }
}
