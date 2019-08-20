package dev.anhcraft.abm.api.inventory;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ItemStorage {
    private Map<String, Long> MAP = new LinkedHashMap<>();

    public void put(String id){
        MAP.put(id, System.currentTimeMillis());
    }

    public void put(String id, long owningDate) {
        MAP.putIfAbsent(id, owningDate);
    }

    public void remove(String id){
        MAP.remove(id);
    }

    public boolean has(String id){
        return MAP.containsKey(id);
    }

    @Nullable
    public Long get(String id){
        return MAP.get(id);
    }

    public List<Map.Entry<String, Long>> list(){
        return new ArrayList<>(MAP.entrySet());
    }
}
