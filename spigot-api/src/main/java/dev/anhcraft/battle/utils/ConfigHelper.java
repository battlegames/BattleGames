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

import com.google.common.collect.Multimap;
import dev.anhcraft.battle.api.inventory.item.BattleItemModel;
import dev.anhcraft.battle.utils.adapters.BattleItemModelAdapter;
import dev.anhcraft.battle.utils.adapters.MultimapAdapter;
import dev.anhcraft.config.ConfigDeserializer;
import dev.anhcraft.config.ConfigSerializer;
import dev.anhcraft.config.bukkit.BukkitConfigProvider;
import dev.anhcraft.config.bukkit.struct.YamlConfigSection;
import dev.anhcraft.config.schema.SchemaScanner;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Objects;

public class ConfigHelper {
    public static final ConfigSerializer SERIALIZER;
    public static final ConfigDeserializer DESERIALIZER;

    static {
        SERIALIZER = BukkitConfigProvider.YAML.createSerializer();
        SERIALIZER.registerTypeAdapter(Multimap.class, MultimapAdapter.INSTANCE);
        SERIALIZER.registerTypeAdapter(BattleItemModel.class, BattleItemModelAdapter.INSTANCE);

        DESERIALIZER = BukkitConfigProvider.YAML.createDeserializer();
        DESERIALIZER.registerTypeAdapter(Multimap.class, MultimapAdapter.INSTANCE);
        DESERIALIZER.registerTypeAdapter(BattleItemModel.class, BattleItemModelAdapter.INSTANCE);
    }

    public static <T> T load(Class<T> clazz, ConfigurationSection section){
        try {
            return DESERIALIZER.transformConfig(Objects.requireNonNull(SchemaScanner.scanConfig(clazz)), new YamlConfigSection(section));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T load(Class<T> clazz, ConfigurationSection section, T dest){
        try {
            return DESERIALIZER.transformConfig(Objects.requireNonNull(SchemaScanner.scanConfig(clazz)), new YamlConfigSection(section), dest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> void save(Class<T> clazz, ConfigurationSection section, T dest){
        try {
            SERIALIZER.transformConfig(Objects.requireNonNull(SchemaScanner.scanConfig(clazz)), new YamlConfigSection(section), dest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
