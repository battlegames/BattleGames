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
package dev.anhcraft.abm.api.misc.info;

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
