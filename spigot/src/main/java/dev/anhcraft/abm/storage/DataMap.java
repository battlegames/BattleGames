package dev.anhcraft.abm.storage;

import dev.anhcraft.abm.storage.tags.*;
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
