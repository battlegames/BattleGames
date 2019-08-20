package dev.anhcraft.abm.api.inventory;

import dev.anhcraft.abm.api.inventory.items.ItemType;

import java.util.*;

public class PlayerInventory {
    private final Map<ItemType, ItemStorage> INV = Collections.synchronizedMap(new HashMap<>());

    public ItemStorage getStorage(ItemType type){
        ItemStorage x = INV.get(type);
        if(x == null){
            x = new ItemStorage();
            INV.put(type, x);
        }
        return x;
    }

    public void clear(){
        INV.clear();
    }

    public List<Map.Entry<ItemType, ItemStorage>> getAllStorage(){
        return new ArrayList<>(INV.entrySet());
    }
}
