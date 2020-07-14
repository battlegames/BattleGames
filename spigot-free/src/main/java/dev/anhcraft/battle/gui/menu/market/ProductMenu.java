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
import dev.anhcraft.inst.lang.Instruction;
import dev.anhcraft.jvmkit.utils.ObjectUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

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

            String currencyFormat = Objects.requireNonNull(ApiProvider.consume().getLocalizedMessage("currency_format." + p.getCurrency().name().toLowerCase()));
            String price = new InfoHolder("")
                    .inform("amount", p.getPrice())
                    .compile()
                    .replace(currencyFormat);
            PreparedItem ic = p.getIcon().duplicate();
            if(mk.isProductLoreFooterEnabled()){
                List<String> lore = mk.getProductLoreFooter();
                if(lore != null) {
                    InfoHolder holder = new InfoHolder("product_");
                    p.inform(holder);
                    holder.inform("price_formatted", price);
                    InfoReplacer replacer = holder.compile();
                    for (String s : lore) {
                        ic.lore().add(replacer.replace(s));
                    }
                }
            }

            Slot slot = chain.next();
            slot.setPaginationItem(ic);
            slot.setExtraClickFunction((vm, report) -> {
                PlayerData pd = api.getPlayerData(player);
                if(pd == null) return;

                Currency c = p.getCurrency().get();
                double balance = c.getBalance(player);
                String balanceFormat = new InfoHolder("")
                        .inform("amount", balance)
                        .compile()
                        .replace(currencyFormat);

                PlayerPrePurchaseEvent ev = new PlayerPrePurchaseEvent(player, mk, ctg, p, balance >= p.getPrice());
                ev.setCancelled(balance < p.getPrice());
                Bukkit.getPluginManager().callEvent(ev);

                if(!ev.hasEnoughBalance()){
                    api.getChatManager().sendPlayer(report.getPlayer(), "market.not_enough_money", new InfoHolder("").inform("balance", balanceFormat).compile());
                    return;
                } else if(ev.isCancelled()) return;

                if(!c.withdraw(player, p.getPrice())){
                    api.getChatManager().sendPlayer(report.getPlayer(), "market.purchase_failed");
                    return;
                }

                Bukkit.getPluginManager().callEvent(new PlayerPurchaseEvent(player, mk, ctg, p));

                p.givePlayer(player, pd);
                if(p.getCallFunction() != null) {
                    Instruction[] ins = p.getCallFunction().stream().map(vm::compileInstruction).toArray(Instruction[]::new);
                    vm.newSession(ins).execute();
                }
                api.getChatManager().sendPlayer(report.getPlayer(), "market.purchase_success");
                if(mk.shouldLogTransactions()){
                    pd.getTransactions().add(new Transaction(
                            player.getUniqueId(),
                            ObjectUtil.optional(p.getIcon().name(), p.getId()),
                            p.getPrice(),
                            p.getCurrency().name(),
                            System.currentTimeMillis()
                    ));
                }
            });
        }
    }
}
