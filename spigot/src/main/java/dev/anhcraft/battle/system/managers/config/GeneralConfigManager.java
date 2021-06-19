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

import dev.anhcraft.battle.api.GeneralConfig;
import dev.anhcraft.battle.api.storage.StorageType;
import dev.anhcraft.battle.system.ResourcePack;
import dev.anhcraft.battle.system.managers.BattleDataManager;
import dev.anhcraft.battle.utils.ConfigHelper;
import dev.anhcraft.jvmkit.utils.FileUtil;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.text.SimpleDateFormat;

public class GeneralConfigManager extends ConfigManager {
    public GeneralConfigManager() {
        super("General", "general.yml");
    }

    @Override
    public void onLoad() {
        StorageType oldStorageType = plugin.generalConf.getStorageType();
        ConfigHelper.load(GeneralConfig.class, getSettings(), plugin.generalConf);
        if (getReloadCount() == 0) {
            plugin.dataManager = new BattleDataManager(plugin, plugin.generalConf.getStorageType());
            boolean ok = false;
            if (plugin.generalConf.getStorageType() == StorageType.MYSQL) {
                if (plugin.premiumConnector.isSuccess()) {
                    ConfigurationSection dsp = plugin.generalConf.getStorageMySQLProperties();
                    String url = "jdbc:mysql://" +
                            plugin.generalConf.getStorageMySQLHost() + ':' +
                            plugin.generalConf.getStorageMySQLPort() + '/' +
                            plugin.generalConf.getStorageMySQLDatabase();
                    plugin.dataManager.initMySQLStorage(
                            url,
                            plugin.generalConf.getStorageMySQLUser(),
                            plugin.generalConf.getStorageMySQLPass(),
                            dsp
                    );
                    plugin.syncDataTaskNeed = true;
                    ok = true;
                } else {
                    plugin.getLogger().warning("MySQL support is disabled in free version.");
                }
            }
            if (!ok) {
                File old = new File(".battle");
                if (old.exists()) {
                    plugin.getLogger().warning("Starting from v1.2.2, the default name for data folder is battle (without dot '.')");
                    plugin.getLogger().warning("This change is necessary since the old one not work for certain file systems.");
                    plugin.getLogger().warning("Trying to upgrade the data folder...");
                    File f = new File("battle");
                    f.mkdirs();
                    plugin.getLogger().warning("-- Copying old files to the new folder (1/3)...");
                    FileUtil.copy(old, f);
                    plugin.getLogger().warning("-- Cleaning old files (2/3)...");
                    FileUtil.clean(old);
                    plugin.getLogger().warning("-- Deleting old folder (3/3)...");
                    old.delete();
                    plugin.getLogger().warning("All done! :D");
                    plugin.dataManager.initFileStorage(f);
                } else {
                    String s = plugin.generalConf.getStorageFilePath().trim();
                    if (s.equals(".battle")) {
                        plugin.getLogger().warning("Please change in general.yml (storage.file.data_path) from .battle -> battle");
                        s = "battle";
                    }
                    File f = new File(s);
                    f.mkdirs();
                    plugin.dataManager.initFileStorage(f);
                }
            }
            plugin.dataManager.loadServerData();
        } else if (oldStorageType != plugin.generalConf.getStorageType()) {
            plugin.getLogger().warning("[" + loggerName + "] Storage type can not be changed with reloads! Please restart the server.");
        }

        plugin.toLevelConverter = new ExpressionBuilder(plugin.generalConf.getExp2LvFormula()).variables("x").build();
        plugin.toExpConverter = new ExpressionBuilder(plugin.generalConf.getLv2ExpFormula()).variables("x").build();
        plugin.longFormDate = new SimpleDateFormat(plugin.generalConf.getDateFormatLong());
        plugin.shortFormDate1 = new SimpleDateFormat(plugin.generalConf.getDateFormatShortHours());
        plugin.shortFormDate2 = new SimpleDateFormat(plugin.generalConf.getDateFormatShortMinutes());
        plugin.shortFormDate3 = new SimpleDateFormat(plugin.generalConf.getDateFormatShortSeconds());

        if (plugin.generalConf.isBungeeEnabled()) {
            if (!plugin.premiumConnector.isSuccess()) {
                plugin.getLogger().warning("Bungeecord support is not provided in free version.");
            } else if (plugin.spigotBungeeEnabled) {
                plugin.supportBungee = true;
            } else {
                plugin.getLogger().warning("Looks like you have enabled Bungeecord support. But please also enable it in spigot.yml too. The option is now skipped for safe!");
            }
        }

        if (plugin.generalConf.isResourcePackEnabled()) {
            ResourcePack.init(s -> plugin.getLogger().info(s));
        }
    }

    @Override
    public void onClean() {
        plugin.supportBungee = false;
    }
}
