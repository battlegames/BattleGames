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
package dev.anhcraft.battle.utils.info;

import dev.anhcraft.battle.api.BattleApi;
import dev.anhcraft.jvmkit.utils.Condition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class InfoHolder {
    private final Map<String, Object> map = new HashMap<>();
    private String identifier;

    public InfoHolder(@NotNull String identifier) {
        Condition.argNotNull("identifier", identifier);
        this.identifier = identifier;
    }

    public InfoHolder inform(String key, @NotNull State val){
        Condition.argNotNull("val", val);
        map.put(identifier + key, val);
        return this;
    }

    public InfoHolder inform(String key, int val){
        map.put(identifier + key, val);
        return this;
    }

    public InfoHolder inform(String key, long val){
        map.put(identifier + key, val);
        return this;
    }

    public InfoHolder inform(String key, double val){
        map.put(identifier + key, val);
        return this;
    }

    public InfoHolder inform(String key, float val){
        map.put(identifier + key, val);
        return this;
    }

    public InfoHolder inform(String key, @NotNull String val){
        Condition.argNotNull("val", val);
        map.put(identifier + key, val);
        return this;
    }

    public InfoHolder inform(String key, @NotNull String... val){
        Condition.argNotNull("val", val);
        map.put(identifier + key, String.join(", ", val));
        return this;
    }

    public InfoHolder inform(String key, @NotNull Iterable<String> val){
        Condition.argNotNull("val", val);
        map.put(identifier + key, String.join(", ", val));
        return this;
    }

    public InfoHolder link(@Nullable InfoHolder another){
        if(another != null) map.putAll(another.map);
        return this;
    }

    @NotNull
    public Map<String, Object> getMap() {
        return map;
    }

    @NotNull
    public Map<String, String> mapInfo() {
        return BattleApi.getInstance().mapInfo(this);
    }

    @NotNull
    public InfoReplacer compile() {
        return new InfoReplacer(this);
    }
}
