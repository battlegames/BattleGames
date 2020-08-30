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
package dev.anhcraft.battle.api.events;

import dev.anhcraft.battle.api.market.Category;
import dev.anhcraft.battle.api.market.Market;
import dev.anhcraft.battle.api.market.Product;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player is going to purchase a product.
 */
public class PlayerPrePurchaseEvent extends PlayerEvent implements Cancellable {
    public static final HandlerList handlers = new HandlerList();

    private final Market market;
    private final Category category;
    private final Product product;
    private boolean cancelled;
    private boolean hasEnoughBalance;

    public PlayerPrePurchaseEvent(@NotNull Player player, @NotNull Market market, @NotNull Category category, @NotNull Product product, boolean hasEnoughBalance) {
        super(player);
        this.market = market;
        this.category = category;
        this.product = product;
        this.hasEnoughBalance = hasEnoughBalance;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @NotNull
    public Market getMarket() {
        return market;
    }

    @NotNull
    public Category getCategory() {
        return category;
    }

    @NotNull
    public Product getProduct() {
        return product;
    }

    public boolean hasEnoughBalance() {
        return hasEnoughBalance;
    }

    public void setHasEnoughBalance(boolean hasEnoughBalance) {
        this.hasEnoughBalance = hasEnoughBalance;
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
}
