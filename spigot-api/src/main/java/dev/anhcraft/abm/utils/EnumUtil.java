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
package dev.anhcraft.abm.utils;

import dev.anhcraft.jvmkit.utils.Condition;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EnumUtil {
    @NotNull
    public static <E extends Enum> E getEnum(@NotNull E[] list, @Nullable String str){
        Condition.notNull(list);
        Condition.notEmpty(list);
        if(str == null) return list[0];

        str = str.toUpperCase();
        for(E e : list){
            if(e.name().equals(str)) return e;
        }
        StackTraceElement stacktrace = Thread.currentThread().getStackTrace()[2];
        E def = list[list.length-1];
        Bukkit.getLogger().warning(String.format("%s#%s() | Enum `%s` not found! Using default: `%s`", stacktrace.getClassName(), stacktrace.getMethodName(), str, def.name()));
        return def;
    }
}
