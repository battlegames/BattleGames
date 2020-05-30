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

import dev.anhcraft.battle.api.arena.team.ABTeam;
import dev.anhcraft.battle.api.inventory.item.ItemType;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;

public class LocaleConfigManager extends ConfigManager {
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

        ABTeam.TEAM_A.setLocalizedName(Objects.requireNonNull(getSettings().getString("ab_team.team_a")));
        ABTeam.TEAM_B.setLocalizedName(Objects.requireNonNull(getSettings().getString("ab_team.team_b")));
    }

    @Override
    public void onClean() {

    }
}
