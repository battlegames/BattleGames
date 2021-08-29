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
package dev.anhcraft.battle.api.arena.game;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.util.concurrent.AtomicDouble;
import dev.anhcraft.battle.api.inventory.item.BattleItem;
import dev.anhcraft.battle.api.inventory.item.ItemType;
import dev.anhcraft.battle.api.stats.StatisticMap;
import dev.anhcraft.battle.api.stats.natives.*;
import dev.anhcraft.battle.impl.Resettable;
import dev.anhcraft.battle.utils.CustomDataContainer;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;

public class GamePlayer extends CustomDataContainer implements Resettable {
    private final StatisticMap stats = new StatisticMap(x -> {
    });
    private final AtomicDouble igBalance = new AtomicDouble();
    private final WeakReference<Player> player;
    private final long joinDate;
    private final Table<ItemType, String, BattleItem<?>> igBackpack = HashBasedTable.create();
    private boolean hasFirstKill;
    private boolean spectator;
    private boolean winner;
    private ItemStack[] backupInventory;

    public GamePlayer(@NotNull Player player) {
        Validate.notNull(player, "Player must be non-null");
        this.player = new WeakReference<>(player);
        this.joinDate = System.currentTimeMillis();
    }

    @NotNull
    public Player toBukkit() {
        Player p = player.get();
        if (p == null) {
            throw new IllegalStateException("The player is not online anymore, thus this game player should be removed previously?");
        }
        return p;
    }

    public boolean isSpectator() {
        return spectator;
    }

    public void setSpectator(boolean spectator) {
        this.spectator = spectator;
    }

    public boolean isWinner() {
        return winner;
    }

    public void setWinner(boolean winner) {
        this.winner = winner;
    }

    @NotNull
    public StatisticMap getStats() {
        return stats;
    }

    @Nullable
    public ItemStack[] getBackupInventory() {
        return backupInventory;
    }

    public void setBackupInventory(@Nullable ItemStack[] backupInventory) {
        this.backupInventory = backupInventory;
    }

    public boolean hasFirstKill() {
        return hasFirstKill;
    }

    public void setHasFirstKill(boolean hasFirstKill) {
        this.hasFirstKill = hasFirstKill;
    }

    @NotNull
    public AtomicDouble getIgBalance() {
        return igBalance;
    }

    @NotNull
    public Table<ItemType, String, BattleItem<?>> getIgBackpack() {
        return igBackpack;
    }

    public long getJoinDate() {
        return joinDate;
    }

    @Override
    public void reset() {
        spectator = false;
        winner = false;
        stats.clear();
        backupInventory = null;
        hasFirstKill = false;
        igBalance.set(0);
    }

    public void mergeBasicStats(StatisticMap dest) {
        Player p = toBukkit();
        dest.of(KillStat.class).increase(p, getStats().of(KillStat.class).get());
        dest.of(HeadshotStat.class).increase(p, getStats().of(HeadshotStat.class).get());
        dest.of(AssistStat.class).increase(p, getStats().of(AssistStat.class).get());
        dest.of(DeathStat.class).increase(p, getStats().of(DeathStat.class).get());
        dest.of(RespawnStat.class).increase(p, getStats().of(RespawnStat.class).get());
        dest.of(StolenMobStat.class).increase(p, getStats().of(StolenMobStat.class).get());
        if (hasFirstKill()) dest.of(FirstKillStat.class).increase(p);
    }
}
