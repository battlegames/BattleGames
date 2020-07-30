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

import dev.anhcraft.battle.utils.ConfigurableObject;
import dev.anhcraft.battle.utils.LocationUtil;
import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.annotation.Explanation;
import dev.anhcraft.confighelper.annotation.IgnoreValue;
import dev.anhcraft.confighelper.annotation.Key;
import dev.anhcraft.confighelper.annotation.Schema;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("FieldMayBeFinal")
@Schema
public class GameOptions extends ConfigurableObject {
    public static final ConfigSchema<GameOptions> SCHEMA = ConfigSchema.of(GameOptions.class);

    @Key("min_players")
    @Explanation("The minimum players needed to start a game")
    private int minPlayers = 1;

    @Key("countdown_time")
    @Explanation("The countdown time")
    private long countdownTime = 300;

    @Key("spawn_protection_time")
    @Explanation("Protection time when a player spawns (or respawn)")
    private long spawnProtectionTime = 40;

    @Key("item_selection_time")
    @Explanation("The time for selecting Battle items")
    private long itemSelectTime = 200;

    @Key("respawn_waiting_time")
    @Explanation("How long players need to wait before respawn")
    private long respawnWaitTime = 200;

    @Key("waiting_spawn_points")
    @Explanation("Spawn points in waiting phase")
    @IgnoreValue(ifNull = true)
    private List<String> waitSpawnPoints = new ArrayList<>();

    public int getMinPlayers() {
        return minPlayers;
    }

    public long getCountdownTime() {
        return countdownTime;
    }

    public long getSpawnProtectionTime() {
        return spawnProtectionTime;
    }

    public long getRespawnWaitTime() {
        return respawnWaitTime;
    }

    public long getItemSelectTime() {
        return itemSelectTime;
    }

    @NotNull
    public List<Location> getWaitSpawnPoints() {
        return waitSpawnPoints.stream().map(LocationUtil::fromString).collect(Collectors.toList());
    }
}
