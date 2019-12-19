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
import dev.anhcraft.battle.api.reports.DamageReport;
import dev.anhcraft.battle.api.reports.PlayerDamagedReport;
import dev.anhcraft.jvmkit.utils.Condition;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GamePlayerDamageEvent extends GameEvent implements Cancellable {
    public enum BattleType {
        /**
         * A player attacks another player.<br>
         * The report will be {@link dev.anhcraft.battle.api.reports.PlayerAttackReport}.
         */
        PLAYER_ATTACK_PLAYER,

        /**
         * An entity attacks a player.<br>
         * The report will be {@link dev.anhcraft.battle.api.reports.PlayerAttackedReport}.
         */
        ENTITY_ATTACK_PLAYER,

        /**
         * A player attacks an entity.<br>
         * The report will be {@link dev.anhcraft.battle.api.reports.PlayerAttackReport}.
         */
        PLAYER_ATTACK_ENTITY,

        /**
         * The player is damaged by other reasons.<br>
         * The report will be {@link PlayerDamagedReport}.
         */
        PLAYER_DAMAGED
    }

    public static final HandlerList handlers = new HandlerList();

    private DamageReport report;
    private GamePlayer gp1;
    private GamePlayer gp2;
    private BattleType battleType;
    private boolean cancelled;

    public GamePlayerDamageEvent(@NotNull LocalGame game, @NotNull DamageReport report, @Nullable GamePlayer gp1, @Nullable GamePlayer gp2, @NotNull BattleType battleType) {
        super(game);
        Condition.argNotNull("report", report);
        this.report = report;
        this.gp1 = gp1;
        this.gp2 = gp2;
        this.battleType = battleType;
    }

    @Override
    @NotNull
    public LocalGame getGame() {
        return (LocalGame) game;
    }

    @NotNull
    public DamageReport getReport() {
        return report;
    }

    @Nullable
    public GamePlayer getDamager() {
        return gp1;
    }

    @Nullable
    public GamePlayer getPlayer() {
        return gp2;
    }

    @NotNull
    public BattleType getBattleType() {
        return battleType;
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
