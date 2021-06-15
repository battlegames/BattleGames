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

import dev.anhcraft.battle.api.arena.Arena;
import dev.anhcraft.battle.premium.config.ArenaSettings;
import dev.anhcraft.battle.system.managers.config.ConfigManager;
import dev.anhcraft.confighelper.ConfigHelper;
import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.exception.InvalidValueException;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ArenaConfigManagerX extends ConfigManager {
    private final Map<String, ArenaSettings> arenaSettingsMap = new HashMap<>();

    public ArenaConfigManagerX() {
        super("Premium/Arena", "premium/arenas.yml");
    }

    @Nullable
    public ArenaSettings getArenaSettings(String arena) {
        return arenaSettingsMap.get(arena);
    }

    @Override
    protected void onLoad() {
        try {
            for (String k : getSettings().getKeys(false)) {
                Arena a = plugin.getArena(k);
                if (a == null) {
                    plugin.getLogger().warning("[" + loggerName + "] Arena #" + k + " not found.");
                    continue;
                }
                ConfigurationSection s = getSettings().getConfigurationSection(k);
                ArenaSettings as = ConfigHelper.readConfig(Objects.requireNonNull(s), ConfigSchema.of(ArenaSettings.class));
                arenaSettingsMap.put(k, as);
                if (a.getRollback() == null) {
                    plugin.getLogger().warning("[" + loggerName + "] You've not configured rollback system for arena #" + k + ". Some extra settings will be disabled for safe reasons.");
                    if (as.getEmptyRegions() != null) {
                        as.getEmptyRegions().clear();
                    }
                }
            }
        } catch (InvalidValueException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onClean() {
        arenaSettingsMap.clear();
    }
}
