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

package dev.anhcraft.battle.api;

import dev.anhcraft.config.annotations.*;
import org.bukkit.WeatherType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldMayBeFinal")
@Configurable
public class WorldSettings {
    @Setting
    @Path("prevent_mob_drops")
    @Description("Should mob drops prevented")
    private boolean preventMobDrops;

    @Setting
    @Path("prevent_explosions")
    @Description("Should explosions from TNT, creeper, etc prevented")
    private boolean preventExplosions;

    @Setting
    @Path("protect_blocks")
    @Description({
            "Should blocks are protected",
            "WARNING: This option should be <b>disabled</b> if you have BedwarPack <b>enabled in these worlds</b>"
    })
    private boolean protectBlocks;

    @Setting
    @Path("always_weather")
    @Description("Set permanent weather type")
    private WeatherType alwaysWeather;

    @Setting
    @Path("always_time")
    @Description("Set permanent world time")
    private long alwaysTime = -1;

    @Setting
    @Path("prevent_hungry")
    @Description("Players won't feel hungry")
    private boolean preventHungry;

    @Setting
    @Path("prevent_mob_spawn")
    @Description("Prevent natural mob spawns")
    private boolean preventMobSpawn;

    @Setting
    @Path("anti_fire_spread")
    @Description("Prevent fire from spreading")
    private boolean antiFireSpread;

    @Setting
    @Path("disable_interact")
    @Description("Disables interacts with blocks like opening door, trapdoor, etc")
    private boolean disableInteract;

    @Setting
    @Path("disable_crafting")
    @Description("Disables crafting")
    private boolean disableCrafting;

    @Setting
    @Path("except_worlds")
    @Validation(notNull = true, silent = true)
    @Description("Specify worlds that won't be affected by this settings")
    private List<String> blacklistWorlds = new ArrayList<>();

    public boolean isPreventMobDrops() {
        return preventMobDrops;
    }

    public void setPreventMobDrops(boolean preventMobDrops) {
        this.preventMobDrops = preventMobDrops;
    }

    public boolean isPreventExplosions() {
        return preventExplosions;
    }

    public void setPreventExplosions(boolean preventExplosions) {
        this.preventExplosions = preventExplosions;
    }

    public boolean isProtectBlocks() {
        return protectBlocks;
    }

    public void setProtectBlocks(boolean protectBlocks) {
        this.protectBlocks = protectBlocks;
    }

    @Nullable
    public WeatherType getAlwaysWeather() {
        return alwaysWeather;
    }

    public void setAlwaysWeather(@Nullable WeatherType alwaysWeather) {
        this.alwaysWeather = alwaysWeather;
    }

    public long getAlwaysTime() {
        return alwaysTime;
    }

    public void setAlwaysTime(long alwaysTime) {
        this.alwaysTime = alwaysTime;
    }

    @NotNull
    public List<String> getBlacklistWorlds() {
        return blacklistWorlds;
    }

    public boolean isPreventHungry() {
        return preventHungry;
    }

    public void setPreventHungry(boolean preventHungry) {
        this.preventHungry = preventHungry;
    }

    public boolean isAntiFireSpread() {
        return antiFireSpread;
    }

    public void setAntiFireSpread(boolean antiFireSpread) {
        this.antiFireSpread = antiFireSpread;
    }

    public boolean isPreventMobSpawn() {
        return preventMobSpawn;
    }

    public void setPreventMobSpawn(boolean preventMobSpawn) {
        this.preventMobSpawn = preventMobSpawn;
    }

    public boolean isInteractDisabled() {
        return disableInteract;
    }

    public void setInteractDisabled(boolean disableInteract) {
        this.disableInteract = disableInteract;
    }

    public boolean isDisableCrafting() {
        return disableCrafting;
    }

    public void setDisableCrafting(boolean disableCrafting) {
        this.disableCrafting = disableCrafting;
    }
}
