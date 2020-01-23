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

import dev.anhcraft.battle.api.BattleApi;
import dev.anhcraft.battle.impl.Informative;
import dev.anhcraft.battle.utils.info.InfoHolder;
import dev.anhcraft.jvmkit.utils.Condition;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.UUID;

public class Transaction implements Informative {
    private UUID buyer;
    private String product;
    private String currency;
    private double price;
    private long date;
    private String formattedDate;
    private String formattedPrice;

    public Transaction(@NotNull UUID buyer, @NotNull String product, double price, @NotNull String currency, long date) {
        Condition.argNotNull("buyer", buyer);
        Condition.argNotNull("product", product);
        Condition.argNotNull("currency", currency);
        this.buyer = buyer;
        this.product = product;
        this.price = price;
        this.currency = currency;
        this.date = date;
        formattedDate = BattleApi.getInstance().formatLongFormDate(new Date(date));
        System.out.println(currency.toLowerCase());
        String s = BattleApi.getInstance().getLocalizedMessage("price_format." + currency.toLowerCase());
        System.out.println(s);
        if(s != null){
            formattedPrice = String.format(s, price);
        } else {
            formattedPrice = price + " " + currency;
        }
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

    @NotNull
    public String getCurrency() {
        return currency;
    }

    public long getDate() {
        return date;
    }

    @Override
    public void inform(@NotNull InfoHolder holder) {
        holder.inform("buyer", buyer.toString())
                .inform("product", product)
                .inform("currency", currency)
                .inform("price", price)
                .inform("date", date)
                .inform("formatted_price", formattedPrice)
                .inform("formatted_date", formattedDate);
    }
}
