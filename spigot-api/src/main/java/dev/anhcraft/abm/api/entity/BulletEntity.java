package dev.anhcraft.abm.api.entity;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BulletEntity extends VirtualEntity {
    private Bullet bullet;

    public BulletEntity(@NotNull Location location, @NotNull Bullet bullet) {
        super(location);
        Validate.notNull(bullet, "Bullet must be non-null");
        this.bullet = bullet;
    }

    @NotNull
    public Bullet getBullet() {
        return bullet;
    }

    public void spawnParticle(){
        Objects.requireNonNull(getLocation().getWorld())
                .getNearbyEntities(getLocation(), bullet.getViewDistance(), bullet.getViewDistance(), bullet.getViewDistance()).stream().filter(f -> f instanceof Player)
                .forEach(entity -> ((Player) entity).spawnParticle(bullet.getParticle(), getLocation(), bullet.getCount(), bullet.getOffsetX(), bullet.getOffsetY(), bullet.getOffsetZ(), bullet.getSpeed()));
    }
}
