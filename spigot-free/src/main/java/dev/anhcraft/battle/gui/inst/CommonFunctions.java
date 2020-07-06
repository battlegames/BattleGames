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
import dev.anhcraft.battle.api.gui.GuiHandler;
import dev.anhcraft.battle.api.gui.SlotReport;
import dev.anhcraft.battle.api.gui.struct.Component;
import dev.anhcraft.battle.gui.GDataRegistry;
import dev.anhcraft.battle.gui.ValueResult;
import dev.anhcraft.battle.system.ResourcePack;
import dev.anhcraft.battle.utils.TempDataContainer;
import dev.anhcraft.battle.utils.VMUtil;
import dev.anhcraft.craftkit.abif.PreparedItem;
import dev.anhcraft.craftkit.utils.ItemUtil;
import dev.anhcraft.inst.VM;
import dev.anhcraft.inst.annotations.Function;
import dev.anhcraft.inst.annotations.Namespace;
import dev.anhcraft.inst.values.*;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Objects;
import java.util.function.Consumer;

@Namespace("Common")
public class CommonFunctions extends GuiHandler {
    public CommonFunctions(SlotReport report) {
        super(report);
    }

    @Function("ApplyValue")
    public void applyVal(VM vm, StringVal val) {
        Object f = report.getView().getWindow().getDataContainer().get(GDataRegistry.VALUE_CALLBACK);
        if(f instanceof Consumer){
            ((Consumer<ValueResult>) f).accept(new ValueResult(val.get()));
        }
    }

    @Function("ApplyValue")
    public void applyVal(VM vm, BoolVal val) {
        Object f = report.getView().getWindow().getDataContainer().get(GDataRegistry.VALUE_CALLBACK);
        if(f instanceof Consumer){
            ((Consumer<ValueResult>) f).accept(new ValueResult(val.get()));
        }
    }

    @Function("ApplyValue")
    public void applyVal(VM vm, IntVal val) {
        Object f = report.getView().getWindow().getDataContainer().get(GDataRegistry.VALUE_CALLBACK);
        if(f instanceof Consumer){
            ((Consumer<ValueResult>) f).accept(new ValueResult(val.get()));
        }
    }

    @Function("ApplyValue")
    public void applyVal(VM vm, LongVal val) {
        Object f = report.getView().getWindow().getDataContainer().get(GDataRegistry.VALUE_CALLBACK);
        if(f instanceof Consumer){
            ((Consumer<ValueResult>) f).accept(new ValueResult(val.get()));
        }
    }

    @Function("ApplyValue")
    public void applyVal(VM vm, DoubleVal val) {
        Object f = report.getView().getWindow().getDataContainer().get(GDataRegistry.VALUE_CALLBACK);
        if(f instanceof Consumer){
            ((Consumer<ValueResult>) f).accept(new ValueResult(val.get()));
        }
    }

    private void add(String key, NumberVal<?> val, TempDataContainer tdc){
        Object o = tdc.getDataContainer().get(key);
        if(o instanceof Byte){
            tdc.getDataContainer().put(key, ((Byte) o) + ((byte) VMUtil.getInt(val)));
        } else if(o instanceof Short){
            tdc.getDataContainer().put(key, ((Short) o) + ((short) VMUtil.getInt(val)));
        } else if(o instanceof Integer){
            tdc.getDataContainer().put(key, ((Integer) o) + VMUtil.getInt(val));
        } else if(o instanceof Double){
            tdc.getDataContainer().put(key, ((Double) o) + VMUtil.getDouble(val));
        } else if(o instanceof Float){
            tdc.getDataContainer().put(key, ((Float) o) + ((float) VMUtil.getDouble(val)));
        } else if(o instanceof Long){
            tdc.getDataContainer().put(key, ((Long) o) + VMUtil.getLong(val));
        }
    }

    @Function("AddWindowData")
    public void addWindowData(VM vm, StringVal key, IntVal delta) {
        add(key.get(), delta, report.getView().getWindow());
    }

    @Function("AddWindowData")
    public void addWindowData(VM vm, StringVal key, LongVal delta) {
        add(key.get(), delta, report.getView().getWindow());
    }

    @Function("AddWindowData")
    public void addWindowData(VM vm, StringVal key, DoubleVal delta) {
        add(key.get(), delta, report.getView().getWindow());
    }

    @Function("AddViewData")
    public void addViewData(VM vm, StringVal key, IntVal delta) {
        add(key.get(), delta, report.getView());
    }

    @Function("AddViewData")
    public void addViewData(VM vm, StringVal key, LongVal delta) {
        add(key.get(), delta, report.getView());
    }

    @Function("AddViewData")
    public void addViewData(VM vm, StringVal key, DoubleVal delta) {
        add(key.get(), delta, report.getView());
    }

    private void negate(String key, TempDataContainer tdc){
        Object o = tdc.getDataContainer().get(key);
        if(o instanceof Boolean){
            tdc.getDataContainer().put(key, !((Boolean) o));
        } else if(o instanceof Byte){
            tdc.getDataContainer().put(key, -((Byte) o));
        } else if(o instanceof Short){
            tdc.getDataContainer().put(key, -((Short) o));
        }  else if(o instanceof Integer){
            tdc.getDataContainer().put(key, -((Integer) o));
        } else if(o instanceof Double){
            tdc.getDataContainer().put(key, -((Double) o));
        } else if(o instanceof Float){
            tdc.getDataContainer().put(key, -((Float) o));
        } else if(o instanceof Long){
            tdc.getDataContainer().put(key, -((Long) o));
        }
    }

    @Function("NegateWindowData")
    public void negateWindowData(VM vm, StringVal key) {
        negate(key.get(), report.getView().getWindow());
    }

    @Function("NegateViewData")
    public void negateViewData(VM vm, StringVal key) {
        negate(key.get(), report.getView());
    }

    @Function("SetViewData")
    public void setViewData(VM vm, StringVal key, StringVal value) {
        report.getView().getDataContainer().put(key.get(), value.get());
    }

    @Function("SetViewData")
    public void setViewData(VM vm, StringVal key, BoolVal value) {
        report.getView().getDataContainer().put(key.get(), value.get());
    }

    @Function("SetViewData")
    public void setViewData(VM vm, StringVal key, IntVal value) {
        report.getView().getDataContainer().put(key.get(), value.get());
    }

    @Function("SetViewData")
    public void setViewData(VM vm, StringVal key, LongVal value) {
        report.getView().getDataContainer().put(key.get(), value.get());
    }

    @Function("SetViewData")
    public void setViewData(VM vm, StringVal key, DoubleVal value) {
        report.getView().getDataContainer().put(key.get(), value.get());
    }
    
    @Function("SetWindowData")
    public void setWindowData(VM vm, StringVal key, StringVal value) {
        report.getView().getWindow().getDataContainer().put(key.get(), value.get());
    }

    @Function("SetWindowData")
    public void setWindowData(VM vm, StringVal key, BoolVal value) {
        report.getView().getWindow().getDataContainer().put(key.get(), value.get());
    }

    @Function("SetWindowData")
    public void setWindowData(VM vm, StringVal key, IntVal value) {
        report.getView().getWindow().getDataContainer().put(key.get(), value.get());
    }

    @Function("SetWindowData")
    public void setWindowData(VM vm, StringVal key, LongVal value) {
        report.getView().getWindow().getDataContainer().put(key.get(), value.get());
    }

    @Function("SetWindowData")
    public void setWindowData(VM vm, StringVal key, DoubleVal value) {
        report.getView().getWindow().getDataContainer().put(key.get(), value.get());
    }

    @Function("DelViewData")
    public void delViewData(VM vm, StringVal key) {
        report.getView().getDataContainer().remove(key.get());
    }

    @Function("DelWindowData")
    public void delWindowData(VM vm, StringVal key) {
        report.getView().getWindow().getDataContainer().remove(key.get());
    }

    @Function("CancelEvent")
    public void prevent(VM vm) {
        if(report.getEvent() instanceof Cancellable) {
            ((Cancellable) report.getEvent()).setCancelled(true);
        }
        if(report.getEvent() instanceof InventoryInteractEvent) {
            ((InventoryInteractEvent) report.getEvent()).setResult(Event.Result.DENY);
        }
    }

    @Function("CloseGUI")
    public void close(VM vm) {
        report.getPlayer().closeInventory();
    }

    @Function("RefreshView")
    public void refreshView(VM vm) {
        ApiProvider.consume().getGuiManager().updateView(report.getPlayer(), report.getView());
    }

    @Function("SwitchGUI")
    public void switchGui(VM vm, StringVal gui) {
        if(report.getView().getInventory() instanceof PlayerInventory){
            setBottom(vm, gui);
        } else {
            openTop(vm, gui);
        }
    }

    @Function("OpenTopGUI")
    public void openTop(VM vm, StringVal gui) {
        ApiProvider.consume().getGuiManager().openTopGui(report.getPlayer(), gui.get());
    }

    @Function("SetBottomGUI")
    public void setBottom(VM vm, StringVal gui) {
        ApiProvider.consume().getGuiManager().setBottomGui(report.getPlayer(), gui.get());
    }

    @Function("PrevPage")
    public void prevPage(VM vm, StringVal pagination){
        if(report.getView().prevPage(pagination.get())) {
            for (Component c : report.getView().getGui().getComponentOf(pagination.get())) {
                ApiProvider.consume().getGuiManager().updateComponent(report.getPlayer(), report.getView(), c);
            }
        }
    }

    @Function("NextPage")
    public void nextPage(VM vm, StringVal pagination){
        if(report.getView().nextPage(pagination.get())) {
            for (Component c : report.getView().getGui().getComponentOf(pagination.get())) {
                ApiProvider.consume().getGuiManager().updateComponent(report.getPlayer(), report.getView(), c);
            }
        }
    }

    @Function("QuitGame")
    public void quitGame(VM vm){
        ApiProvider.consume().getArenaManager().quit(report.getPlayer());
    }

    @Function("CopyCurrentSlot")
    public void copyCurrentSlot(VM vm, StringVal container, StringVal data){
        if(container.get().equalsIgnoreCase("window")) {
            report.getView().getWindow().getDataContainer().put(data.get(), report.getPosition());
        } else if(container.get().equalsIgnoreCase("view")) {
            report.getView().getDataContainer().put(data.get(), report.getPosition());
        }
    }

    @Function("PullItemFromData")
    public void pullItemFromData(VM vm, StringVal data, StringVal notNull){
        Object f = report.getView().getWindow().getDataContainer().remove(data.get());
        if(f instanceof ItemStack) {
            if(!notNull.get().equalsIgnoreCase("not-null") || !ItemUtil.isNull((ItemStack) f)) {
                report.getSlot().getComponent().setItem(PreparedItem.of((ItemStack) f));
            }
        } else if(f instanceof PreparedItem) {
            if(!notNull.get().equalsIgnoreCase("not-null") || !ItemUtil.isNull(((PreparedItem) f).material())) {
                report.getSlot().getComponent().setItem((PreparedItem) f);
            }
        }
    }

    @Function("PullItemFromCursor")
    public void pullItemFromCursor(VM vm, StringVal notNull){
        ItemStack i = report.getPlayer().getItemOnCursor();
        if(!notNull.get().equalsIgnoreCase("not-null") || !ItemUtil.isNull(i)) {
            report.getSlot().getComponent().setItem(PreparedItem.of(i));
            report.getPlayer().setItemOnCursor(null);
        }
    }

    @Function("PushItemToValue")
    public void pushItemToValue(VM vm, IntVal slot){
        Object f = report.getView().getWindow().getDataContainer().get(GDataRegistry.VALUE_CALLBACK);
        if(f instanceof Consumer){
            ((Consumer<ValueResult>) f).accept(new ValueResult(Objects.requireNonNull(report.getView().getSlot(slot.get())).getComponent().getItem()));
        }
    }

    @Function("InstallResourcePack")
    public void installResourcePack(VM vm) {
        ResourcePack.send(report.getPlayer());
    }
}
