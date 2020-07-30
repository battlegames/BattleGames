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
package dev.anhcraft.battle.api.events.game;

import dev.anhcraft.battle.api.arena.game.LocalGame;
import dev.anhcraft.battle.api.inventory.item.Weapon;
import dev.anhcraft.battle.api.reports.PlayerAttackReport;
import dev.anhcraft.jvmkit.utils.Condition;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Called when a player tries to attack an entity with weapon.
 * This event is fired before any damages occur - which may not exist in the future.
 */
public class WeaponUseEvent extends GameEvent implements Cancellable {
    public static final HandlerList handlers = new HandlerList();

    private final PlayerAttackReport report;
    private boolean cancelled;

    public WeaponUseEvent(@NotNull LocalGame game, @NotNull PlayerAttackReport report) {
        super(game);
        Condition.check(report.getWeapon() != null);
        this.report = report;
    }

    @Override
    @NotNull
    public LocalGame getGame() {
        return (LocalGame) game;
    }

    @NotNull
    public PlayerAttackReport getReport() {
        return report;
    }

    @NotNull
    public Weapon getWeapon() {
        return Objects.requireNonNull(report.getWeapon());
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
