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

package dev.anhcraft.battle.utils;

import dev.anhcraft.battle.utils.info.InfoHolder;
import dev.anhcraft.battle.impl.Informative;
import dev.anhcraft.battle.utils.info.State;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * A container for storing data.
 */
public class CustomDataContainer implements Informative {
    private Map<String, Object> dataContainer;

    /**
     * Gets the backend of this container (which is a HashMap).
     * @return backend map
     */
    @NotNull
    public Map<String, Object> getBackend() {
        if(dataContainer == null){
            dataContainer = new HashMap<>();
        }
        return dataContainer;
    }

    @Override
    public void inform(@NotNull InfoHolder holder) {
        for(Map.Entry<String, Object> ent : getBackend().entrySet()){
            Object v = ent.getValue();
            if(v instanceof String){
                holder.inform("data_"+ent.getKey(), (String) v);
            }
            else if(v instanceof Boolean){
                holder.inform("data_"+ent.getKey(), v.toString()); // fixed bug
            }
            else if(v instanceof State){
                holder.inform("data_"+ent.getKey(), (State) v);
            }
            else if(v instanceof Byte){
                holder.inform("data_"+ent.getKey(), (Byte) v);
            }
            else if(v instanceof Short){
                holder.inform("data_"+ent.getKey(), (Short) v);
            }
            else if(v instanceof Integer){
                holder.inform("data_"+ent.getKey(), (Integer) v);
            }
            else if(v instanceof Double){
                holder.inform("data_"+ent.getKey(), (Double) v);
            }
            else if(v instanceof Float){
                holder.inform("data_"+ent.getKey(), (Float) v);
            }
            else if(v instanceof Long){
                holder.inform("data_"+ent.getKey(), (Long) v);
            }
        }
    }
}
