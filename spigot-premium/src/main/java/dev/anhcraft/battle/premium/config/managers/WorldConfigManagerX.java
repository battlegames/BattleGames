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

package dev.anhcraft.battle.premium.config.managers;

import dev.anhcraft.battle.premium.config.WorldSettings;
import dev.anhcraft.battle.system.managers.config.ConfigManager;
import dev.anhcraft.confighelper.ConfigHelper;
import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.exception.InvalidValueException;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class WorldConfigManagerX extends ConfigManager {
    private final Map<String, WorldSettings> worldSettingsMap = new HashMap<>();
    private WorldSettings globalWorldSettings;

    public WorldConfigManagerX() {
        super("Premium/World", "premium/worlds.yml");
    }

    @Nullable
    public WorldSettings getWorldSettings(String world){
        WorldSettings ws = worldSettingsMap.get(world);
        if(ws != null) return ws;
        return globalWorldSettings.getBlacklistWorlds().contains(world) ? null : globalWorldSettings;
    }

    private static void fillOptions(ConfigurationSection model, ConfigurationSection target){
        for(String s : model.getKeys(false)){
            if(!target.isSet(s)){
                target.set(s, model.get(s));
            }
        }
    }

    @Override
    protected void onLoad() {
        try {
            ConfigurationSection gen = getSettings().getConfigurationSection("general");
            globalWorldSettings = ConfigHelper.readConfig(Objects.requireNonNull(gen), ConfigSchema.of(WorldSettings.class));
            for(String k : Objects.requireNonNull(getSettings().getConfigurationSection("specific")).getKeys(false)){
                ConfigurationSection s = getSettings().getConfigurationSection("specific."+k);
                fillOptions(gen, s);
                worldSettingsMap.put(k, ConfigHelper.readConfig(Objects.requireNonNull(s), ConfigSchema.of(WorldSettings.class)));
            }
        } catch (InvalidValueException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onClean() {
        worldSettingsMap.clear();
    }
}
