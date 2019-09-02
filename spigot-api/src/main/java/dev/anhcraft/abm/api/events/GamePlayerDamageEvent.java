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
import dev.anhcraft.abm.api.game.GamePlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GamePlayerDamageEvent extends PlayerDamageEvent {
    public static final HandlerList handlers = new HandlerList();

    private GamePlayer gp1;
    private GamePlayer gp2;

    public GamePlayerDamageEvent(Game game, DamageReport report, LivingEntity entity, Weapon weapon, GamePlayer gp1, GamePlayer gp2) {
        super(game, report, entity, weapon);
        this.gp1 = gp1;
        this.gp2 = gp2;
    }

    public Player getPlayer(){
        return (Player) getEntity();
    }

    public GamePlayer getGameDamager() {
        return gp1;
    }

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
