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

import dev.anhcraft.battle.ApiProvider;
import dev.anhcraft.battle.api.BattleApi;
import dev.anhcraft.battle.api.BattleSound;
import dev.anhcraft.battle.api.arena.game.GamePlayer;
import dev.anhcraft.battle.api.gui.GuiHandler;
import dev.anhcraft.battle.api.gui.SlotReport;
import dev.anhcraft.battle.api.gui.screen.Window;
import dev.anhcraft.battle.api.gui.struct.Component;
import dev.anhcraft.battle.api.inventory.item.BattleItem;
import dev.anhcraft.battle.api.inventory.item.BattleItemModel;
import dev.anhcraft.battle.api.market.Category;
import dev.anhcraft.battle.api.market.Product;
import dev.anhcraft.battle.system.ResourcePack;
import dev.anhcraft.battle.utils.PlaceholderUtil;
import dev.anhcraft.battle.utils.info.InfoHolder;
import dev.anhcraft.craftkit.abif.PreparedItem;
import dev.anhcraft.craftkit.utils.ItemUtil;
import dev.anhcraft.inst.VM;
import dev.anhcraft.inst.annotations.Function;
import dev.anhcraft.inst.annotations.Namespace;
import dev.anhcraft.inst.lang.Reference;
import dev.anhcraft.inst.values.IntVal;
import dev.anhcraft.inst.values.StringVal;
import dev.anhcraft.inst.values.Val;
import dev.anhcraft.jvmkit.utils.RandomUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.function.Consumer;

@Namespace("Battle")
public class BattleFunction extends GuiHandler {
    public BattleFunction(SlotReport report) {
        super(report);
    }

    @Function("ApplyValue")
    public void applyVal(Val<?> val) {
        Object f = report.getView().getWindow().getBackend().get(GDataRegistry.VALUE_CALLBACK);
        if(f instanceof Consumer){
            ((Consumer<ValueResult>) f).accept(new ValueResult(val.getData()));
        }
    }

    @Function("ApplyDataValue")
    public void applyDataValue(StringVal container, StringVal key){
        Object v;
        if(container.getData().equalsIgnoreCase("window")) {
            v = report.getView().getWindow().getBackend().get(key.getData());
        } else if(container.getData().equalsIgnoreCase("view")) {
            v = report.getView().getBackend().get(key.getData());
        } else {
            return;
        }
        Object f = report.getView().getWindow().getBackend().get(GDataRegistry.VALUE_CALLBACK);
        if (f instanceof Consumer) {
            ((Consumer<ValueResult>) f).accept(new ValueResult(v));
        }
    }

    @Function("SetViewData")
    public void setViewData(StringVal key, Val<?> value) {
        report.getView().getBackend().put(key.getData(), value.getData());
    }

    @Function("SetWindowData")
    public void setWindowData(StringVal key, Val<?> value) {
        report.getView().getWindow().getBackend().put(key.getData(), value.getData());
    }

    @Function("DelViewData")
    public void delViewData(StringVal key) {
        report.getView().getBackend().remove(key.getData());
    }

    @Function("DelWindowData")
    public void delWindowData(StringVal key) {
        report.getView().getWindow().getBackend().remove(key.getData());
    }

    @Function("CancelEvent")
    public void cancelEvent() {
        if(report.getEvent() instanceof Cancellable) {
            ((Cancellable) report.getEvent()).setCancelled(true);
        }
        if(report.getEvent() instanceof InventoryInteractEvent) {
            ((InventoryInteractEvent) report.getEvent()).setResult(Event.Result.DENY);
        }
    }

    @Function("CloseGUI")
    public void close() {
        report.getPlayer().closeInventory();
    }

    @Function("RefreshView")
    public void refreshView() {
        ApiProvider.consume().getGuiManager().updateView(report.getPlayer(), report.getView());
    }

    @Function("SwitchGUI")
    public void switchGui(StringVal gui) {
        if(report.getView().getInventory() instanceof PlayerInventory){
            setBottom(gui);
        } else {
            openTop(gui);
        }
    }

    @Function("OpenTopGUI")
    public void openTop(StringVal gui) {
        ApiProvider.consume().getGuiManager().openTopGui(report.getPlayer(), gui.getData());
    }

    @Function("SetBottomGUI")
    public void setBottom(StringVal gui) {
        ApiProvider.consume().getGuiManager().setBottomGui(report.getPlayer(), gui.getData());
    }

    @Function("PrevPage")
    public void prevPage(StringVal pagination){
        if(report.getView().prevPage(pagination.getData())) {
            for (Component c : report.getView().getGui().getComponentOf(pagination.getData())) {
                ApiProvider.consume().getGuiManager().updateComponent(report.getPlayer(), report.getView(), c);
            }
        }
    }

    @Function("NextPage")
    public void nextPage(StringVal pagination){
        if(report.getView().nextPage(pagination.getData())) {
            for (Component c : report.getView().getGui().getComponentOf(pagination.getData())) {
                ApiProvider.consume().getGuiManager().updateComponent(report.getPlayer(), report.getView(), c);
            }
        }
    }

    @Function("QuitGame")
    public void quitGame(){
        ApiProvider.consume().getArenaManager().quit(report.getPlayer());
    }

    @Function("CopyCurrentSlot")
    public void copyCurrentSlot(VM vm, Reference resultVar){
        vm.setVariable(resultVar.getTarget(), new IntVal(report.getPosition()));
    }

    @Function("SetItemFromData")
    public void setItemFromData(StringVal container, StringVal data, IntVal slot, StringVal notNull){
        Object f;
        if(container.getData().equalsIgnoreCase("window")) {
            f = report.getView().getWindow().getBackend().get(data.getData());
        } else if(container.getData().equalsIgnoreCase("view")) {
            f = report.getView().getBackend().get(data.getData());
        } else {
            return;
        }
        if(f instanceof ItemStack) {
            if(!notNull.getData().equalsIgnoreCase("not-null") || !ItemUtil.isNull((ItemStack) f)) {
                report.getView().getInventory().setItem(slot.getData(), (ItemStack) f);
            }
        } else if(f instanceof PreparedItem) {
            if(!notNull.getData().equalsIgnoreCase("not-null") || !ItemUtil.isNull(((PreparedItem) f).material())) {
                report.getView().getInventory().setItem(slot.getData(), ((PreparedItem) f).build());
            }
        }
    }

    @Function("SetDataFromCursor")
    public void setDataFromCursor(StringVal container, StringVal data, StringVal notNull){
        ItemStack i = report.getPlayer().getItemOnCursor();
        if(!notNull.getData().equalsIgnoreCase("not-null") || !ItemUtil.isNull(i)) {
            if(container.getData().equalsIgnoreCase("window")) {
                report.getView().getWindow().getBackend().put(data.getData(), i.clone());
                report.getPlayer().setItemOnCursor(null);
            } else if(container.getData().equalsIgnoreCase("view")) {
                report.getView().getBackend().put(data.getData(), i.clone());
                report.getPlayer().setItemOnCursor(null);
            }
        }
    }

    @Function("InstallResourcePack")
    public void installResourcePack() {
        ResourcePack.send(report.getPlayer());
    }

    @Function("FormatPAPI")
    public void formatPAPI(VM vm, Reference resultVar, StringVal text) {
        vm.setVariable(resultVar.getTarget(), new StringVal(PlaceholderUtil.formatPAPI(report.getPlayer(), text.getData())));
    }

    @Function("HandleItemDrop")
    public void handleItemDrop(StringVal type) {
        Player player = report.getPlayer();
        ItemStack item = player.getItemOnCursor();
        if(!ItemUtil.isNull(item)) {
            GamePlayer gp = BattleApi.getInstance().getArenaManager().getGamePlayer(player);
            if(gp != null) {
                BattleItem<?> bi = BattleApi.getInstance().getItemManager().read(item);
                if (bi != null && bi.getModel() != null) {
                    BattleItemModel m = bi.getModel();
                    if (m.getItemType().name().equalsIgnoreCase(type.getData())) {
                        gp.getIgBackpack().put(m.getItemType(), m.getId(), bi);
                        player.setItemOnCursor(null);
                        BattleApi.getInstance().getGuiManager().updateView(report.getPlayer(), report.getView());
                        BattleSound bs = BattleApi.getInstance().getGeneralConfig().getBackpackSoundAddItemSuccess();
                        if (bs != null) {
                            bs.play(player);
                        }
                    } else {
                        BattleSound bs = BattleApi.getInstance().getGeneralConfig().getBackpackSoundAddItemFailure();
                        if (bs != null) {
                            bs.play(player);
                        }
                    }
                } else {
                    BattleSound bs = BattleApi.getInstance().getGeneralConfig().getBackpackSoundAddItemFailure();
                    if (bs != null) {
                        bs.play(player);
                    }
                }
            }
        }
    }

    @Function("RemoveCategory")
    public void rmvCtg(){
        Window w = report.getView().getWindow();
        Category c = (Category) w.getBackend().get(GDataRegistry.MARKET_CATEGORY_EDITOR);
        if(c != null) {
            ApiProvider.consume().getMarket().getCategories().remove(c);
        }
    }

    @Function("RemoveProduct")
    public void rmvPd(){
        Window w = report.getView().getWindow();
        Category c = (Category) w.getBackend().get(GDataRegistry.MARKET_CATEGORY_EDITOR);
        Product p = (Product) w.getBackend().get(GDataRegistry.MARKET_PRODUCT_EDITOR);
        if(c != null && p != null) {
            c.getProducts().remove(p);
        }
    }

    @Function("IgoEditor")
    public void ige(){
        Window w = report.getView().getWindow();
        Product p = (Product) w.getBackend().get(GDataRegistry.MARKET_PRODUCT_EDITOR);
        if(p != null) {
            w.getBackend().put(GDataRegistry.VALUE, p.isInGameOnly());
            w.getBackend().put(GDataRegistry.VALUE_CALLBACK, (Consumer<ValueResult>) r -> {
                p.setInGameOnly(r.asBoolean());
            });
        } else {
            Category ctg = (Category) w.getBackend().get(GDataRegistry.MARKET_CATEGORY_EDITOR);
            w.getBackend().put(GDataRegistry.VALUE, ctg.isInGameOnly());
            w.getBackend().put(GDataRegistry.VALUE_CALLBACK, (Consumer<ValueResult>) r -> {
                ctg.setInGameOnly(r.asBoolean());
            });
        }
    }

    @Function("PriceEditor")
    public void pve(){
        Window w = report.getView().getWindow();
        Product p = (Product) w.getBackend().get(GDataRegistry.MARKET_PRODUCT_EDITOR);
        if(p == null) return;
        w.getBackend().put(GDataRegistry.VALUE, p.getPrice());
        w.getBackend().put(GDataRegistry.VALUE_CALLBACK, (Consumer<ValueResult>) r -> {
            p.setPrice(r.asDouble());
        });
    }

    @Function("ExpEditor")
    public void ee(){
        Window w = report.getView().getWindow();
        Product p = (Product) w.getBackend().get(GDataRegistry.MARKET_PRODUCT_EDITOR);
        if(p == null) return;
        w.getBackend().put(GDataRegistry.VALUE, p.getVanillaExp());
        w.getBackend().put(GDataRegistry.VALUE_CALLBACK, (Consumer<ValueResult>) r -> {
            p.setVanillaExp(r.asInt());
        });
    }

    @Function("IconEditor")
    public void ie(){
        Window w = report.getView().getWindow();
        Product p = (Product) w.getBackend().get(GDataRegistry.MARKET_PRODUCT_EDITOR);
        if(p == null) return;
        w.getBackend().put(GDataRegistry.VALUE, p.getIcon().build());
        w.getBackend().put(GDataRegistry.VALUE_CALLBACK, (Consumer<ValueResult>) r -> {
            p.setIcon(r.asPreparedItem());
        });
    }

    @Function("CreateProduct")
    public void createProduct(){
        Category ctg = (Category) report.getView().getWindow().getBackend().get(GDataRegistry.MARKET_CATEGORY_EDITOR);
        if(ctg == null) return;
        String id = new String(RandomUtil.randomLetters(7));
        Product p = new Product(id);
        p.getIcon().name(id);
        ctg.getProducts().add(p);
        ApiProvider.consume().getChatManager().sendPlayer(report.getPlayer(), "editor.market.product_created", new InfoHolder("").inform("id", id).compile());
    }

    @Function("CreateCategory")
    public void createCategory(){
        String id = new String(RandomUtil.randomLetters(7));
        Category category = new Category(id);
        category.getIcon().name(id);
        BattleApi api = ApiProvider.consume();
        api.getMarket().getCategories().add(category);
        api.getChatManager().sendPlayer(report.getPlayer(), "editor.market.category_created", new InfoHolder("").inform("id", id).compile());
    }
}
