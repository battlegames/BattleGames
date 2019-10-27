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
package dev.anhcraft.battle.api.inventory;

import dev.anhcraft.battle.api.inventory.items.ItemType;
import dev.anhcraft.jvmkit.utils.Condition;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiConsumer;

public class PlayerInventory {
    private final Map<ItemType, ItemStorage> INV = Collections.synchronizedMap(new HashMap<>());

    @NotNull
    public ItemStorage getStorage(@NotNull ItemType type){
        Condition.argNotNull("type", type);
        ItemStorage x = INV.get(type);
        if(x == null){
            x = new ItemStorage();
            INV.put(type, x);
        }
        return x;
    }

    public void clearInventory(){
        INV.clear();
    }

    public void listStorage(@NotNull BiConsumer<ItemType, ItemStorage> consumer){
        Condition.argNotNull("consumer", consumer);
        INV.forEach(consumer);
    }
}
