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
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.anhcraft.battle.api.arena.team.ABTeam;
import dev.anhcraft.battle.api.arena.team.MRTeam;
import dev.anhcraft.battle.api.inventory.item.ItemType;
import dev.anhcraft.config.bukkit.NMSVersion;
import dev.anhcraft.jvmkit.utils.FileUtil;
import dev.anhcraft.jvmkit.utils.HttpUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

public class LocaleConfigManager extends ConfigManager {
    private static final Map<NMSVersion, String> ASSETS_VERSION = new EnumMap<>(NMSVersion.class);
    private static final Gson GSON = new Gson();

    static {
        ASSETS_VERSION.put(NMSVersion.current(), "1.12.2"); // set default

        ASSETS_VERSION.put(NMSVersion.v1_12_R1, "1.12.2");
        ASSETS_VERSION.put(NMSVersion.v1_13_R1, "1.13");
        ASSETS_VERSION.put(NMSVersion.v1_13_R2, "1.13.2");
        ASSETS_VERSION.put(NMSVersion.v1_14_R1, "1.14.4");
        ASSETS_VERSION.put(NMSVersion.v1_15_R1, "1.15.2");
        ASSETS_VERSION.put(NMSVersion.v1_16_R1, "1.16.1");
        ASSETS_VERSION.put(NMSVersion.v1_16_R2, "1.16.2");
        ASSETS_VERSION.put(NMSVersion.v1_16_R3, "1.16.5");
        ASSETS_VERSION.put(NMSVersion.v1_17_R1, "1.17.1");
        ASSETS_VERSION.put(NMSVersion.v1_18_R1, "1.18.1");
        ASSETS_VERSION.put(NMSVersion.v1_18_R2, "1.18.2");
        ASSETS_VERSION.put(NMSVersion.v1_19_R1, "1.19");
    }

    public LocaleConfigManager() {
        super("Locale", "locale/en_us.yml");
        setCompareDefault(true);
    }

    @NotNull
    protected String buildResourcePath() {
        return "config/locale/" + plugin.generalConf.getLocaleFile();
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
        if (itemTypeSec != null) {
            for (ItemType t : ItemType.values()) {
                String n = itemTypeSec.getString(t.name().toLowerCase());
                if (n != null) {
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
        //noinspection UnstableApiUsage
        File f = new File(plugin.configFolder, "locale/minecraft/" + version.hashCode() + "." + Files.getNameWithoutExtension(plugin.generalConf.getLocaleFile()) + ".json");
        try {
            if (f.exists()) {
                plugin.minecraftLocale = GSON.fromJson(FileUtil.readText(f), JsonObject.class);
            } else {
                f.createNewFile();
                //noinspection UnstableApiUsage
                String locale = Files.getNameWithoutExtension(plugin.generalConf.getLocaleFile());
                plugin.getLogger().warning("[" + loggerName + "] Downloading Minecraft language file...");
                plugin.getLogger().warning("- Hash: " + version.hashCode());
                plugin.getLogger().warning("- Locale: " + locale);
                if (NMSVersion.current().compare(NMSVersion.v1_13_R1) >= 0) {
                    String str = HttpUtil.fetchString("https://assets.mcasset.cloud/" + version + "/assets/minecraft/lang/" + locale + ".json");
                    FileUtil.write(f, str);
                    plugin.minecraftLocale = GSON.fromJson(str, JsonObject.class);
                } else {
                    String str = HttpUtil.fetchString("https://assets.mcasset.cloud/" + version + "/assets/minecraft/lang/" + locale + ".lang");
                    Properties p = new Properties();
                    p.load(new StringReader(str));
                    plugin.minecraftLocale = new JsonObject();
                    for (Map.Entry<Object, Object> e : p.entrySet()) {
                        plugin.minecraftLocale.addProperty(String.valueOf(e.getKey()), String.valueOf(e.getValue()));
                    }
                    FileUtil.write(f, GSON.toJson(plugin.minecraftLocale));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClean() {

    }
}
