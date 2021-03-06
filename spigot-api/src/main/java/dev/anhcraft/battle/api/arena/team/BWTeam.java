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

package dev.anhcraft.battle.api.arena.team;

import com.google.common.base.Preconditions;
import com.google.common.math.Stats;
import dev.anhcraft.battle.api.arena.game.options.BWTeamOptions;
import dev.anhcraft.battle.utils.BlockUtil;
import dev.anhcraft.battle.utils.CustomDataContainer;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BWTeam extends CustomDataContainer implements Team {
    private final DyeColor color;
    private final List<Location> spawnPoints;
    private final Location centerSpawnPoint;
    private String name;
    private Block bedPart1;
    private Block bedPart2;

    @SuppressWarnings("UnstableApiUsage")
    public BWTeam(BWTeamOptions c) {
        Preconditions.checkNotNull(c);

        this.name = c.getName();
        this.color = c.getColor();
        this.spawnPoints = c.getSpawnPoints();
        for (Block b : BlockUtil.getNearbyBlocks(c.getBedLocation(), 2, 2, 2)) {
            if (b.getType().name().equals("BED_BLOCK") || b.getType().name().endsWith("_BED")) {
                if (this.bedPart1 == null) {
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
        for (Location sp : spawnPoints) {
            xp.add(sp.getX());
            yp.add(sp.getY());
            zp.add(sp.getZ());
        }
        centerSpawnPoint = new Location(
                c.getBedLocation().getWorld(),
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
