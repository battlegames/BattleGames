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
import dev.anhcraft.battle.api.gui.screen.View;
import dev.anhcraft.battle.api.gui.screen.Window;
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

    @Function("ApplyValue")
    public void applyValue(VM vm, StringVal container, StringVal data){
        Object v;
        if(container.get().equalsIgnoreCase("window")) {
            v = report.getView().getWindow().getDataContainer().get(data.get());
        } else if(container.get().equalsIgnoreCase("view")) {
            v = report.getView().getDataContainer().get(data.get());
        } else {
            return;
        }
        Object f = report.getView().getWindow().getDataContainer().get(GDataRegistry.VALUE_CALLBACK);
        if (f instanceof Consumer) {
            ((Consumer<ValueResult>) f).accept(new ValueResult(v));
        }
    }

    private void add(VM vm, String key, NumberVal<?> val, TempDataContainer tdc){
        Object o = tdc.getDataContainer().get(key);
        if(o instanceof Byte){
            tdc.getDataContainer().put(key, o = ((Byte) o) + ((byte) VMUtil.getInt(val)));
        } else if(o instanceof Short){
            tdc.getDataContainer().put(key, o = ((Short) o) + ((short) VMUtil.getInt(val)));
        } else if(o instanceof Integer){
            tdc.getDataContainer().put(key, o = ((Integer) o) + VMUtil.getInt(val));
        } else if(o instanceof Double){
            tdc.getDataContainer().put(key, o = ((Double) o) + VMUtil.getDouble(val));
        } else if(o instanceof Float){
            tdc.getDataContainer().put(key, o = ((Float) o) + ((float) VMUtil.getDouble(val)));
        } else if(o instanceof Long){
            tdc.getDataContainer().put(key, o = ((Long) o) + VMUtil.getLong(val));
        }
        if(tdc instanceof Window) {
            VMUtil.setVariable(vm, VMUtil.WINDOW_DATA_PREFIX+key, o);
        } else if(tdc instanceof View) {
            VMUtil.setVariable(vm, VMUtil.VIEW_DATA_PREFIX+key, o);
        }
    }

    @Function("AddWindowData")
    public void addWindowData(VM vm, StringVal key, IntVal delta) {
        add(vm, key.get(), delta, report.getView().getWindow());
    }

    @Function("AddWindowData")
    public void addWindowData(VM vm, StringVal key, LongVal delta) {
        add(vm, key.get(), delta, report.getView().getWindow());
    }

    @Function("AddWindowData")
    public void addWindowData(VM vm, StringVal key, DoubleVal delta) {
        add(vm, key.get(), delta, report.getView().getWindow());
    }

    @Function("AddViewData")
    public void addViewData(VM vm, StringVal key, IntVal delta) {
        add(vm, key.get(), delta, report.getView());
    }

    @Function("AddViewData")
    public void addViewData(VM vm, StringVal key, LongVal delta) {
        add(vm, key.get(), delta, report.getView());
    }

    @Function("AddViewData")
    public void addViewData(VM vm, StringVal key, DoubleVal delta) {
        add(vm, key.get(), delta, report.getView());
    }

    private void negate(VM vm, String key, TempDataContainer tdc){
        Object o = tdc.getDataContainer().get(key);
        if(o instanceof Boolean){
            tdc.getDataContainer().put(key, o = !((Boolean) o));
        } else if(o instanceof Byte){
            tdc.getDataContainer().put(key, o = -((Byte) o));
        } else if(o instanceof Short){
            tdc.getDataContainer().put(key, o = -((Short) o));
        }  else if(o instanceof Integer){
            tdc.getDataContainer().put(key, o = -((Integer) o));
        } else if(o instanceof Double){
            tdc.getDataContainer().put(key, o = -((Double) o));
        } else if(o instanceof Float){
            tdc.getDataContainer().put(key, o = -((Float) o));
        } else if(o instanceof Long){
            tdc.getDataContainer().put(key, o = -((Long) o));
        }
        if(tdc instanceof Window) {
            VMUtil.setVariable(vm, VMUtil.WINDOW_DATA_PREFIX+key, o);
        } else if(tdc instanceof View) {
            VMUtil.setVariable(vm, VMUtil.VIEW_DATA_PREFIX+key, o);
        }
    }

    @Function("NegateWindowData")
    public void negateWindowData(VM vm, StringVal key) {
        negate(vm, key.get(), report.getView().getWindow());
    }

    @Function("NegateViewData")
    public void negateViewData(VM vm, StringVal key) {
        negate(vm, key.get(), report.getView());
    }

    @Function("SetViewData")
    public void setViewData(VM vm, StringVal key, StringVal value) {
        report.getView().getDataContainer().put(key.get(), value.get());
        vm.setVariable(VMUtil.VIEW_DATA_PREFIX+key.get(), value);
    }

    @Function("SetViewData")
    public void setViewData(VM vm, StringVal key, BoolVal value) {
        report.getView().getDataContainer().put(key.get(), value.get());
        vm.setVariable(VMUtil.VIEW_DATA_PREFIX+key.get(), value);
    }

    @Function("SetViewData")
    public void setViewData(VM vm, StringVal key, IntVal value) {
        report.getView().getDataContainer().put(key.get(), value.get());
        vm.setVariable(VMUtil.VIEW_DATA_PREFIX+key.get(), value);
    }

    @Function("SetViewData")
    public void setViewData(VM vm, StringVal key, LongVal value) {
        report.getView().getDataContainer().put(key.get(), value.get());
        vm.setVariable(VMUtil.VIEW_DATA_PREFIX+key.get(), value);
    }

    @Function("SetViewData")
    public void setViewData(VM vm, StringVal key, DoubleVal value) {
        report.getView().getDataContainer().put(key.get(), value.get());
        vm.setVariable(VMUtil.VIEW_DATA_PREFIX+key.get(), value);
    }
    
    @Function("SetWindowData")
    public void setWindowData(VM vm, StringVal key, StringVal value) {
        report.getView().getWindow().getDataContainer().put(key.get(), value.get());
        vm.setVariable(VMUtil.WINDOW_DATA_PREFIX+key.get(), value);
    }

    @Function("SetWindowData")
    public void setWindowData(VM vm, StringVal key, BoolVal value) {
        report.getView().getWindow().getDataContainer().put(key.get(), value.get());
        vm.setVariable(VMUtil.WINDOW_DATA_PREFIX+key.get(), value);
    }

    @Function("SetWindowData")
    public void setWindowData(VM vm, StringVal key, IntVal value) {
        report.getView().getWindow().getDataContainer().put(key.get(), value.get());
        vm.setVariable(VMUtil.WINDOW_DATA_PREFIX+key.get(), value);
    }

    @Function("SetWindowData")
    public void setWindowData(VM vm, StringVal key, LongVal value) {
        report.getView().getWindow().getDataContainer().put(key.get(), value.get());
        vm.setVariable(VMUtil.WINDOW_DATA_PREFIX+key.get(), value);
    }

    @Function("SetWindowData")
    public void setWindowData(VM vm, StringVal key, DoubleVal value) {
        report.getView().getWindow().getDataContainer().put(key.get(), value.get());
        vm.setVariable(VMUtil.WINDOW_DATA_PREFIX+key.get(), value);
    }

    @Function("DelViewData")
    public void delViewData(VM vm, StringVal key) {
        report.getView().getDataContainer().remove(key.get());
        vm.setVariable(VMUtil.VIEW_DATA_PREFIX+key.get(), null);
    }

    @Function("DelWindowData")
    public void delWindowData(VM vm, StringVal key) {
        report.getView().getWindow().getDataContainer().remove(key.get());
        vm.setVariable(VMUtil.WINDOW_DATA_PREFIX+key.get(), null);
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
            String k = data.get();
            int p = report.getPosition();
            report.getView().getWindow().getDataContainer().put(k, p);
            VMUtil.setVariable(vm, VMUtil.WINDOW_DATA_PREFIX+k, p);
        } else if(container.get().equalsIgnoreCase("view")) {
            String k = data.get();
            int p = report.getPosition();
            report.getView().getDataContainer().put(k, p);
            VMUtil.setVariable(vm, VMUtil.VIEW_DATA_PREFIX+k, p);
        }
    }

    @Function("SetItemFromData")
    public void setItemFromData(VM vm, StringVal container, StringVal data, IntVal slot, StringVal notNull){
        Object f;
        if(container.get().equalsIgnoreCase("window")) {
            f = report.getView().getWindow().getDataContainer().get(data.get());
        } else if(container.get().equalsIgnoreCase("view")) {
            f = report.getView().getDataContainer().get(data.get());
        } else {
            return;
        }
        if(f instanceof ItemStack) {
            if(!notNull.get().equalsIgnoreCase("not-null") || !ItemUtil.isNull((ItemStack) f)) {
                report.getView().getInventory().setItem(slot.get(), (ItemStack) f);
            }
        } else if(f instanceof PreparedItem) {
            if(!notNull.get().equalsIgnoreCase("not-null") || !ItemUtil.isNull(((PreparedItem) f).material())) {
                report.getView().getInventory().setItem(slot.get(), ((PreparedItem) f).build());
            }
        }
    }

    @Function("SetDataFromCursor")
    public void setDataFromCursor(VM vm, StringVal container, StringVal data, StringVal notNull){
        ItemStack i = report.getPlayer().getItemOnCursor();
        if(!notNull.get().equalsIgnoreCase("not-null") || !ItemUtil.isNull(i)) {
            if(container.get().equalsIgnoreCase("window")) {
                report.getView().getWindow().getDataContainer().put(data.get(), i.clone());
                report.getPlayer().setItemOnCursor(null);
            } else if(container.get().equalsIgnoreCase("view")) {
                report.getView().getDataContainer().put(data.get(), i.clone());
                report.getPlayer().setItemOnCursor(null);
            }
        }
    }

    @Function("InstallResourcePack")
    public void installResourcePack(VM vm) {
        ResourcePack.send(report.getPlayer());
    }
}
