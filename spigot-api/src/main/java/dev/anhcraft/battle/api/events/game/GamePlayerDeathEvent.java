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

import com.google.common.collect.ImmutableList;
import dev.anhcraft.battle.api.arena.game.LocalGame;
import dev.anhcraft.battle.api.reports.DamageReport;
import dev.anhcraft.battle.api.reports.PlayerAttackReport;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Called when an in-game player dies.
 */
public class GamePlayerDeathEvent extends GameEvent {
    public static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final Collection<DamageReport> damageReports;
    private final Map<Player, Contribution> damagerMap;
    private final Player mostDamager;
    private final double mostPlayerDamage;
    private final double totalPlayerDamage;
    private final double totalNatureDamage;
    private final double avgDamage;
    public GamePlayerDeathEvent(@NotNull LocalGame game, @NotNull Player player, @NotNull Collection<DamageReport> damageReports, @NotNull Map<Player, Contribution> damagerMap, @Nullable Player mostDamager, double mostPlayerDamage, double totalPlayerDamage, double totalNatureDamage, double avgDamage) {
        super(game);
        this.player = player;
        this.damageReports = damageReports;
        this.damagerMap = damagerMap;
        this.mostDamager = mostDamager;
        this.mostPlayerDamage = mostPlayerDamage;
        this.totalPlayerDamage = totalPlayerDamage;
        this.totalNatureDamage = totalNatureDamage;
        this.avgDamage = avgDamage;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Gets the player who died.
     *
     * @return the player
     */
    @NotNull
    public Player getPlayer() {
        return player;
    }

    /**
     * Returns all damage reports.
     *
     * @return immutable collection of damage reports
     */
    @NotNull
    public Collection<DamageReport> getDamageReports() {
        return damageReports;
    }

    /**
     * Returns the damager map.
     *
     * @return immutable damage map caused by players
     */
    @NotNull
    public Map<Player, Contribution> getDamagerMap() {
        return damagerMap;
    }

    /**
     * Returns the average damage.
     *
     * @return average damage
     */
    public double getAvgDamage() {
        return avgDamage;
    }

    /**
     * Gets the player who causes the most damage.
     *
     * @return most damager (maybe null if the death was entirely caused by nature)
     */
    @Nullable
    public Player getMostDamager() {
        return mostDamager;
    }

    /**
     * Returns the damage that {@link #getMostDamager()} caused
     *
     * @return the most damage caused by player
     */
    public double getMostPlayerDamage() {
        return mostPlayerDamage;
    }

    /**
     * Gets the total player damage.
     *
     * @return total player damage
     */
    public double getTotalPlayerDamage() {
        return totalPlayerDamage;
    }

    /**
     * Gets the total nature damage.
     *
     * @return total nature damage.
     */
    public double getTotalNatureDamage() {
        return totalNatureDamage;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static class Contribution {
        private List<PlayerAttackReport> damageReports = new ArrayList<>();
        private boolean readOnly;
        private double totalDamage;
        private double avgDamage;
        private boolean isHeadshooter;
        private boolean isKiller;
        private boolean isAssistant;

        private void checkAccess() {
            if (readOnly) {
                try {
                    throw new IllegalAccessException("Read-only");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * Returns an immutable list of damage reports.
         *
         * @return damage reports
         */
        @NotNull
        public List<PlayerAttackReport> getDamageReports() {
            return damageReports;
        }

        public double getTotalDamage() {
            return totalDamage;
        }

        public void setTotalDamage(double totalDamage) {
            checkAccess();
            this.totalDamage = totalDamage;
        }

        public double getAvgDamage() {
            return avgDamage;
        }

        public void setAvgDamage(double avgDamage) {
            checkAccess();
            this.avgDamage = avgDamage;
        }

        public boolean isHeadshooter() {
            return isHeadshooter;
        }

        public void setHeadshooter(boolean headshooter) {
            checkAccess();
            isHeadshooter = headshooter;
        }

        public boolean isKiller() {
            return isKiller;
        }

        public void setKiller(boolean killer) {
            checkAccess();
            isKiller = killer;
        }

        public boolean isAssistant() {
            return isAssistant;
        }

        public void setAssistant(boolean assistant) {
            checkAccess();
            isAssistant = assistant;
        }

        public void readOnly() {
            checkAccess();
            readOnly = true;
            damageReports = ImmutableList.copyOf(damageReports);
        }
    }
}
