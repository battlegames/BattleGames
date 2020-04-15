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

package dev.anhcraft.battle.api.economy;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public enum CurrencyType {
    VAULT(() -> NativeCurrencies.VAULT),
    ING(() -> NativeCurrencies.ING),
    IRON(() -> NativeCurrencies.IRON),
    GOLD(() -> NativeCurrencies.GOLD),
    DIAMOND(() -> NativeCurrencies.DIAMOND),
    EMERALD(() -> NativeCurrencies.EMERALD);

    private final Supplier<Currency> currency;

    CurrencyType(Supplier<Currency> currency) {
        this.currency = currency;
    }

    @NotNull
    public Currency get() {
        return currency.get();
    }
}
