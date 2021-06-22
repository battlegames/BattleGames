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

import dev.anhcraft.battle.utils.BoundingBox;
import dev.anhcraft.battle.utils.LocationUtil;
import dev.anhcraft.config.annotations.*;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldMayBeFinal")
@Configurable
public class Rollback {
    private List<BoundingBox> cachedRegionPartitions = new ArrayList<>();

    @Setting
    @Description("Enabled/Disabled the rollback system")
    private boolean enabled;

    @Setting
    @Description("The provider which handles the rollback")
    @Validation(notNull = true)
    private Provider provider;

    @Setting
    @Description("List of worlds need to be reset")
    @Validation(notNull = true, silent = true)
    private List<String> worlds = new ArrayList<>();

    @Setting
    @Path("region.corner_1")
    @Description({
            "First corner in the region",
            "This option only works with BATTLE_REGION"
    })
    @Validation(notNull = true, silent = true)
    private String corner1;

    @Setting
    @Path("region.corner_2")
    @Description({
            "Second corner in the region",
            "This option only works with BATTLE_REGION"
    })
    @Validation(notNull = true, silent = true)
    private String corner2;

    @Setting
    @Path("clear_entities")
    @Description({
            "Clear all entities in the configured worlds",
            "This option only works with BATTLE_REGION"
    })
    private boolean clearEntities;

    public boolean isEnabled() {
        return enabled;
    }

    @NotNull
    public Provider getProvider() {
        return provider == Provider.BATTLE ? Provider.BATTLE_WORLD : provider;
    }

    public void setProvider(@NotNull Provider provider) {
        this.provider = provider;
    }

    @NotNull
    public List<String> getWorlds() {
        return worlds;
    }

    @Nullable
    public Location getCorner1() {
        return corner1 == null ? null : LocationUtil.fromString(corner1);
    }

    @Nullable
    public Location getCorner2() {
        return corner2 == null ? null : LocationUtil.fromString(corner2);
    }

    public boolean shouldClearEntities() {
        return clearEntities;
    }

    @NotNull
    public List<BoundingBox> getCachedRegionPartitions() {
        return cachedRegionPartitions;
    }

    public enum Provider {
        SLIME_WORLD,
        @Deprecated BATTLE,
        BATTLE_WORLD,
        BATTLE_REGION
    }
}
