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
