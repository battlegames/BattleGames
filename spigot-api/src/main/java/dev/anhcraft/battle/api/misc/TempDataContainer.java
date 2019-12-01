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

package dev.anhcraft.battle.api.misc;

import dev.anhcraft.battle.api.misc.info.InfoHolder;
import dev.anhcraft.battle.api.misc.info.Informative;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class TempDataContainer implements Informative {
    private Map<String, Object> dataContainer;

    @NotNull
    public Map<String, Object> getDataContainer() {
        if(dataContainer == null){
            dataContainer = new HashMap<>();
        }
        return dataContainer;
    }

    @Override
    public void inform(@NotNull InfoHolder holder) {
        for(Map.Entry<String, Object> ent : getDataContainer().entrySet()){
            Object v = ent.getValue();
            if(v instanceof String){
                holder.inform(ent.getKey(), (String) v);
            }
            else if(v instanceof Boolean){
                holder.inform(ent.getKey(), (Boolean) v);
            }
            else if(v instanceof Integer){
                holder.inform(ent.getKey(), (Integer) v);
            }
            else if(v instanceof Double){
                holder.inform(ent.getKey(), (Double) v);
            }
            else if(v instanceof Long){
                holder.inform(ent.getKey(), (Long) v);
            }
        }
    }
}
