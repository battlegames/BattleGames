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

import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.annotation.Middleware;
import dev.anhcraft.confighelper.annotation.Schema;
import dev.anhcraft.craftkit.common.utils.ChatUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Schema
public class ConfigurableObject {
    private Object colorize(Object value){
        if(value != null) {
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
                for (String s : cs.getKeys(false)) {
                    Object k = cs.get(s);
                    cs.set(s, colorize(k));
                }
                return cs;
            }
        }
        return value;
    }

    @Middleware(Middleware.Direction.CONFIG_TO_SCHEMA)
    @Nullable
    private Object c2s(ConfigSchema.Entry entry, @Nullable Object value){
        return conf2schema(colorize(value), entry);
    }

    @Middleware(Middleware.Direction.SCHEMA_TO_CONFIG)
    @Nullable
    private Object s2c(ConfigSchema.Entry entry, @Nullable Object value){
        return schema2conf(value, entry);
    }

    @Nullable
    protected Object conf2schema(@Nullable Object value, ConfigSchema.Entry entry){
        return value;
    }

    @Nullable
    protected Object schema2conf(@Nullable Object value, ConfigSchema.Entry entry){
        return value;
    }
}
