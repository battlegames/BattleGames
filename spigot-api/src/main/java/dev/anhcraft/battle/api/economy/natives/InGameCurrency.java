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

package dev.anhcraft.battle.api.economy.natives;

import dev.anhcraft.battle.ApiProvider;
import dev.anhcraft.battle.api.arena.game.GamePlayer;
import dev.anhcraft.battle.api.economy.Currency;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InGameCurrency implements Currency {
    @Nullable
    private GamePlayer getGamePlayer(Player player) {
        return ApiProvider.consume().getArenaManager().getGamePlayer(player);
    }

    @Override
    public double getBalance(@NotNull Player player) {
        GamePlayer gp = getGamePlayer(player);
        return gp == null ? 0 : getBalance(gp);
    }

    public double getBalance(@NotNull GamePlayer player) {
        return player.getIgBalance().get();
    }

    @Override
    public boolean withdraw(@NotNull Player player, double delta) {
        GamePlayer gp = getGamePlayer(player);
        if (gp == null) return false;
        if (delta != 0) {
            gp.getIgBalance().addAndGet(delta < 0 ? delta : -delta);
        }
        return true;
    }

    @Override
    public boolean deposit(@NotNull Player player, double delta) {
        GamePlayer gp = getGamePlayer(player);
        if (gp == null) return false;
        if (delta != 0) {
            gp.getIgBalance().addAndGet(delta < 0 ? -delta : delta);
        }
        return true;
    }
}
