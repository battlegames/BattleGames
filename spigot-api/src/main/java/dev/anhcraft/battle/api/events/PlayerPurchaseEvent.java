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
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player purchased a product successfully.
 */
public class PlayerPurchaseEvent extends PlayerEvent {
    public static final HandlerList handlers = new HandlerList();

    private final Market market;
    private final Category category;
    private final Product product;

    public PlayerPurchaseEvent(@NotNull Player player, @NotNull Market market, @NotNull Category category, @NotNull Product product) {
        super(player);
        this.market = market;
        this.category = category;
        this.product = product;
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

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
