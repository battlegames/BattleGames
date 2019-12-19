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

import dev.anhcraft.battle.api.arena.game.GamePlayer;
import dev.anhcraft.battle.api.arena.game.LocalGame;
import dev.anhcraft.battle.api.reports.PlayerAttackReport;
import dev.anhcraft.battle.api.events.WeaponUseEvent;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This event has the same function with {@link WeaponUseEvent} except that this one is only fired during the game.
 */
public class GamePlayerWeaponUseEvent extends WeaponUseEvent {
    public static final HandlerList handlers = new HandlerList();

    private GamePlayer gp1;
    private GamePlayer gp2;

    public GamePlayerWeaponUseEvent(@NotNull LocalGame game, @NotNull PlayerAttackReport report, @NotNull GamePlayer gp1, @Nullable GamePlayer gp2) {
        super(game, report);
        this.gp1 = gp1;
        this.gp2 = gp2;
    }

    @NotNull
    public GamePlayer getDamager() {
        return gp1;
    }

    @Nullable
    public GamePlayer getTarget() {
        return gp2;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
