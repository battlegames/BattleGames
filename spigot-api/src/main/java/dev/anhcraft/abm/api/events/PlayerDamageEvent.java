package dev.anhcraft.abm.api.events;

import dev.anhcraft.abm.api.inventory.items.Weapon;
import dev.anhcraft.abm.api.misc.DamageReport;
import dev.anhcraft.abm.api.game.Game;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerDamageEvent extends Event implements Cancellable {
    public static final HandlerList handlers = new HandlerList();

    private Game game;
    private DamageReport report;
    private LivingEntity entity;
    private Weapon weapon;
    private boolean cancelled;

    public PlayerDamageEvent(Game game, DamageReport report, LivingEntity entity, Weapon weapon) {
        this.game = game;
        this.report = report;
        this.entity = entity;
        this.weapon = weapon;
    }

    public Game getGame() {
        return game;
    }

    public DamageReport getReport() {
        return report;
    }

    public Player getDamager() {
        return report.getDamager();
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public double getDamage() {
        return report.getDamage();
    }

    public void setDamage(double damage) {
        report.setDamage(damage);
    }

    public Weapon getWeapon() {
        return weapon;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
