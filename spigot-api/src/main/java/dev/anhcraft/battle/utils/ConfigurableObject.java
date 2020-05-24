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

import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.annotation.Middleware;
import dev.anhcraft.confighelper.annotation.Schema;
import org.jetbrains.annotations.Nullable;

@Schema
public class ConfigurableObject {
    @Middleware(Middleware.Direction.CONFIG_TO_SCHEMA)
    @Nullable
    private Object c2s(ConfigSchema.Entry entry, @Nullable Object value){
        return conf2schema(ColorUtil.colorize(value), entry);
    }

    @Middleware(Middleware.Direction.SCHEMA_TO_CONFIG)
    @Nullable
    private Object s2c(ConfigSchema.Entry entry, @Nullable Object value){
        return ColorUtil.uncolorize(schema2conf(value, entry));
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
