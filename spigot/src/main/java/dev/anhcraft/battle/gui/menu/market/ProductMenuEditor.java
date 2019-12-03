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

import dev.anhcraft.battle.api.ApiProvider;
import dev.anhcraft.battle.api.BattleAPI;
import dev.anhcraft.battle.api.gui.page.Pagination;
import dev.anhcraft.battle.api.gui.page.SlotChain;
import dev.anhcraft.battle.api.gui.screen.View;
import dev.anhcraft.battle.api.gui.struct.Slot;
import dev.anhcraft.battle.api.market.Category;
import dev.anhcraft.battle.api.market.Product;
import dev.anhcraft.battle.gui.GDataRegistry;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ProductMenuEditor implements Pagination {
    @Override
    public void supply(@NotNull Player player, @NotNull View view, @NotNull SlotChain chain) {
        BattleAPI api = ApiProvider.consume();
        Category ctg = (Category) view.getWindow().getDataContainer().get(GDataRegistry.MARKET_CATEGORY_EDITOR);
        if(ctg == null) return;
        for(Product p : ctg.getProducts()){
            if(!chain.hasNext()) break;
            if(chain.shouldSkip()){
                continue;
            }
            Slot slot = chain.next();
            slot.setPaginationItem(p.getIcon().duplicate());
            slot.setAdditionalFunction(report -> {

            });
        }
    }
}
