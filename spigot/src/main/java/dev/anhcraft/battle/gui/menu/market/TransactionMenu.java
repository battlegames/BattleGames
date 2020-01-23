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
import dev.anhcraft.battle.api.gui.page.Pagination;
import dev.anhcraft.battle.api.gui.page.SlotChain;
import dev.anhcraft.battle.api.gui.screen.View;
import dev.anhcraft.battle.api.gui.struct.Slot;
import dev.anhcraft.battle.api.market.Transaction;
import dev.anhcraft.battle.api.storage.data.PlayerData;
import dev.anhcraft.battle.utils.info.InfoHolder;
import dev.anhcraft.craftkit.abif.PreparedItem;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TransactionMenu implements Pagination {
    @Override
    public void supply(@NotNull Player player, @NotNull View view, @NotNull SlotChain chain) {
        BattleApi api = ApiProvider.consume();
        PlayerData pd = api.getPlayerData(player);
        if(pd == null) return;
        PreparedItem pi = api.getMarket().getTransactionItem();
        for(Transaction t : pd.getTransactions()){
            if(!chain.hasNext()) break;
            if(chain.shouldSkip()) continue;
            Slot slot = chain.next();
            if(pi != null) {
                InfoHolder ih = new InfoHolder("");
                t.inform(ih);
                slot.setPaginationItem(ih.compile().replace(pi.duplicate()));
            }
        }
    }
}
