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

import dev.anhcraft.battle.api.gui.Gui;
import dev.anhcraft.battle.utils.ConfigHelper;
import dev.anhcraft.battle.utils.ConfigUpdater;
import dev.anhcraft.craftkit.cb_common.NMSVersion;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class GUIConfigManager extends ConfigManager {
    public GUIConfigManager() {
        super("GUI", "gui.yml", (NMSVersion.current().compare(NMSVersion.v1_13_R1) >= 0 ? "gui.yml" : "gui.legacy.yml"));
    }

    @Override
    public void onLoad() {
        ConfigUpdater configUpdater = new ConfigUpdater(plugin.getLogger());
        configUpdater.getPathRelocating().add(
                new ConfigUpdater.PathRelocating()
                        .type(List.class)
                        .oldPath("*.components.*.functions")
                        .newPath("#0.components.#1.functions.on_click")
        );
        configUpdater.update(getSettings());
        getSettings().getKeys(false).forEach(s -> {
            ConfigurationSection cs = getSettings().getConfigurationSection(s);
            Gui gui = new Gui(s);
            ConfigHelper.load(Gui.class, cs, gui);
            plugin.guiManager.GUI.put(s, gui); // put directly for better performance
        });
    }

    @Override
    public void onClean() {
        plugin.guiManager.GUI.clear();
    }
}
