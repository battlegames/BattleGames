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

import dev.anhcraft.battle.api.inventory.item.ItemType;
import dev.anhcraft.jvmkit.utils.Condition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;

public class Backpack {
    private final Map<ItemType, Compartment> backend = Collections.synchronizedMap(new HashMap<>());

    @NotNull
    public Backpack.Compartment getStorage(@NotNull ItemType type){
        Condition.argNotNull("type", type);
        Compartment x = backend.get(type);
        if(x == null){
            x = new Compartment();
            backend.put(type, x);
        }
        return x;
    }

    public void clear(){
        backend.clear();
    }

    public void listStorage(@NotNull BiConsumer<ItemType, Compartment> consumer){
        Condition.argNotNull("consumer", consumer);
        backend.forEach(consumer);
    }

    public static class Compartment {
        private Map<String, Long> MAP = new LinkedHashMap<>();

        public void put(@Nullable String id){
            MAP.put(id, System.currentTimeMillis());
        }

        public void put(@Nullable String id, long owningDate) {
            MAP.putIfAbsent(id, owningDate);
        }

        public void remove(@Nullable String id){
            MAP.remove(id);
        }

        @Nullable
        public Long get(@Nullable String id){
            return MAP.get(id);
        }

        public void list(@NotNull BiConsumer<String, Long> consumer){
            Condition.argNotNull("consumer", consumer);
            MAP.forEach(consumer);
        }

        @NotNull
        public Collection<Map.Entry<String, Long>> list(){
            return MAP.entrySet();
        }
    }
}
