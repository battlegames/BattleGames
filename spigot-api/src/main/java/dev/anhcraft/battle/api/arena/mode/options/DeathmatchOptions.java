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

import dev.anhcraft.battle.utils.LocationUtil;
import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.annotation.IgnoreValue;
import dev.anhcraft.confighelper.annotation.Key;
import dev.anhcraft.confighelper.annotation.Schema;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Schema
public class DeathmatchOptions extends ModeOptions {
    public static final ConfigSchema<DeathmatchOptions> SCHEMA = ConfigSchema.of(DeathmatchOptions.class);

    @Key("playing_spawn_points")
    @IgnoreValue(ifNull = true)
    private List<String> playSpawnPoints = new ArrayList<>();

    @NotNull
    public List<Location> getPlaySpawnPoints() {
        return playSpawnPoints.stream().map(LocationUtil::fromString).collect(Collectors.toList());
    }
}
