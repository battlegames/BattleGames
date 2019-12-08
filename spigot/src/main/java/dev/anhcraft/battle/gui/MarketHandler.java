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
import dev.anhcraft.battle.api.gui.screen.Window;
import dev.anhcraft.battle.api.market.Category;
import dev.anhcraft.battle.api.market.Product;
import dev.anhcraft.battle.utils.functions.Function;
import dev.anhcraft.jvmkit.utils.RandomUtil;

import java.util.function.Consumer;

public class MarketHandler extends GuiHandler {
    @Function("remove_category")
    public void rmvCtg(SlotReport report){
        Window w = report.getView().getWindow();
        Category c = (Category) w.getDataContainer().get(GDataRegistry.MARKET_CATEGORY_EDITOR);
        if(c != null) {
            ApiProvider.consume().getMarket().getCategories().remove(c);
        }
    }

    @Function("remove_product")
    public void rmvPd(SlotReport report){
        Window w = report.getView().getWindow();
        Category c = (Category) w.getDataContainer().get(GDataRegistry.MARKET_CATEGORY_EDITOR);
        Product p = (Product) w.getDataContainer().get(GDataRegistry.MARKET_PRODUCT_EDITOR);
        if(c != null && p != null) {
            c.getProducts().remove(p);
        }
    }

    @Function("igo_editor")
    public void ige(SlotReport report){
        Window w = report.getView().getWindow();
        Product p = (Product) w.getDataContainer().get(GDataRegistry.MARKET_PRODUCT_EDITOR);
        if(p != null) {
            w.getDataContainer().put(GDataRegistry.VALUE, p.isInGameOnly());
            w.getDataContainer().put(GDataRegistry.VALUE_CALLBACK, (Consumer<ValueResult>) r -> {
                p.setInGameOnly(r.asBoolean());
            });
        } else {
            Category ctg = (Category) w.getDataContainer().get(GDataRegistry.MARKET_CATEGORY_EDITOR);
            w.getDataContainer().put(GDataRegistry.VALUE, ctg.isInGameOnly());
            w.getDataContainer().put(GDataRegistry.VALUE_CALLBACK, (Consumer<ValueResult>) r -> {
                ctg.setInGameOnly(r.asBoolean());
            });
        }
    }

    @Function("price_editor")
    public void pve(SlotReport report){
        Window w = report.getView().getWindow();
        Product p = (Product) w.getDataContainer().get(GDataRegistry.MARKET_PRODUCT_EDITOR);
        if(p == null) return;
        w.getDataContainer().put(GDataRegistry.VALUE, p.getPrice());
        w.getDataContainer().put(GDataRegistry.VALUE_CALLBACK, (Consumer<ValueResult>) r -> {
            p.setPrice(r.asDouble());
        });
    }

    @Function("exp_editor")
    public void ee(SlotReport report){
        Window w = report.getView().getWindow();
        Product p = (Product) w.getDataContainer().get(GDataRegistry.MARKET_PRODUCT_EDITOR);
        if(p == null) return;
        w.getDataContainer().put(GDataRegistry.VALUE, p.getVanillaExp());
        w.getDataContainer().put(GDataRegistry.VALUE_CALLBACK, (Consumer<ValueResult>) r -> {
            p.setVanillaExp(r.asInt());
        });
    }

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
