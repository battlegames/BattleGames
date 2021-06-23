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

import dev.anhcraft.battle.api.advancement.Advancement;
import dev.anhcraft.battle.utils.ConfigHelper;
import org.bukkit.configuration.ConfigurationSection;

public class AdvancementConfigManager extends ConfigManager {
    public AdvancementConfigManager() {
        super("Advancement", "advancements.yml");
    }

    @Override
    public void onLoad() {
        getSettings().getKeys(false).forEach(s -> {
            Advancement ach = new Advancement(s);
            ConfigurationSection cs = getSettings().getConfigurationSection(s);
            ConfigHelper.load(Advancement.class, cs, ach);
            plugin.advancementManager.registerAdvancement(ach);
        });
    }

    @Override
    public void onClean() {
        plugin.advancementManager.clean();
    }
}
