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

import com.google.common.collect.ImmutableMap;
import dev.anhcraft.battle.api.arena.game.LocalGame;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Called when an in-game player dies.
 */
public class GamePlayerDeathEvent extends GameEvent {
    public static final HandlerList handlers = new HandlerList();

    private Player player;
    private ImmutableMap<Player, Double> damagers;
    private Set<Player> headshooters;
    private Set<Player> killers;
    private Set<Player> assistants;
    private Player mostDamager;
    private double mostPlayerDamage;
    private double totalPlayerDamage;
    private double totalNatureDamage;

    public GamePlayerDeathEvent(@NotNull LocalGame game, @NotNull Player player, @NotNull ImmutableMap<Player, Double> damagers, @NotNull Set<Player> headshooters, @NotNull Set<Player> killers, @NotNull Set<Player> assistants, @Nullable Player mostDamager, double mostPlayerDamage, double totalPlayerDamage, double totalNatureDamage) {
        super(game);
        this.player = player;
        this.damagers = damagers;
        this.headshooters = headshooters;
        this.killers = killers;
        this.assistants = assistants;
        this.mostDamager = mostDamager;
        this.mostPlayerDamage = mostPlayerDamage;
        this.totalPlayerDamage = totalPlayerDamage;
        this.totalNatureDamage = totalNatureDamage;
    }

    /**
     * Gets the player who died.
     * @return the player
     */
    @NotNull
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the map contains all damagers and their damages to the player.
     * @return damage map
     */
    @NotNull
    public ImmutableMap<Player, Double> getDamagers() {
        return damagers;
    }

    @NotNull
    public Set<Player> getHeadshooters() {
        return headshooters;
    }

    @NotNull
    public Set<Player> getKillers() {
        return killers;
    }

    @NotNull
    public Set<Player> getAssistants() {
        return assistants;
    }

    /**
     * Gets the player who causes the most damage.
     * @return most damager (maybe null if the death was entirely caused by nature)
     */
    @Nullable
    public Player getMostDamager() {
        return mostDamager;
    }

    public double getMostPlayerDamage() {
        return mostPlayerDamage;
    }

    public double getTotalPlayerDamage() {
        return totalPlayerDamage;
    }

    public void setTotalPlayerDamage(double totalPlayerDamage) {
        this.totalPlayerDamage = totalPlayerDamage;
    }

    public double getTotalNatureDamage() {
        return totalNatureDamage;
    }

    public void setTotalNatureDamage(double totalNatureDamage) {
        this.totalNatureDamage = totalNatureDamage;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
