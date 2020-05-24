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

import dev.anhcraft.battle.api.SystemConfig;
import dev.anhcraft.battle.utils.ConfigUpdater;
import dev.anhcraft.confighelper.ConfigHelper;
import dev.anhcraft.confighelper.exception.InvalidValueException;
import dev.anhcraft.craftkit.common.utils.VersionUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class SystemConfigManager extends ConfigManager {
    public SystemConfigManager() {
        super("system.yml");
        setPreventRemote(true);
    }

    @Override
    @NotNull
    protected File buildConfigFile() {
        return new File(plugin.getDataFolder(), getFilePath());
    }

    @Override
    public void onLoad() {
        ConfigUpdater configUpdater = new ConfigUpdater(plugin.getLogger());
        configUpdater.getPathRelocating().add(new ConfigUpdater.PathRelocating().type(String.class).oldPath("config_version").newPath("last_config_version"));
        configUpdater.getPathRelocating().add(new ConfigUpdater.PathRelocating().type(String.class).oldPath("storage_version").newPath("last_storage_version"));
        configUpdater.getPathRelocating().add(new ConfigUpdater.PathRelocating().type(String.class).oldPath("plugin_version").newPath("last_plugin_version"));
        configUpdater.update(getSettings());

        try {
            SystemConfig config = ConfigHelper.readConfig(getSettings(), SystemConfig.SCHEMA, plugin.getSystemConfig());

            if(VersionUtil.compareVersion(config.getLastPluginVersion(), "1.1.9") < 0){
                plugin.getLogger().warning("ATTENTION! It looks like you have updated the plugin from an older version!");
                plugin.getLogger().warning("You should be noticed that the new version will have massive changes to the configuration");
                plugin.getLogger().warning("Therefore, it is recommended to upgrade your config manually with the following steps:");
                plugin.getLogger().warning("1. Backup all the config files");
                plugin.getLogger().warning("2. Remove the entire Battle folder");
                plugin.getLogger().warning("3. Check out the new files");
                plugin.getLogger().warning("4. Compare with the old files");
                plugin.getLogger().warning("5. Re-configure");
                plugin.getLogger().warning("If you need help, contact me via Discord: https://discord.gg/QSpc5xH");
            }

            if(!config.isRemoteConfigEnabled()) {
                String cf = config.getConfigFolder().trim();
                if (cf.isEmpty()) {
                    // reset the config folder (in case of it was changed from a different path)
                    plugin.configFolder = plugin.getDataFolder();
                } else {
                    File file = new File(cf);
                    if (file.exists()) {
                        if (file.isDirectory()) {
                            plugin.configFolder = file;
                            plugin.getLogger().info("Now using config folder: " + file.getAbsolutePath());
                        } else
                            // reset the config folder
                            plugin.configFolder = plugin.getDataFolder();
                            plugin.getLogger().warning("Config folder is not an directory: " + file.getAbsolutePath());
                    } else {
                        plugin.configFolder = file;
                    }
                }
            }

            plugin.configFolder.mkdir();
            new File(plugin.configFolder, "locale").mkdir();
            new File(plugin.configFolder, "items").mkdir();
            new File(plugin.configFolder, "editor").mkdir();
        } catch (InvalidValueException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClean() {

    }
}
