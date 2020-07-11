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

import com.google.common.io.Files;
import com.google.gson.JsonObject;
import dev.anhcraft.battle.api.arena.team.ABTeam;
import dev.anhcraft.battle.api.arena.team.MRTeam;
import dev.anhcraft.battle.api.inventory.item.ItemType;
import dev.anhcraft.craftkit.cb_common.NMSVersion;
import dev.anhcraft.craftkit.common.internal.CKPlugin;
import dev.anhcraft.jvmkit.utils.FileUtil;
import dev.anhcraft.jvmkit.utils.HttpUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public class LocaleConfigManager extends ConfigManager {
    private static final Map<NMSVersion, String> ASSETS_VERSION = new EnumMap<>(NMSVersion.class);

    static {
        ASSETS_VERSION.put(NMSVersion.v1_12_R1, "1.12");
        ASSETS_VERSION.put(NMSVersion.v1_13_R1, "1.13");
        ASSETS_VERSION.put(NMSVersion.v1_13_R2, "1.13.2");
        ASSETS_VERSION.put(NMSVersion.v1_14_R1, "1.14.4");
        ASSETS_VERSION.put(NMSVersion.v1_15_R1, "1.15.2");
        ASSETS_VERSION.put(NMSVersion.v1_16_R1, "1.16.1");
    }

    public LocaleConfigManager() {
        super("Locale", "locale/en_us.yml");
        setCompareDefault(true);
    }

    @NotNull
    protected String buildResourcePath() {
        return  "config/locale/" + plugin.generalConf.getLocaleFile();
    }

    @NotNull
    protected File buildConfigFile() {
        return new File(plugin.configFolder, "locale/" + plugin.generalConf.getLocaleFile());
    }

    @NotNull
    protected String buildConfigURL() {
        return String.format(plugin.getSystemConfig().getRemoteConfigLink(), "locale/" + plugin.generalConf.getLocaleFile());
    }

    @Override
    public void onLoad() {
        ConfigurationSection itemTypeSec = getSettings().getConfigurationSection("item_type");
        if(itemTypeSec != null){
            for(ItemType t : ItemType.values()){
                String n = itemTypeSec.getString(t.name().toLowerCase());
                if(n != null) {
                    t.setLocalizedName(n);
                }
            }
        }

        ABTeam.TEAM_A.setLocalizedName(Objects.requireNonNull(getSettings().getString("ab_team.team_a", "")));
        ABTeam.TEAM_B.setLocalizedName(Objects.requireNonNull(getSettings().getString("ab_team.team_b", "")));
        MRTeam.THIEF.setLocalizedName(Objects.requireNonNull(getSettings().getString("mr_team.thief", "")));
        MRTeam.FARMER.setLocalizedName(Objects.requireNonNull(getSettings().getString("mr_team.farmer", "")));

        new File(plugin.configFolder, "locale/minecraft/").mkdirs();
        String version = ASSETS_VERSION.get(NMSVersion.current());
        File f = new File(plugin.configFolder, "locale/minecraft/" + version.hashCode() + "." + plugin.generalConf.getLocaleFile());
        try {
            if (f.exists()) {
                plugin.minecraftLocale = CKPlugin.GSON.fromJson(FileUtil.readText(f), JsonObject.class);
            } else {
                f.createNewFile();
                //noinspection UnstableApiUsage
                String locale = Files.getNameWithoutExtension(plugin.generalConf.getLocaleFile());
                plugin.getLogger().warning("[" + loggerName + "] Downloading Minecraft language file...");
                plugin.getLogger().warning("- Hash: " + version.hashCode());
                plugin.getLogger().warning("- Locale: " + locale);
                String str = HttpUtil.fetchString("https://assets.mcasset.cloud/" + version + "/assets/minecraft/lang/" + locale + ".json");
                FileUtil.write(f, str);
                plugin.minecraftLocale = CKPlugin.GSON.fromJson(str, JsonObject.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClean() {

    }
}
