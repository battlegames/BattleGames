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

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ColorUtil {
    @Nullable
    public static Object colorize(@Nullable Object value) {
        if (value != null) {
            if (value instanceof String) {
                return ChatUtil.formatColorCodes((String) value);
            } else if (value instanceof List) {
                List<Object> list = (List<Object>) value;
                if (!list.isEmpty() && list.get(0) instanceof String) {
                    list.replaceAll(o -> ChatUtil.formatColorCodes((String) o));
                    return list;
                }
            } else if (value instanceof ConfigurationSection) {
                ConfigurationSection cs = (ConfigurationSection) value;
                // we will do recursion, don't do getKeys(true)!!!
                for (String s : cs.getKeys(false)) {
                    Object k = cs.get(s);
                    cs.set(s, colorize(k));
                }
                return cs;
            }
        }
        return value;
    }

    @Nullable
    public static Object uncolorize(@Nullable Object value) {
        if (value != null) {
            if (value instanceof String) {
                return ((String) value).replace(ChatColor.COLOR_CHAR, '&');
            } else if (value instanceof List) {
                List<Object> list = (List<Object>) value;
                if (!list.isEmpty() && list.get(0) instanceof String) {
                    list.replaceAll(o -> ((String) o).replace(ChatColor.COLOR_CHAR, '&'));
                    return list;
                }
            } else if (value instanceof ConfigurationSection) {
                ConfigurationSection cs = (ConfigurationSection) value;
                // we will do recursion, don't do getKeys(true)!!!
                for (String s : cs.getKeys(false)) {
                    Object k = cs.get(s);
                    cs.set(s, uncolorize(k));
                }
                return cs;
            }
        }
        return value;
    }
}
