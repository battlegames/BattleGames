package dev.anhcraft.abm.utils.info;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class InfoHolder {
    private final Map<String, InfoData> map = new HashMap<>();
    private String identifier;

    public InfoHolder(@NotNull String identifier) {
        Validate.notNull(identifier);
        this.identifier = identifier;
    }

    public InfoHolder inform(String key, boolean val){
        map.put(identifier + key, new InfoBooleanData(val));
        return this;
    }

    public InfoHolder inform(String key, int val){
        map.put(identifier + key, new InfoIntData(val));
        return this;
    }

    public InfoHolder inform(String key, long val){
        map.put(identifier + key, new InfoLongData(val));
        return this;
    }

    public InfoHolder inform(String key, double val){
        map.put(identifier + key, new InfoDoubleData(val));
        return this;
    }

    public InfoHolder inform(String key, String val){
        map.put(identifier + key, new InfoStringData(val));
        return this;
    }

    public InfoHolder inform(String key, String... val){
        map.put(identifier + key, new InfoStringData(String.join(", ", val)));
        return this;
    }

    public InfoHolder inform(String key, Iterable<String> val){
        map.put(identifier + key, new InfoStringData(String.join(", ", val)));
        return this;
    }

    public InfoHolder link(@Nullable InfoHolder another){
        if(another != null) map.putAll(another.map);
        return this;
    }

    @NotNull
    public Map<String, InfoData> read() {
        return Collections.unmodifiableMap(map);
    }
}
