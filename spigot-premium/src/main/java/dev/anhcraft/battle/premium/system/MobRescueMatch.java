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

package dev.anhcraft.battle.premium.system;

import dev.anhcraft.battle.api.arena.mode.MobRescueObjective;
import dev.anhcraft.battle.api.arena.mode.options.MobRescueOptions;
import dev.anhcraft.craftkit.cb_common.BoundingBox;
import dev.anhcraft.jvmkit.utils.RandomUtil;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

public class MobRescueMatch {
    @NotNull
    public static MobRescueMatch create(@NotNull MobRescueOptions options){
        Map<EntityType, Integer> mobCount = new EnumMap<>(EntityType.class);
        int totalMobs = 0;
        for(Map.Entry<EntityType, MobRescueObjective> e : options.getObjectives().entrySet()){
            int v = RandomUtil.randomInt(e.getValue().getMinAmount(), e.getValue().getMaxAmount());
            mobCount.put(e.getKey(), v);
            totalMobs += v;
        }
        BoundingBox region = BoundingBox.of(options.getGatheringRegionCorner1(), options.getGatheringRegionCorner2());
        return new MobRescueMatch(mobCount, totalMobs, region);
    }

    private final Map<EntityType, Integer> mobCount;
    private final int totalMobs;
    private final BoundingBox gatheringRegion;
    private int stolenMobs;

    MobRescueMatch(@NotNull Map<EntityType, Integer> mobCount, int totalMobs, BoundingBox gatheringRegion) {
        this.mobCount = mobCount;
        this.totalMobs = totalMobs;
        this.gatheringRegion = gatheringRegion;
    }

    @NotNull
    public Map<EntityType, Integer> getMobCount() {
        return mobCount;
    }

    public int getTotalMobs() {
        return totalMobs;
    }

    @NotNull
    public BoundingBox getGatheringRegion() {
        return gatheringRegion;
    }

    public int getStolenMobs() {
        return stolenMobs;
    }

    public void setStolenMobs(int stolenMobs) {
        this.stolenMobs = stolenMobs;
    }
}
