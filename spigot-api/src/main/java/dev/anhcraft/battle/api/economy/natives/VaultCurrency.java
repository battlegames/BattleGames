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

import dev.anhcraft.battle.api.economy.Currency;
import dev.anhcraft.craftkit.utils.VaultApiUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class VaultCurrency implements Currency {
    private Economy eco;

    public VaultCurrency(){
        eco = VaultApiUtil.getEconomyApi();
    }

    @Override
    public double getBalance(@NotNull Player player) {
        return eco.getBalance(player);
    }

    @Override
    public boolean withdraw(@NotNull Player player, double delta) {
        return eco.withdrawPlayer(player, delta).transactionSuccess();
    }

    @Override
    public boolean deposit(@NotNull Player player, double delta) {
        return eco.depositPlayer(player, delta).transactionSuccess();
    }
}
