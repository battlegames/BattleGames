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
package dev.anhcraft.battle.gui.market;

import dev.anhcraft.battle.api.ApiProvider;
import dev.anhcraft.battle.api.BattleAPI;
import dev.anhcraft.battle.api.gui.*;
import dev.anhcraft.battle.api.market.Category;
import dev.anhcraft.battle.api.market.Market;
import dev.anhcraft.battle.api.market.Product;
import dev.anhcraft.battle.api.market.Transaction;
import dev.anhcraft.battle.api.storage.data.PlayerData;
import dev.anhcraft.battle.system.integrations.VaultApi;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;

import java.util.List;

public class ProductMenu extends GuiListener implements PaginationHandler {
    @Override
    public void pullData(Player player, PlayerGui playerGui, Gui gui, Pagination pagination, List<PaginationItem> data) {
        BattleAPI api = ApiProvider.consume();
        Category ctg = (Category) playerGui.getSharedData().remove("category");
        if(ctg == null) return;
        Market mk = api.getMarket();
        for(Product p : ctg.getProducts()){
            if(mk.isSummaryProductInfoEnabled() && mk.getSummaryProductLore() != null){
                p.getIcon().lore().addAll(mk.getSummaryProductLore());
            }
            data.add(new PaginationItem(p.getIcon().build(), new GuiCallback<SlotClickReport>(SlotClickReport.class) {
                @Override
                public void call(SlotClickReport event) {
                    event.getClickEvent().setCancelled(true);
                    if(!mk.isInGameShoppingAllowed() && api.getGameManager().getGame(player) != null){
                        api.getChatManager().sendPlayer(event.getPlayer(), "market.ig_shop_not_allowed");
                        event.getPlayer().closeInventory();
                        return;
                    }
                    double balance = VaultApi.getEconomyApi().getBalance(player);
                    if(balance < p.getPrice()){
                        api.getChatManager().sendPlayer(event.getPlayer(), "market.not_enough_money", s -> String.format(s, balance));
                        return;
                    }
                    PlayerData pd = api.getPlayerData(player);
                    if(pd == null) return;
                    EconomyResponse er = VaultApi.getEconomyApi().withdrawPlayer(player, p.getPrice());
                    if(!er.transactionSuccess()){
                        api.getChatManager().sendPlayer(event.getPlayer(), "market.purchase_failed");
                        return;
                    }
                    p.givePlayer(player, pd);
                    api.getChatManager().sendPlayer(event.getPlayer(), "market.purchase_success");
                    if(mk.shouldLogTransactions()){
                        pd.getTransactions().add(new Transaction(
                                player.getUniqueId(),
                                p.getId(),
                                p.getPrice(),
                                System.currentTimeMillis()
                        ));
                    }
                }
            }));
        }
    }
}
