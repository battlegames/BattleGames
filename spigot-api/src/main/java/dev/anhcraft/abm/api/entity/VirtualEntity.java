package dev.anhcraft.abm.api.entity;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public abstract class VirtualEntity {
    private Location location;

    public VirtualEntity(@NotNull Location location) {
        Validate.notNull(location, "Location must be non-null");
        this.location = location;
    }

    @NotNull
    public Location getLocation() {
        return location;
    }

    public void setLocation(@NotNull Location location) {
        Validate.notNull(location, "Location must be non-null");
        this.location = location;
    }
}
