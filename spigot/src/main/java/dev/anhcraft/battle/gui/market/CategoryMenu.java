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
import org.bukkit.entity.Player;

import java.util.List;

public class CategoryMenu extends GuiListener implements PaginationHandler {
    @Override
    public void pullData(Player player, PlayerGui playerGui, Gui gui, Pagination pagination, List<PaginationItem> data) {
        BattleAPI api = ApiProvider.consume();
        Market mk = api.getMarket();
        for(Category c : mk.getCategories()){
            data.add(new PaginationItem(c.getIcon().build(), new GuiCallback<SlotClickReport>(SlotClickReport.class) {
                @Override
                public void call(SlotClickReport event) {
                    playerGui.getSharedData().put("category", c);
                    api.getGuiManager().openTopInventory(player, "market_product_menu");
                }
            }));
        }
    }
}
