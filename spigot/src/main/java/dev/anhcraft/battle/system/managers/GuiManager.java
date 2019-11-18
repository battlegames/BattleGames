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
package dev.anhcraft.battle.system.managers;

import dev.anhcraft.battle.BattleComponent;
import dev.anhcraft.battle.BattlePlugin;
import dev.anhcraft.battle.api.BattleGuiManager;
import dev.anhcraft.battle.api.gui.Gui;
import dev.anhcraft.battle.api.gui.GuiCallback;
import dev.anhcraft.battle.api.gui.GuiListener;
import dev.anhcraft.battle.api.gui.Slot;
import dev.anhcraft.battle.api.gui.pagination.PaginationFactory;
import dev.anhcraft.battle.api.gui.pagination.PaginationItem;
import dev.anhcraft.battle.api.gui.reports.GuiReport;
import dev.anhcraft.battle.api.gui.reports.SlotCancelReport;
import dev.anhcraft.battle.api.gui.reports.SlotClickReport;
import dev.anhcraft.battle.api.gui.reports.SlotReport;
import dev.anhcraft.battle.api.gui.window.Button;
import dev.anhcraft.battle.api.gui.window.View;
import dev.anhcraft.battle.api.gui.window.Window;
import dev.anhcraft.craftkit.abif.ABIF;
import dev.anhcraft.jvmkit.helpers.PaginationHelper;
import dev.anhcraft.jvmkit.lang.annotation.Label;
import dev.anhcraft.jvmkit.utils.Condition;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static dev.anhcraft.battle.utils.PlaceholderUtil.*;

public class GuiManager extends BattleComponent implements BattleGuiManager {
    private final Map<String, Gui> GUI = new HashMap<>();
    private final Map<String, GuiListener> GUI_LISTENERS = new HashMap<>();
    private final Map<UUID, Window> WINDOWS = new HashMap<>();

    public GuiManager(BattlePlugin plugin) {
        super(plugin);
    }

    @Override
    public void registerGui(@NotNull String id, @NotNull Gui gui){
        Condition.argNotNull("id", id);
        Condition.argNotNull("gui", gui);
        GUI.put(id, gui);
    }

    @Override
    public void registerGuiHandler(@NotNull String id, @NotNull GuiListener handler){
        Condition.argNotNull("id", id);
        Condition.argNotNull("handler", handler);
        GUI_LISTENERS.put(id, handler);

        // register slot listeners here
        Method[] methods = handler.getClass().getMethods();
        for(Method m : methods){
            m.setAccessible(true);
            if(!m.isAnnotationPresent(Label.class)) continue;
            int count = m.getParameterCount();
            String[] args = m.getAnnotation(Label.class).value();
            if(args.length == 1){
                handler.getEventListeners().put(args[0], new GuiCallback<GuiReport>(GuiReport.class) {
                    @Override
                    public void call(@NotNull GuiReport event) {
                        try {
                            if (count == 1) m.invoke(handler, event.getPlayer());
                            else if (count == 2) m.invoke(handler, event.getPlayer(), event.getGui());
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else if(args.length == 2){
                String event = args[1];
                switch (event){
                    case "onSlot":{
                        handler.getEventListeners().put(args[0], new GuiCallback<SlotReport>(SlotReport.class) {
                            @Override
                            public void call(@NotNull SlotReport event) {
                                try {
                                    m.invoke(handler, event);
                                } catch (IllegalAccessException | InvocationTargetException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        break;
                    }
                    case "onClickSlot":{
                        handler.getEventListeners().put(args[0], new GuiCallback<SlotClickReport>(SlotClickReport.class) {
                            @Override
                            public void call(@NotNull SlotClickReport event) {
                                try {
                                    m.invoke(handler, event);
                                } catch (IllegalAccessException | InvocationTargetException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        break;
                    }
                    case "onCancellableSlot":{
                        handler.getEventListeners().put(args[0], new GuiCallback<SlotCancelReport>(SlotCancelReport.class) {
                            @Override
                            public void call(@NotNull SlotCancelReport event) {
                                try {
                                    m.invoke(handler, event);
                                } catch (IllegalAccessException | InvocationTargetException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        break;
                    }
                }
            }
        }
    }

    @Override
    @NotNull
    public Window getWindow(@NotNull Player player){
        Condition.argNotNull("player", player);
        Window x = WINDOWS.get(player.getUniqueId());
        if(x == null) WINDOWS.put(player.getUniqueId(), x = new Window());
        return x;
    }

    public void callEvent(Player p, int slot, boolean top) {
        Window wd = getWindow(p);
        callEvent(p, top ? wd.getTopView() : wd.getBottomView(), slot);
    }

    public void callEvent(Player p, View v, int slot) {
        callEvent(p, v, slot, null);
    }

    public void callEvent(Player p, int slot, boolean top, @Nullable Event event) {
        Window wd = getWindow(p);
        callEvent(p, top ? wd.getTopView() : wd.getBottomView(), slot, event);
    }

    public void callEvent(Player p, @Nullable View v, int slot, @Nullable Event event) {
        if(v == null) return;
        Button[] x = v.getButtons();
        if (slot < x.length) {
            Button s = x[slot];
            if(s == null) return;
            s.getEvents().forEach(gl -> {
                if(gl.getClazz() == GuiReport.class){
                    ((GuiCallback<GuiReport>) gl).call(new GuiReport(p, v));
                }
                else if(gl.getClazz() == SlotReport.class){
                    ((GuiCallback<SlotReport>) gl).call(new SlotReport(p, v, s));
                }
                else if(gl.getClazz() == SlotClickReport.class){
                    if(event instanceof InventoryClickEvent)
                        ((GuiCallback<SlotClickReport>) gl).call(
                                new SlotClickReport(p, v, s, (InventoryClickEvent) event));
                }
                else if(gl.getClazz() == SlotCancelReport.class){
                    if(event instanceof Cancellable)
                        ((GuiCallback<SlotCancelReport>) gl).call(
                                new SlotCancelReport(p, v, s, (Cancellable) event));
                }
            });
        }
    }

    public boolean validateButton(Player p, int slot, boolean top) {
        Window w = getWindow(p);
        View v = top ? w.getTopView() : w.getBottomView();
        if(v == null) return false;
        Button[] x = v.getButtons();
        if (slot < x.length) return x[slot] != null;
        return false;
    }

    private View setupGui(Player player, Window window, Gui gui){
        Button[] buttons = new Button[gui.getSize()];
        for(int i = 0; i < gui.getSize(); i++){
            List<GuiCallback<? extends GuiReport>> callbacks = new ArrayList<>();
            // only handling on normal slots
            Slot s = gui.getSlots()[i];
            if(s.isPaginationSlot()) continue;
            Collection<String> ehs = s.getEventHandlers();
            for (String eh : ehs) {
                String[] args = eh.split("::");
                if (args.length < 2) continue;

                GuiListener guiListener = GUI_LISTENERS.get(args[0]);
                if (guiListener == null) continue;

                GuiCallback<? extends GuiReport> callback = guiListener.getEventListeners().get(args[1]);
                if(callback == null) {
                    plugin.getLogger().warning("GUI callback not found: "+eh);
                    continue;
                }
                callbacks.add(callback);
            }
            buttons[i] = new Button(i, s, callbacks);
        }

        if(gui.getPagination() != null){
            GuiListener gl = GUI_LISTENERS.get(gui.getPagination().getHandler());
            if (gl instanceof PaginationFactory) {
                List<PaginationItem> data = new ArrayList<>();
                // get data
                ((PaginationFactory) gl).pullData(player, window, gui, gui.getPagination(), data);
                // slots per page
                int[] pageSlots = gui.getPagination().getSlots();
                // all pagination slots
                Button[] bs = new Button[data.size()];
                int i = 0; // data index
                int j = 0; // slot index on one page
                for(PaginationItem pi : data){
                    int index = pageSlots[j++]; // get slot index
                    Button btn = buttons[index];
                    if(btn == null) {
                        buttons[index] = (btn = new Button(index, new Slot(null, new ArrayList<>(), true), Collections.unmodifiableCollection(pi.getGuiCallbacks())));
                    } else {
                        btn.getEvents().clear();
                        btn.getEvents().addAll(pi.getGuiCallbacks());
                    }
                    btn.setCachedItem(pi.getItemStack()); // cache item
                    // put to temp pagination slots
                    bs[i++] = btn;
                    if(j == pageSlots.length) j = 0; // reset if reached the maximum slot
                }
                View v = new View(gui, window, buttons);
                v.setPagination(new PaginationHelper<>(bs, pageSlots.length));
                return v;
            }
        }
        return new View(gui, window, buttons);
    }

    @NotNull
    @Override
    public View setBottomGui(@NotNull Player player, @NotNull String name){
        Condition.argNotNull("player", player);
        Condition.argNotNull("name", name);
        Window gui = getWindow(player);
        View v = setupGui(player, gui, GUI.get(name));
        gui.setBottomView(v);
        renderBottomView(player, gui);
        return v;
    }

    @Override
    public View renderBottomView(@NotNull Player player, @NotNull Window window){
        Condition.argNotNull("player", player);
        Condition.argNotNull("apg", window);
        View v = window.getBottomView();
        if(v == null) return null;
        ItemStack[] items = renderItems(player, v);
        for(int i = 0; i < items.length; i++)
            player.getInventory().setItem(i, items[i]);
        return v;
    }

    @NotNull
    @Override
    public View openTopGui(@NotNull Player player, @NotNull String name){
        Condition.argNotNull("player", player);
        Condition.argNotNull("name", name);
        Window w = getWindow(player);
        View v = setupGui(player, w, GUI.get(name));
        w.setTopView(v);
        Inventory inv;
        if(v.getGui().getTitle() == null) inv = Bukkit.createInventory(null, v.getGui().getSize());
        else {
            String title = localizeString(v.getGui().getTitle(), plugin.getLocaleConf());
            title = formatPAPI(player, title);
            inv = Bukkit.createInventory(null, v.getGui().getSize(), title);
        }
        w.setTopInv(inv);
        renderTopView(player, w);
        player.openInventory(inv);
        v.getGui().getSound().play(player);
        return v;
    }

    @Override
    public View renderTopView(@NotNull Player player, @NotNull Window window){
        Condition.argNotNull("player", player);
        Condition.argNotNull("apg", window);
        View gui = window.getTopView();
        if(gui == null || window.getTopInv() == null) return null;
        window.getTopInv().setContents(renderItems(player, gui));
        return gui;
    }

    @Override
    public void destroyWindow(@NotNull Player player){
        Condition.argNotNull("player", player);
        WINDOWS.remove(player.getUniqueId());
    }

    private ItemStack[] renderItems(Player player, View v){
        ItemStack[] items = new ItemStack[v.getGui().getSize()];
        Button[] buttons = v.getButtons();
        for(int i = 0; i < buttons.length; i++){
            Button x = buttons[i];
            if(x != null && x.getSlot().getItemConf() != null)
                items[i] = formatPAPI(formatTranslations(ABIF.read(x.getSlot().getItemConf()), plugin.getLocaleConf()), player).build();
        }
        if(v.getPagination() != null && v.getGui().getPagination() != null){
            Button[] bt = v.getPagination().collect(); // get all slots in current page
            int[] is = v.getGui().getPagination().getSlots(); // get all slot indexes
            int len = Math.min(is.length, bt.length);
            for(int i = 0; i < len; i++) {
                if(bt[i] != null)
                    items[is[i]] = bt[i].getCachedItem();
            }
        }
        return items;
    }
}
