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

import dev.anhcraft.battle.api.inventory.item.MagazineModel;
import dev.anhcraft.confighelper.ConfigHelper;
import dev.anhcraft.confighelper.exception.InvalidValueException;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MagazineConfigManager extends ConfigManager {
    public final Map<String, MagazineModel> MAGAZINE_MAP = new HashMap<>();

    public MagazineConfigManager() {
        super("Magazine", "items/magazines.yml");
    }

    @Override
    public void onLoad() {
        plugin.limit(loggerName, getSettings().getKeys(false), 15).forEach(s -> {
            MagazineModel m = new MagazineModel(s);
            ConfigurationSection cs = getSettings().getConfigurationSection(s);
            try {
                ConfigHelper.readConfig(Objects.requireNonNull(cs), MagazineModel.SCHEMA, m);
            } catch (InvalidValueException e) {
                e.printStackTrace();
            }
            MAGAZINE_MAP.put(s, m);
        });
    }

    @Override
    public void onClean() {
        MAGAZINE_MAP.clear();
    }
}
