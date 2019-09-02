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
