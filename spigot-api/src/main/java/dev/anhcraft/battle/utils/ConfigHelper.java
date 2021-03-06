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
import dev.anhcraft.battle.api.BattleSound;
import dev.anhcraft.battle.api.inventory.item.BattleItemModel;
import dev.anhcraft.battle.utils.adapters.*;
import dev.anhcraft.config.ConfigDeserializer;
import dev.anhcraft.config.ConfigSerializer;
import dev.anhcraft.config.adapters.defaults.EnumAdapter;
import dev.anhcraft.config.bukkit.BukkitConfigProvider;
import dev.anhcraft.config.bukkit.struct.YamlConfigSection;
import dev.anhcraft.config.schema.SchemaScanner;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;

import java.util.Objects;

public class ConfigHelper {
    public static final ConfigSerializer SERIALIZER;
    public static final ConfigDeserializer DESERIALIZER;

    static {
        SERIALIZER = BukkitConfigProvider.YAML.createSerializer();
        SERIALIZER.registerTypeAdapter(Multimap.class, MultimapAdapter.INSTANCE);
        SERIALIZER.registerTypeAdapter(BattleItemModel.class, BattleItemModelAdapter.INSTANCE);
        SERIALIZER.registerTypeAdapter(BattleSound.class, BattleSoundAdapter.INSTANCE);
        SERIALIZER.registerTypeAdapter(Material.class, new MaterialAdapter());
        SERIALIZER.registerTypeAdapter(Enchantment.class, new EnchantmentAdapter());

        DESERIALIZER = BukkitConfigProvider.YAML.createDeserializer();
        DESERIALIZER.registerTypeAdapter(Multimap.class, MultimapAdapter.INSTANCE);
        DESERIALIZER.registerTypeAdapter(BattleItemModel.class, BattleItemModelAdapter.INSTANCE);
        DESERIALIZER.registerTypeAdapter(BattleSound.class, BattleSoundAdapter.INSTANCE);
        DESERIALIZER.registerTypeAdapter(Material.class, new MaterialAdapter());
        DESERIALIZER.registerTypeAdapter(Enchantment.class, new EnchantmentAdapter());
        EnumAdapter ea = new EnumAdapter();
        ea.preferUppercase(true);
        DESERIALIZER.registerTypeAdapter(Enum.class, ea);
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
