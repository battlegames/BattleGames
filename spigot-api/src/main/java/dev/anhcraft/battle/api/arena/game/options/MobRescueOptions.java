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

package dev.anhcraft.battle.api.arena.game.options;

import dev.anhcraft.battle.api.BattleSound;
import dev.anhcraft.battle.api.arena.game.MobGroup;
import dev.anhcraft.battle.api.arena.game.MobRescueObjective;
import dev.anhcraft.battle.api.arena.team.MRTeam;
import dev.anhcraft.battle.utils.LocationUtil;
import dev.anhcraft.confighelper.ConfigHelper;
import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.annotation.*;
import dev.anhcraft.confighelper.exception.InvalidValueException;
import dev.anhcraft.jvmkit.utils.EnumUtil;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("FieldMayBeFinal")
@Schema
public class MobRescueOptions extends GameOptions {
    public static final ConfigSchema<MobRescueOptions> SCHEMA = ConfigSchema.of(MobRescueOptions.class);

    @Key("extra_farmer_countdown_time")
    @Explanation("Extra countdown time for farmers")
    private long extraCountdownTimeFarmer;

    @Key("playing_spawn_points_farmer")
    @Explanation("The spawn points of team Farmer (in playing phase)")
    @IgnoreValue(ifNull = true)
    private List<String> playSpawnPointsFarmer = new ArrayList<>();

    @Key("playing_spawn_points_thief")
    @Explanation("The spawn points of team Thief (in playing phase)")
    @IgnoreValue(ifNull = true)
    private List<String> playSpawnPointsThief = new ArrayList<>();

    @Key("mob_groups")
    @Explanation("Configuration for mobs")
    @Validation(notNull = true)
    private List<MobGroup> mobGroups;

    @Key("weight_speed_ratio")
    @Explanation({
            "Ratio between mob weight and speed reduction",
            "Take an example, when the ratio is 4000",
            "if a player carries a cow weighting 500 (kg)",
            "his speed will be reduced by 500/4000 = 0.125",
            "Note: the default speed is 0.2"
    })
    private double weightSpeedRatio;

    @Key("gathering_region.corner_1")
    @Explanation("First corner of the gathering region")
    @Validation(notNull = true)
    private String gatheringRegionCorner1;

    @Key("gathering_region.corner_2")
    @Explanation("Second corner of the gathering region")
    @Validation(notNull = true)
    private String gatheringRegionCorner2;

    @Key("objectives")
    @Validation(notNull = true)
    private Map<EntityType, MobRescueObjective> objectives;

    @Key("sounds.on_pick_up_mob")
    @Explanation("Sound to be played on picking up mobs")
    private BattleSound pickUpMobSound;

    @Key("sounds.on_put_down_mob")
    @Explanation("Sound to be played on putting down mobs")
    private BattleSound putDownMobSound;

    @Key("sounds.extra_countdown")
    @Explanation("Sound during extra countdown phrase")
    private BattleSound extraCountdownSound;

    public long getExtraCountdownTimeFarmer() {
        return extraCountdownTimeFarmer;
    }

    @NotNull
    public List<Location> getPlaySpawnPoints(@NotNull MRTeam team) {
        return (team == MRTeam.FARMER ? playSpawnPointsFarmer : playSpawnPointsThief).stream().map(LocationUtil::fromString).collect(Collectors.toList());
    }

    @NotNull
    public List<MobGroup> getMobGroups() {
        return mobGroups;
    }

    public double getWeightSpeedRatio() {
        return weightSpeedRatio;
    }

    @NotNull
    public Location getGatheringRegionCorner1() {
        return LocationUtil.fromString(gatheringRegionCorner1);
    }

    @NotNull
    public Location getGatheringRegionCorner2() {
        return LocationUtil.fromString(gatheringRegionCorner2);
    }

    @NotNull
    public Map<EntityType, MobRescueObjective> getObjectives() {
        return objectives;
    }

    @Nullable
    public BattleSound getPickUpMobSound() {
        return pickUpMobSound;
    }

    @Nullable
    public BattleSound getPutDownMobSound() {
        return putDownMobSound;
    }

    @Nullable
    public BattleSound getExtraCountdownSound() {
        return extraCountdownSound;
    }

    @Nullable
    protected Object conf2schema(@Nullable Object value, ConfigSchema.Entry entry) {
        if (value != null) {
            if (entry.getKey().equals("mob_groups")) {
                ConfigurationSection conf = (ConfigurationSection) value;
                List<MobGroup> mobGroups = new ArrayList<>();
                try {
                    for (String s : conf.getKeys(false)) {
                        mobGroups.add(ConfigHelper.readConfig(conf.getConfigurationSection(s), MobGroup.SCHEMA));
                    }
                } catch (InvalidValueException e) {
                    e.printStackTrace();
                }
                return mobGroups;
            }
            if (entry.getKey().equals("objectives")) {
                ConfigurationSection conf = (ConfigurationSection) value;
                Map<EntityType, MobRescueObjective> map = new EnumMap<>(EntityType.class);
                try {
                    for (String s : conf.getKeys(false)) {
                        map.put((EntityType) EnumUtil.findEnum(EntityType.class, s.toUpperCase()), ConfigHelper.readConfig(conf.getConfigurationSection(s), MobRescueObjective.SCHEMA));
                    }
                } catch (InvalidValueException e) {
                    e.printStackTrace();
                }
                return map;
            }
        }
        return value;
    }

    @Nullable
    protected Object schema2conf(@Nullable Object value, ConfigSchema.Entry entry) {
        if (value != null) {
            if (entry.getKey().equals("mob_groups")) {
                YamlConfiguration conf = new YamlConfiguration();
                int i = 0;
                for (MobGroup mg : (List<MobGroup>) value) {
                    YamlConfiguration c = new YamlConfiguration();
                    ConfigHelper.writeConfig(c, MobGroup.SCHEMA, mg);
                    conf.set(String.valueOf(i++), c);
                }
                return conf;
            }
            if (entry.getKey().equals("objectives")) {
                YamlConfiguration conf = new YamlConfiguration();
                for (Map.Entry<EntityType, MobRescueObjective> e : ((Map<EntityType, MobRescueObjective>) value).entrySet()) {
                    YamlConfiguration c = new YamlConfiguration();
                    ConfigHelper.writeConfig(c, MobRescueObjective.SCHEMA, e.getValue());
                    conf.set(e.getKey().name(), c);
                }
                return conf;
            }
        }
        return value;
    }
}
