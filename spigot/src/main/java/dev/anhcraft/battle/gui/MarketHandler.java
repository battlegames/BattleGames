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

package dev.anhcraft.battle.gui;

import dev.anhcraft.battle.api.ApiProvider;
import dev.anhcraft.battle.api.BattleAPI;
import dev.anhcraft.battle.api.gui.GuiHandler;
import dev.anhcraft.battle.api.gui.SlotReport;
import dev.anhcraft.battle.api.market.Category;
import dev.anhcraft.battle.api.market.Product;
import dev.anhcraft.battle.utils.functions.Function;
import dev.anhcraft.jvmkit.utils.RandomUtil;

public class MarketHandler extends GuiHandler {
    @Function("create_product")
    public void createProduct(SlotReport report){
        Category ctg = (Category) report.getView().getWindow().getDataContainer().get(GDataRegistry.MARKET_CATEGORY_EDITOR);
        if(ctg == null) return;
        String id = new String(RandomUtil.randomLetters(7));
        Product p = new Product(id);
        p.getIcon().name(id);
        ctg.getProducts().add(p);
        ApiProvider.consume().getChatManager().sendPlayer(report.getPlayer(), "editor.market.product_created", s -> String.format(s, id));
    }

    @Function("create_category")
    public void createCategory(SlotReport report){
        String id = new String(RandomUtil.randomLetters(7));
        Category category = new Category(id);
        category.getIcon().name(id);
        BattleAPI api = ApiProvider.consume();
        api.getMarket().getCategories().add(category);
        api.getChatManager().sendPlayer(report.getPlayer(), "editor.market.category_created", s -> String.format(s, id));
    }
}
