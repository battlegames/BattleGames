package dev.anhcraft.abm.api.storage.data;

import dev.anhcraft.abm.api.misc.Resettable;
import dev.anhcraft.abm.api.storage.Serializable;
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
                map.readTag("sp.x", Double.class),
                map.readTag("sp.y", Double.class),
                map.readTag("sp.z", Double.class),
                map.readTag("sp.yw", Float.class),
                map.readTag("sp.pt", Float.class)
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
