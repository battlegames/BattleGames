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
