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

package dev.anhcraft.battle.system.managers.config;

import dev.anhcraft.battle.api.WorldSettings;
import dev.anhcraft.battle.utils.ConfigHelper;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class WorldConfigManager extends ConfigManager {
    private final Map<String, WorldSettings> worldSettingsMap = new HashMap<>();
    private WorldSettings globalWorldSettings;

    public WorldConfigManager() {
        super("World", "worlds.yml");
    }

    private static void fillOptions(ConfigurationSection model, ConfigurationSection target) {
        for (String s : model.getKeys(false)) {
            if (!target.isSet(s)) {
                target.set(s, model.get(s));
            }
        }
    }

    @Nullable
    public WorldSettings getWorldSettings(String world) {
        WorldSettings ws = worldSettingsMap.get(world);
        if (ws != null) return ws;
        return globalWorldSettings.getBlacklistWorlds().contains(world) ? null : globalWorldSettings;
    }

    @Override
    protected void onLoad() {
        worldSettingsMap.clear();
        ConfigurationSection gen = getSettings().getConfigurationSection("general");
        globalWorldSettings = ConfigHelper.load(WorldSettings.class, gen);
        for (String k : Objects.requireNonNull(getSettings().getConfigurationSection("specific")).getKeys(false)) {
            ConfigurationSection s = getSettings().getConfigurationSection("specific." + k);
            fillOptions(gen, s);
            worldSettingsMap.put(k, ConfigHelper.load(WorldSettings.class, Objects.requireNonNull(s)));
        }
    }

    @Override
    protected void onClean() {
        worldSettingsMap.clear();
    }
}
