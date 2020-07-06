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

package dev.anhcraft.battle.gui.inst;

import dev.anhcraft.battle.ApiProvider;
import dev.anhcraft.battle.api.BattleApi;
import dev.anhcraft.battle.api.gui.GuiHandler;
import dev.anhcraft.battle.api.gui.SlotReport;
import dev.anhcraft.battle.api.gui.screen.Window;
import dev.anhcraft.battle.api.market.Category;
import dev.anhcraft.battle.api.market.Product;
import dev.anhcraft.battle.gui.GDataRegistry;
import dev.anhcraft.battle.gui.ValueResult;
import dev.anhcraft.battle.utils.info.InfoHolder;
import dev.anhcraft.inst.VM;
import dev.anhcraft.inst.annotations.Function;
import dev.anhcraft.inst.annotations.Namespace;
import dev.anhcraft.jvmkit.utils.RandomUtil;

import java.util.function.Consumer;

@Namespace("Market")
public class MarketFunctions extends GuiHandler {
    public MarketFunctions(SlotReport report) {
        super(report);
    }

    @Function("RemoveCategory")
    public void rmvCtg(VM vm){
        Window w = report.getView().getWindow();
        Category c = (Category) w.getDataContainer().get(GDataRegistry.MARKET_CATEGORY_EDITOR);
        if(c != null) {
            ApiProvider.consume().getMarket().getCategories().remove(c);
        }
    }

    @Function("RemoveProduct")
    public void rmvPd(VM vm){
        Window w = report.getView().getWindow();
        Category c = (Category) w.getDataContainer().get(GDataRegistry.MARKET_CATEGORY_EDITOR);
        Product p = (Product) w.getDataContainer().get(GDataRegistry.MARKET_PRODUCT_EDITOR);
        if(c != null && p != null) {
            c.getProducts().remove(p);
        }
    }

    @Function("IgoEditor")
    public void ige(VM vm){
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

    @Function("PriceEditor")
    public void pve(VM vm){
        Window w = report.getView().getWindow();
        Product p = (Product) w.getDataContainer().get(GDataRegistry.MARKET_PRODUCT_EDITOR);
        if(p == null) return;
        w.getDataContainer().put(GDataRegistry.VALUE, p.getPrice());
        w.getDataContainer().put(GDataRegistry.VALUE_CALLBACK, (Consumer<ValueResult>) r -> {
            p.setPrice(r.asDouble());
        });
    }

    @Function("ExpEditor")
    public void ee(VM vm){
        Window w = report.getView().getWindow();
        Product p = (Product) w.getDataContainer().get(GDataRegistry.MARKET_PRODUCT_EDITOR);
        if(p == null) return;
        w.getDataContainer().put(GDataRegistry.VALUE, p.getVanillaExp());
        w.getDataContainer().put(GDataRegistry.VALUE_CALLBACK, (Consumer<ValueResult>) r -> {
            p.setVanillaExp(r.asInt());
        });
    }

    @Function("IconEditor")
    public void ie(VM vm){
        Window w = report.getView().getWindow();
        Product p = (Product) w.getDataContainer().get(GDataRegistry.MARKET_PRODUCT_EDITOR);
        if(p == null) return;
        w.getDataContainer().put(GDataRegistry.VALUE, p.getIcon().build());
        w.getDataContainer().put(GDataRegistry.VALUE_CALLBACK, (Consumer<ValueResult>) r -> {
            p.setIcon(r.asPreparedItem());
        });
    }

    @Function("CreateProduct")
    public void createProduct(VM vm){
        Category ctg = (Category) report.getView().getWindow().getDataContainer().get(GDataRegistry.MARKET_CATEGORY_EDITOR);
        if(ctg == null) return;
        String id = new String(RandomUtil.randomLetters(7));
        Product p = new Product(id);
        p.getIcon().name(id);
        ctg.getProducts().add(p);
        ApiProvider.consume().getChatManager().sendPlayer(report.getPlayer(), "editor.market.product_created", new InfoHolder("").inform("id", id).compile());
    }

    @Function("CreateCategory")
    public void createCategory(VM vm){
        String id = new String(RandomUtil.randomLetters(7));
        Category category = new Category(id);
        category.getIcon().name(id);
        BattleApi api = ApiProvider.consume();
        api.getMarket().getCategories().add(category);
        api.getChatManager().sendPlayer(report.getPlayer(), "editor.market.category_created", new InfoHolder("").inform("id", id).compile());
    }
}
