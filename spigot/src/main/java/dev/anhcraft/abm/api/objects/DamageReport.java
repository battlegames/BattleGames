package dev.anhcraft.abm.api.objects;

import org.bukkit.entity.Player;

public class DamageReport {
    private long date;
    private Player damager;

    private double damage;
    private boolean headshotDamage;

    public DamageReport(Player damager, double damage) {
        this.damager = damager;
        this.date = System.currentTimeMillis();
        this.damage = damage;
    }

    public Player getDamager() {
        return damager;
    }

    public long getDate() {
        return date;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public double getDamage() {
        return damage;
    }

    public boolean isHeadshotDamage() {
        return headshotDamage;
    }

    public void setHeadshotDamage(boolean headshotDamage) {
        this.headshotDamage = headshotDamage;
    }
}
