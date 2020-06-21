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
import dev.anhcraft.battle.api.gui.GuiHandler;
import dev.anhcraft.battle.api.gui.SlotReport;
import dev.anhcraft.battle.api.gui.struct.Component;
import dev.anhcraft.battle.system.ResourcePack;
import dev.anhcraft.battle.utils.TempDataContainer;
import dev.anhcraft.battle.utils.functions.Function;
import dev.anhcraft.craftkit.abif.PreparedItem;
import dev.anhcraft.craftkit.utils.ItemUtil;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Objects;
import java.util.function.Consumer;

public class CommonHandler extends GuiHandler {
    @Function("apply_value")
    public void applyVal(SlotReport report, String val) {
        Object f = report.getView().getWindow().getDataContainer().get(GDataRegistry.VALUE_CALLBACK);
        if(f instanceof Consumer){
            ((Consumer<ValueResult>) f).accept(new ValueResult(val));
        }
    }

    private void add(String key, String val, TempDataContainer tdc){
        Object o = tdc.getDataContainer().get(key);
        if(o instanceof Integer){
            int f = (int) Double.parseDouble(val);
            tdc.getDataContainer().put(key, ((Integer) o) + f);
        } else if(o instanceof Double){
            tdc.getDataContainer().put(key, ((Double) o) + Double.parseDouble(val));
        } else if(o instanceof Float){
            tdc.getDataContainer().put(key, ((Float) o) + Float.parseFloat(val));
        } else if(o instanceof Long){
            long f = (long) Double.parseDouble(val);
            tdc.getDataContainer().put(key, ((Long) o) + f);
        }
    }

    @Function("add_window_data")
    public void addWindowData(SlotReport report, String key, String delta) {
        add(key, delta, report.getView().getWindow());
    }

    @Function("add_view_data")
    public void addViewData(SlotReport report, String key, String delta) {
        add(key, delta, report.getView());
    }

    private void flip(String key, TempDataContainer tdc){
        Object o = tdc.getDataContainer().get(key);
        if(o instanceof Boolean){
            tdc.getDataContainer().put(key, !((Boolean) o));
        } else if(o instanceof Integer){
            tdc.getDataContainer().put(key, -((Integer) o));
        } else if(o instanceof Double){
            tdc.getDataContainer().put(key, -((Double) o));
        } else if(o instanceof Float){
            tdc.getDataContainer().put(key, -((Float) o));
        } else if(o instanceof Long){
            tdc.getDataContainer().put(key, -((Long) o));
        }
    }

    @Function("flip_window_data")
    public void flipWindowData(SlotReport report, String key) {
        flip(key, report.getView().getWindow());
    }

    @Function("flip_view_data")
    public void flipViewData(SlotReport report, String key) {
        flip(key, report.getView());
    }

    @Function("set_view_data")
    public void setViewData(SlotReport report, String key, String value) {
        report.getView().getDataContainer().put(key, value);
    }

    @Function("set_window_data")
    public void setWindowData(SlotReport report, String key, String value) {
        report.getView().getWindow().getDataContainer().put(key, value);
    }

    @Function("del_view_data")
    public void delViewData(SlotReport report, String key) {
        report.getView().getDataContainer().remove(key);
    }

    @Function("del_window_data")
    public void delWindowData(SlotReport report, String key) {
        report.getView().getWindow().getDataContainer().remove(key);
    }

    @Function("cancel_event")
    public void prevent(SlotReport report) {
        if(report.getEvent() instanceof Cancellable) {
            ((Cancellable) report.getEvent()).setCancelled(true);
        }
        if(report.getEvent() instanceof InventoryInteractEvent) {
            ((InventoryInteractEvent) report.getEvent()).setResult(Event.Result.DENY);
        }
    }

    @Function("close_gui")
    public void close(SlotReport report) {
        report.getPlayer().closeInventory();
    }

    @Function("refresh")
    public void refresh(SlotReport report) {
        ApiProvider.consume().getGuiManager().updateView(report.getPlayer(), report.getView());
    }

    @Function("switch_gui")
    public void switchGui(SlotReport report, String gui) {
        if(report.getView().getInventory() instanceof PlayerInventory){
            setBottom(report, gui);
        } else {
            openTop(report, gui);
        }
    }

    @Function("open_top_gui")
    public void openTop(SlotReport report, String gui) {
        ApiProvider.consume().getGuiManager().openTopGui(report.getPlayer(), gui);
    }

    @Function("set_bottom_gui")
    public void setBottom(SlotReport report, String gui) {
        ApiProvider.consume().getGuiManager().setBottomGui(report.getPlayer(), gui);
    }

    @Function("prev_page")
    public void prevPage(SlotReport report, String pagination){
        if(report.getView().prevPage(pagination)) {
            for (Component c : report.getView().getGui().getComponentOf(pagination)) {
                ApiProvider.consume().getGuiManager().updateComponent(report.getPlayer(), report.getView(), c);
            }
        }
    }

    @Function("next_page")
    public void nextPage(SlotReport report, String pagination){
        if(report.getView().nextPage(pagination)) {
            for (Component c : report.getView().getGui().getComponentOf(pagination)) {
                ApiProvider.consume().getGuiManager().updateComponent(report.getPlayer(), report.getView(), c);
            }
        }
    }

    @Function("quit_game")
    public void quitGame(SlotReport report){
        ApiProvider.consume().getArenaManager().quit(report.getPlayer());
    }

    @Function("copy_current_slot")
    public void copyCurrentSlot(SlotReport report, String container, String data){
        if(container.equalsIgnoreCase("window")) {
            report.getView().getWindow().getDataContainer().put(data, report.getPosition());
        } else if(container.equalsIgnoreCase("view")) {
            report.getView().getDataContainer().put(data, report.getPosition());
        }
    }

    @Function("pull_item_from_data")
    public void pullItemFromData(SlotReport report, String data, String notNull){
        Object f = report.getView().getWindow().getDataContainer().remove(data);
        if(f instanceof ItemStack) {
            if(!notNull.equalsIgnoreCase("not-null") || !ItemUtil.isNull((ItemStack) f)) {
                report.getSlot().getComponent().setItem(PreparedItem.of((ItemStack) f));
            }
        } else if(f instanceof PreparedItem) {
            if(!notNull.equalsIgnoreCase("not-null") || !ItemUtil.isNull(((PreparedItem) f).material())) {
                report.getSlot().getComponent().setItem((PreparedItem) f);
            }
        }
    }

    @Function("pull_item_from_cursor")
    public void pullItemFromCursor(SlotReport report, String notNull){
        ItemStack i = report.getPlayer().getItemOnCursor();
        if(!notNull.equalsIgnoreCase("not-null") || !ItemUtil.isNull(i)) {
            report.getSlot().getComponent().setItem(PreparedItem.of(i));
            report.getPlayer().setItemOnCursor(null);
        }
    }

    @Function("push_item_to_value")
    public void pushItemToValue(SlotReport report, String slot){
        Object f = report.getView().getWindow().getDataContainer().get(GDataRegistry.VALUE_CALLBACK);
        if(f instanceof Consumer){
            ((Consumer<ValueResult>) f).accept(new ValueResult(Objects.requireNonNull(report.getView().getSlot(Integer.parseInt(slot))).getComponent().getItem()));
        }
    }

    @Function("install_resource_pack")
    public void installResourcePack(SlotReport report) {
        ResourcePack.send(report.getPlayer());
    }
}
