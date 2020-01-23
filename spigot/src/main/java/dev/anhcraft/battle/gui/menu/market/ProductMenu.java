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
package dev.anhcraft.battle.gui.menu.market;

import dev.anhcraft.battle.ApiProvider;
import dev.anhcraft.battle.api.BattleApi;
import dev.anhcraft.battle.api.arena.game.Game;
import dev.anhcraft.battle.api.economy.Currency;
import dev.anhcraft.battle.api.events.PlayerPrePurchaseEvent;
import dev.anhcraft.battle.api.events.PlayerPurchaseEvent;
import dev.anhcraft.battle.api.gui.page.Pagination;
import dev.anhcraft.battle.api.gui.page.SlotChain;
import dev.anhcraft.battle.api.gui.screen.View;
import dev.anhcraft.battle.api.gui.struct.Slot;
import dev.anhcraft.battle.api.market.Category;
import dev.anhcraft.battle.api.market.Market;
import dev.anhcraft.battle.api.market.Product;
import dev.anhcraft.battle.api.market.Transaction;
import dev.anhcraft.battle.api.storage.data.PlayerData;
import dev.anhcraft.battle.gui.GDataRegistry;
import dev.anhcraft.battle.utils.info.InfoHolder;
import dev.anhcraft.battle.utils.info.InfoReplacer;
import dev.anhcraft.craftkit.abif.PreparedItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ProductMenu implements Pagination {
    @Override
    public void supply(@NotNull Player player, @NotNull View view, @NotNull SlotChain chain) {
        BattleApi api = ApiProvider.consume();
        Category ctg = (Category) view.getWindow().getDataContainer().remove(GDataRegistry.MARKET_CATEGORY);
        if(ctg == null) return;
        Market mk = api.getMarket();
        Game g = api.getArenaManager().getGame(player);
        for(Product p : ctg.getProducts()){
            if(!chain.hasNext()) break;
            if(chain.shouldSkip()) continue;
            if(p.isInGameOnly()) {
                if(g == null) continue;
                if(p.getGameModeReserved() != null && p.getGameModeReserved().stream().map(String::toLowerCase).noneMatch(s -> s.equals(g.getMode().getId()))) continue;
            }

            String pf = ApiProvider.consume().getChatManager().getFormattedMessages("price_format."+p.getCurrency().name().toLowerCase()).get(0);
            PreparedItem ic = p.getIcon().duplicate();
            if(mk.isSummaryProductInfoEnabled()){
                List<String> lore = mk.getSummaryProductLore();
                if(lore != null) {
                    String x = String.format(pf, p.getPrice());
                    InfoHolder holder = new InfoHolder("product_");
                    p.inform(holder);
                    InfoReplacer replacer = holder.compile();
                    for (String s : lore) {
                        ic.lore().add(replacer.replace(String.format(s, x)));
                    }
                }
            }

            Slot slot = chain.next();
            slot.setPaginationItem(ic);
            slot.setAdditionalFunction(report -> {
                PlayerData pd = api.getPlayerData(player);
                if(pd == null) return;

                Currency c = p.getCurrency().get();
                final double balance = c.getBalance(player);

                PlayerPrePurchaseEvent ev = new PlayerPrePurchaseEvent(player, mk, ctg, p, balance >= p.getPrice());
                ev.setCancelled(balance < p.getPrice());
                Bukkit.getPluginManager().callEvent(ev);

                if(!ev.hasEnoughBalance()){
                    api.getChatManager().sendPlayer(report.getPlayer(), "market.not_enough_money", s -> String.format(s, String.format(pf, balance)));
                    return;
                } else if(ev.isCancelled()) return;

                if(!c.withdraw(player, p.getPrice())){
                    api.getChatManager().sendPlayer(report.getPlayer(), "market.purchase_failed");
                    return;
                }

                Bukkit.getPluginManager().callEvent(new PlayerPurchaseEvent(player, mk, ctg, p));

                p.givePlayer(player, pd);
                api.getChatManager().sendPlayer(report.getPlayer(), "market.purchase_success");
                if(mk.shouldLogTransactions()){
                    pd.getTransactions().add(new Transaction(
                            player.getUniqueId(),
                            p.getId(),
                            p.getPrice(),
                            p.getCurrency().name(),
                            System.currentTimeMillis()
                    ));
                }
            });
        }
    }
}
