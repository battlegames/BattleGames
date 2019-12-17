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
package dev.anhcraft.battle.api.storage.data;

import dev.anhcraft.battle.impl.Resettable;
import dev.anhcraft.battle.impl.Serializable;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ServerData implements Resettable, Serializable {
    private Location spawnPoint;

    @NotNull
    public Location getSpawnPoint() {
        return spawnPoint;
    }

    public void setSpawnPoint(@NotNull Location spawnPoint) {
        Validate.notNull(spawnPoint, "Spawn point must be non-null");
        this.spawnPoint = spawnPoint;
    }

    @Override
    public void reset() {
        spawnPoint = Bukkit.getWorlds().get(0).getSpawnLocation();
    }

    @Override
    public void read(DataMap<String> map) {
        spawnPoint = new Location(
                Bukkit.getWorld(map.readTag("sp.w", String.class)),
                map.readTag("sp.x", Double.class, 0d),
                map.readTag("sp.y", Double.class, 0d),
                map.readTag("sp.z", Double.class, 0d),
                map.readTag("sp.yw", Float.class, 0f),
                map.readTag("sp.pt", Float.class, 0f)
        );
    }

    @Override
    public void write(DataMap<String> map) {
        map.writeTag("sp.w", Optional.ofNullable(spawnPoint.getWorld())
                .orElse(Bukkit.getWorlds().get(0)).getName());
        map.writeTag("sp.x", spawnPoint.getX());
        map.writeTag("sp.y", spawnPoint.getY());
        map.writeTag("sp.z", spawnPoint.getZ());
        map.writeTag("sp.yw", spawnPoint.getYaw());
        map.writeTag("sp.pt", spawnPoint.getPitch());
    }
}
