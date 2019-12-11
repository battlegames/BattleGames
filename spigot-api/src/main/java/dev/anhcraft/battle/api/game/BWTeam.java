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

package dev.anhcraft.battle.api.game;

import com.google.common.base.Preconditions;
import com.google.common.math.Stats;
import dev.anhcraft.battle.utils.EnumUtil;
import dev.anhcraft.battle.utils.LocationUtil;
import dev.anhcraft.craftkit.utils.BlockUtil;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BWTeam implements Team {
    private String name;
    private DyeColor color;
    private List<Location> spawnPoints;
    private Block bedPart1;
    private Block bedPart2;
    private Location centerSpawnPoint;

    @SuppressWarnings("UnstableApiUsage")
    public BWTeam(@NotNull ConfigurationSection c) {
        Preconditions.checkNotNull(c);

        this.name = c.getString("name");
        this.color = EnumUtil.getEnum(DyeColor.values(), c.getString("color"));
        this.spawnPoints = c.getStringList("spawn_points").stream()
                .map(LocationUtil::fromString)
                .collect(Collectors.toList());
        Location bl = LocationUtil.fromString(c.getString("bed_location"));
        Preconditions.checkNotNull(bl);
        for (Block b : BlockUtil.getNearbyBlocks(bl, 2, 2, 2)){
            if(b.getType().name().equals("BED_BLOCK") || b.getType().name().endsWith("_BED")){
                if(this.bedPart1 == null) {
                    this.bedPart1 = b;
                    this.bedPart2 = b;
                } else {
                    this.bedPart2 = b;
                    break;
                }
            }
        }
        Preconditions.checkNotNull(this.bedPart1);
        List<Double> xp = new ArrayList<>();
        List<Double> yp = new ArrayList<>();
        List<Double> zp = new ArrayList<>();
        for(Location sp : spawnPoints){
            xp.add(sp.getX());
            yp.add(sp.getY());
            zp.add(sp.getZ());
        }
        centerSpawnPoint = new Location(
                bl.getWorld(),
                Stats.meanOf(xp),
                Stats.meanOf(yp),
                Stats.meanOf(zp)
        );
    }

    @NotNull
    public DyeColor getColor() {
        return color;
    }

    @NotNull
    public List<Location> getSpawnPoints() {
        return spawnPoints;
    }

    @NotNull
    public Block getBedPart1() {
        return bedPart1;
    }

    @NotNull
    public Block getBedPart2() {
        return bedPart2;
    }

    public boolean isBedPresent() {
        boolean a = bedPart1.getType().name().equals("BED_BLOCK") || bedPart1.getType().name().endsWith("_BED");
        boolean b = bedPart2.getType().name().equals("BED_BLOCK") || bedPart2.getType().name().endsWith("_BED");
        return a && b;
    }

    @NotNull
    public Location getCenterSpawnPoint() {
        return centerSpawnPoint;
    }

    @Override
    public @NotNull String getLocalizedName() {
        return name;
    }

    @Override
    public void setLocalizedName(@NotNull String localizedName) {
        name = localizedName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BWTeam bwTeam = (BWTeam) o;
        return name.equals(bwTeam.name) &&
                color == bwTeam.color &&
                spawnPoints.equals(bwTeam.spawnPoints) &&
                bedPart1.equals(bwTeam.bedPart1) &&
                bedPart2.equals(bwTeam.bedPart2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, color, bedPart1, bedPart2);
    }
}
