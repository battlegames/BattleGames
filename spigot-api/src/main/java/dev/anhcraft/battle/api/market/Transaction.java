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

package dev.anhcraft.battle.api.market;

import dev.anhcraft.battle.utils.info.InfoHolder;
import dev.anhcraft.battle.utils.info.Informative;
import dev.anhcraft.jvmkit.utils.Condition;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class Transaction implements Informative {
    private UUID buyer;
    private String product;
    private double price;
    private long date;

    public Transaction(@NotNull UUID buyer, @NotNull String product, double price, long date) {
        Condition.argNotNull("buyer", buyer);
        Condition.argNotNull("product", product);
        this.buyer = buyer;
        this.product = product;
        this.price = price;
        this.date = date;
    }

    @NotNull
    public UUID getBuyer() {
        return buyer;
    }

    @NotNull
    public String getProduct() {
        return product;
    }

    public double getPrice() {
        return price;
    }

    public long getDate() {
        return date;
    }

    @Override
    public void inform(@NotNull InfoHolder holder) {
        holder.inform("buyer", buyer.toString())
                .inform("product", product)
                .inform("price", price)
                .inform("date", date);
    }
}
