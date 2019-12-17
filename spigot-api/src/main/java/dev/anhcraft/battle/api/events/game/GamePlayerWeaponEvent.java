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
import dev.anhcraft.battle.api.inventory.item.Weapon;
import dev.anhcraft.battle.api.DamageReport;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GamePlayerWeaponEvent extends GameWeaponEvent {
    public static final HandlerList handlers = new HandlerList();
    private GamePlayer gp1;
    private GamePlayer gp2;

    public GamePlayerWeaponEvent(@NotNull LocalGame localGame, @NotNull DamageReport report, @NotNull LivingEntity entity, @NotNull Weapon weapon, @NotNull GamePlayer gp1, @NotNull GamePlayer gp2) {
        super(localGame, report, entity, weapon);
        this.gp1 = gp1;
        this.gp2 = gp2;
    }

    @NotNull
    public Player getPlayer(){
        return (Player) getEntity();
    }

    @NotNull
    public GamePlayer getGameDamager() {
        return gp1;
    }

    @NotNull
    public GamePlayer getGamePlayer() {
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
