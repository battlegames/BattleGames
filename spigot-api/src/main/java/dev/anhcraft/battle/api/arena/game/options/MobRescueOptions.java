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
import dev.anhcraft.config.annotations.*;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("FieldMayBeFinal")
@Configurable
public class MobRescueOptions extends GameOptions {
    @Setting
    @Path("extra_farmer_countdown_time")
    @Description("Extra countdown time for farmers")
    private long extraCountdownTimeFarmer;

    @Setting
    @Path("playing_spawn_points_farmer")
    @Description("The spawn points of team Farmer (in playing phase)")
    @Validation(notNull = true, silent = true)
    private List<String> playSpawnPointsFarmer = new ArrayList<>();

    @Setting
    @Path("playing_spawn_points_thief")
    @Description("The spawn points of team Thief (in playing phase)")
    @Validation(notNull = true, silent = true)
    private List<String> playSpawnPointsThief = new ArrayList<>();

    @Setting
    @Path("mob_groups")
    @Description("Configuration for mobs")
    @Validation(notNull = true)
    private Map<String, MobGroup> mobGroups;

    @Setting
    @Path("weight_speed_ratio")
    @Description({
            "Ratio between mob weight and speed reduction",
            "Take an example, when the ratio is 4000",
            "if a player carries a cow weighting 500 (kg)",
            "his speed will be reduced by 500/4000 = 0.125",
            "Note: the default speed is 0.2"
    })
    private double weightSpeedRatio;

    @Setting
    @Path("gathering_region.corner_1")
    @Description("First corner of the gathering region")
    @Validation(notNull = true)
    private String gatheringRegionCorner1;

    @Setting
    @Path("gathering_region.corner_2")
    @Description("Second corner of the gathering region")
    @Validation(notNull = true)
    private String gatheringRegionCorner2;

    @Setting
    @Validation(notNull = true)
    private Map<EntityType, MobRescueObjective> objectives;

    @Setting
    @Path("sounds.on_pick_up_mob")
    @Description("Sound to be played on picking up mobs")
    private BattleSound pickUpMobSound;

    @Setting
    @Path("sounds.on_put_down_mob")
    @Description("Sound to be played on putting down mobs")
    private BattleSound putDownMobSound;

    @Setting
    @Path("sounds.extra_countdown")
    @Description("Sound during extra countdown phrase")
    private BattleSound extraCountdownSound;

    public long getExtraCountdownTimeFarmer() {
        return extraCountdownTimeFarmer;
    }

    @NotNull
    public List<Location> getPlaySpawnPoints(@NotNull MRTeam team) {
        return (team == MRTeam.FARMER ? playSpawnPointsFarmer : playSpawnPointsThief).stream().map(LocationUtil::fromString).collect(Collectors.toList());
    }

    @NotNull
    public Collection<MobGroup> getMobGroups() {
        return mobGroups.values();
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
}
