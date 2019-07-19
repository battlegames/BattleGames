package dev.anhcraft.abm.api.objects;

import org.bukkit.Particle;

public class Bullet {
    private double damage;
    private double knockback;
    private Particle particle;
    private int count;
    private double offsetX;
    private double offsetY;
    private double offsetZ;
    private double speed;
    private Object data;
    private double viewDistance;

    public Bullet(double damage, double knockback, Particle particle, int count, double offsetX, double offsetY, double offsetZ, double speed, Object data, double viewDistance) {
        this.damage = damage;
        this.knockback = knockback;
        this.particle = particle;
        this.count = count;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.speed = speed;
        this.data = data;
        this.viewDistance = viewDistance;
    }

    public double getDamage() {
        return damage;
    }

    public double getKnockback() {
        return knockback;
    }

    public Particle getParticle() {
        return particle;
    }

    public int getCount() {
        return count;
    }

    public double getOffsetX() {
        return offsetX;
    }

    public double getOffsetY() {
        return offsetY;
    }

    public double getOffsetZ() {
        return offsetZ;
    }

    public double getSpeed() {
        return speed;
    }

    public Object getData() {
        return data;
    }

    public double getViewDistance() {
        return viewDistance;
    }
}
