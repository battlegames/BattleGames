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

package dev.anhcraft.battle.premium.config;

import dev.anhcraft.battle.premium.system.PositionPair;
import dev.anhcraft.battle.utils.ConfigurableObject;
import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.annotation.Key;
import dev.anhcraft.confighelper.annotation.Schema;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldMayBeFinal")
@Schema
public class ArenaSettings extends ConfigurableObject {
    @Key("empty_regions")
    private List<PositionPair> emptyRegions;

    @Nullable
    public List<PositionPair> getEmptyRegions() {
        return emptyRegions;
    }

    @Nullable
    protected Object conf2schema(@Nullable Object value, ConfigSchema.Entry entry) {
        if (value != null && entry.getKey().equals("empty_regions")) {
            List<PositionPair> list = new ArrayList<>();
            ConfigurationSection section = (ConfigurationSection) value;
            for (String k : section.getKeys(false)) {
                ConfigurationSection v = section.getConfigurationSection(k);
                list.add(new PositionPair(v.getString("corner_1"), v.getString("corner_2")));
            }
            return list;
        }
        return value;
    }

    @Nullable
    protected Object schema2conf(@Nullable Object value, ConfigSchema.Entry entry) {
        if (value != null && entry.getKey().equals("empty_regions")) {
            YamlConfiguration section = new YamlConfiguration();
            List<PositionPair> list = (List<PositionPair>) value;
            int i = 0;
            for (PositionPair p : list) {
                YamlConfiguration x = new YamlConfiguration();
                x.set("corner_1", p.getFirst());
                x.set("corner_2", p.getSecond());
                section.set(String.valueOf(i), x);
                i++;
            }
        }
        return value;
    }
}
