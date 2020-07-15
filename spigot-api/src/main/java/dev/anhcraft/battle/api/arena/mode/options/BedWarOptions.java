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

package dev.anhcraft.battle.api.arena.mode.options;

import dev.anhcraft.battle.api.BattleApi;
import dev.anhcraft.confighelper.ConfigHelper;
import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.annotation.*;
import dev.anhcraft.confighelper.exception.InvalidValueException;
import org.bukkit.DyeColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldMayBeFinal")
@Schema
public class BedWarOptions extends ModeOptions {
    public static final ConfigSchema<BedWarOptions> SCHEMA = ConfigSchema.of(BedWarOptions.class);

    @Key("team_size")
    @Explanation("The size of a team")
    private int teamSize = 2;

    @Key("teams")
    @Explanation("List of teams")
    @Example({
            "teams:",
            "  '1':",
            "    name: \"&fWhite\"",
            "    color: white",
            "    spawn_points:",
            "    - lighthouse 30.39 65 -88.76 -0.45 -1.05",
            "    - lighthouse 27.80 65 -88.75 -0.45 -1.05",
            "    - lighthouse 29.36 65 -88.07 -0.45 -1.05",
            "    bed_location: lighthouse 29.53 66.56 -74.76 0 0",
            "  '2':",
            "    name: \"&bCyan\"",
            "    color: cyan",
            "    spawn_points:",
            "    - lighthouse -20.62 65 -88.37 -359.85 -0.30",
            "    - lighthouse -22.97 65 -88.38 -359.85 -0.30",
            "    - lighthouse -21.98 65 -87.81 -357.75 -0.60",
            "    bed_location: lighthouse -22.44 66.56 -76.01 0 0"
    })
    @IgnoreValue(ifNull = true)
    private final List<BWTeamOptions> teams = new ArrayList<>();

    public int getTeamSize() {
        return teamSize;
    }

    @NotNull
    public List<BWTeamOptions> getTeams() {
        return teams;
    }

    @Nullable
    protected Object conf2schema(@Nullable Object value, ConfigSchema.Entry entry){
        if(value != null && entry.getKey().equals("teams")){
            ConfigurationSection conf = (ConfigurationSection) value;
            List<BWTeamOptions> teams = new ArrayList<>();
            try {
                for (String s : conf.getKeys(false)) {
                    teams.add(ConfigHelper.readConfig(conf.getConfigurationSection(s), BWTeamOptions.SCHEMA));
                }
            } catch (InvalidValueException e) {
                e.printStackTrace();
            }
            boolean[] colors = new boolean[DyeColor.values().length];
            for (BWTeamOptions t : teams) {
                DyeColor c = t.getColor();
                if(colors[c.ordinal()]) {
                    BattleApi.getInstance().getLogger().warning("[BedWarValidator] Duplicated team color: " + c.name());
                } else {
                    colors[c.ordinal()] = true;
                }
            }
            return teams;
        }
        return value;
    }

    @Nullable
    protected Object schema2conf(@Nullable Object value, ConfigSchema.Entry entry){
        if(value != null && entry.getKey().equals("teams")){
            YamlConfiguration conf = new YamlConfiguration();
            int i = 0;
            for (BWTeamOptions tm : (List<BWTeamOptions>) value) {
                YamlConfiguration c = new YamlConfiguration();
                ConfigHelper.writeConfig(c, BWTeamOptions.SCHEMA, tm);
                conf.set(String.valueOf(i++), c);
            }
            return conf;
        }
        return value;
    }
}
