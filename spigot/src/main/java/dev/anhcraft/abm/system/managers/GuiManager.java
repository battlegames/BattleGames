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
package dev.anhcraft.abm.system.managers;

import dev.anhcraft.abm.BattleComponent;
import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.BattleGuiManager;
import dev.anhcraft.abm.api.gui.*;
import dev.anhcraft.abm.utils.PlaceholderUtils;
import dev.anhcraft.craftkit.kits.abif.ABIF;
import dev.anhcraft.craftkit.kits.abif.PreparedItem;
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

public class GuiManager extends BattleComponent implements BattleGuiManager {
    private final Map<String, Gui> GUI = new HashMap<>();
    private final Map<String, GuiHandler> GUI_HANDLERS = new HashMap<>();
    private final Map<UUID, PlayerGui> PLAYER_GUI = new HashMap<>();

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
    public void registerGuiHandler(@NotNull String id, @NotNull GuiHandler handler){
        Condition.argNotNull("id", id);
        Condition.argNotNull("handler", handler);
        GUI_HANDLERS.put(id, handler);

        // register slot listeners here
        Method[] methods = handler.getClass().getMethods();
        for(Method m : methods){
            m.setAccessible(true);
            if(!m.isAnnotationPresent(Label.class)) continue;
            int count = m.getParameterCount();
            String[] args = m.getAnnotation(Label.class).value();
            if(args.length == 1){
                handler.getEventListeners().put(args[0], new GuiListener<GuiReport>(GuiReport.class) {
                    @Override
                    public void call(GuiReport event) {
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
                        handler.getEventListeners().put(args[0], new GuiListener<SlotReport>(SlotReport.class) {
                            @Override
                            public void call(SlotReport event) {
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
                        handler.getEventListeners().put(args[0], new GuiListener<SlotClickReport>(SlotClickReport.class) {
                            @Override
                            public void call(SlotClickReport event) {
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
                        handler.getEventListeners().put(args[0], new GuiListener<SlotCancelReport>(SlotCancelReport.class) {
                            @Override
                            public void call(SlotCancelReport event) {
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
    @NotNull public PlayerGui getPlayerGui(@NotNull Player player){
        Condition.argNotNull("player", player);
        PlayerGui x = PLAYER_GUI.get(player.getUniqueId());
        if(x == null) PLAYER_GUI.put(player.getUniqueId(), x = new PlayerGui());
        return x;
    }

    public void callEvent(Player p, int slot, boolean top) {
        PlayerGui pg = getPlayerGui(p);
        callEvent(p, top ? pg.getTopGui() : pg.getBottomGui(), slot);
    }

    public void callEvent(Player p, BattleGui bg, int slot) {
        callEvent(p, bg, slot, null);
    }

    public void callEvent(Player p, int slot, boolean top, @Nullable Event event) {
        PlayerGui pg = getPlayerGui(p);
        callEvent(p, top ? pg.getTopGui() : pg.getBottomGui(), slot, event);
    }

    public void callEvent(Player p, @Nullable BattleGui bg, int slot, @Nullable Event event) {
        if(bg == null) return;
        BattleGuiSlot[] x = bg.getSlots();
        if (slot < x.length) {
            BattleGuiSlot s = x[slot];
            if(s == null) return;
            s.getEvents().forEach(gl -> {
                if(gl.getClazz() == GuiReport.class){
                    ((GuiListener<GuiReport>) gl).call(new GuiReport(p, bg));
                }
                else if(gl.getClazz() == SlotReport.class){
                    ((GuiListener<SlotReport>) gl).call(new SlotReport(p, bg, s));
                }
                else if(gl.getClazz() == SlotClickReport.class){
                    if(event instanceof InventoryClickEvent)
                        ((GuiListener<SlotClickReport>) gl).call(
                                new SlotClickReport(p, bg, s, (InventoryClickEvent) event));
                }
                else if(gl.getClazz() == SlotCancelReport.class){
                    if(event instanceof Cancellable)
                        ((GuiListener<SlotCancelReport>) gl).call(
                                new SlotCancelReport(p, bg, s, (Cancellable) event));
                }
            });
        }
    }

    public boolean validateButton(Player p, int slot, boolean top) {
        PlayerGui pg = getPlayerGui(p);
        BattleGui bg = top ? pg.getTopGui() : pg.getBottomGui();
        if(bg == null) return false;
        BattleGuiSlot[] x = bg.getSlots();
        if (slot < x.length) return x[slot] != null;
        return false;
    }

    private BattleGui setupGui(Player player, PlayerGui pg, Gui gui){
        BattleGuiSlot[] slots = new BattleGuiSlot[gui.getSize()];
        for(int i = 0; i < gui.getSize(); i++){
            List<GuiListener<? extends GuiReport>> listeners = new ArrayList<>();
            // only handling on normal slots
            GuiSlot s = gui.getSlots()[i];
            if(s.isPaginationSlot()) continue;
            Collection<String> ehs = s.getEventHandlers();
            for (String eh : ehs) {
                String[] args = eh.split("::");
                if (args.length < 2) continue;

                GuiHandler guiHandler = GUI_HANDLERS.get(args[0]);
                if (guiHandler == null) continue;

                GuiListener<? extends GuiReport> listener = guiHandler.getEventListeners().get(args[1]);
                listeners.add(listener);
            }
            slots[i] = new BattleGuiSlot(i, s, listeners);
        }

        if(gui.getPagination() != null){
            GuiHandler gh = GUI_HANDLERS.get(gui.getPagination().getHandler());
            if (gh instanceof PaginationHandler) {
                List<PaginationItem> data = new ArrayList<>();
                // get data
                ((PaginationHandler) gh).pullData(gui.getPagination(), player, data);
                // slots per page
                int[] pageSlots = gui.getPagination().getSlots();
                // all pagination slots
                BattleGuiSlot[] ps = new BattleGuiSlot[data.size()];
                int i = 0; // data index
                int j = 0; // slot index on one page
                for(PaginationItem elem : data){
                    int index = pageSlots[j++]; // get slot index
                    BattleGuiSlot s = slots[index];
                    if(s == null) {
                        slots[index] = (s = new BattleGuiSlot(index, new GuiSlot(null, new ArrayList<>(), true), Collections.unmodifiableCollection(elem.getGuiListeners())));
                    } else {
                        s.getEvents().clear();
                        s.getEvents().addAll(elem.getGuiListeners());
                    }
                    s.setCachedItem(elem.getItemStack()); // cache item
                    // put to temp pagination slots
                    ps[i++] = s;
                    if(j == pageSlots.length) j = 0; // reset if reached the maximum slot
                }
                BattleGui bg = new BattleGui(gui, pg, slots);
                bg.setPagination(new PaginationHelper<>(ps, pageSlots.length));
                return bg;
            }
        }
        return new BattleGui(gui, pg, slots);
    }

    @Override
    public void setBottomInv(@NotNull Player player, @NotNull String name){
        Condition.argNotNull("player", player);
        Condition.argNotNull("name", name);
        PlayerGui gui = getPlayerGui(player);
        gui.setBottomGui(setupGui(player, gui, GUI.get(name)));
        renderBottomInv(player, gui);
    }

    @Override
    public void renderBottomInv(@NotNull Player player, @NotNull PlayerGui apg){
        Condition.argNotNull("player", player);
        Condition.argNotNull("apg", apg);
        BattleGui bg = apg.getBottomGui();
        if(bg == null) return;
        ItemStack[] items = renderItems(player, bg);
        for(int i = 0; i < items.length; i++)
            player.getInventory().setItem(i, items[i]);
    }

    @Override
    public void openTopInventory(@NotNull Player player, @NotNull String name){
        Condition.argNotNull("player", player);
        Condition.argNotNull("name", name);
        PlayerGui pg = getPlayerGui(player);
        BattleGui bg = setupGui(player, pg, GUI.get(name));
        pg.setTopGui(bg);
        Inventory inv;
        if(bg.getGui().getTitle() == null) inv = Bukkit.createInventory(null, bg.getGui().getSize());
        else {
            String title = PlaceholderUtils.localizeString(bg.getGui().getTitle(), plugin.getLocaleConf());
            title = PlaceholderUtils.formatPAPI(player, title);
            inv = Bukkit.createInventory(null, bg.getGui().getSize(), title);
        }
        pg.setTopInv(inv);
        renderTopInventory(player, pg);
        player.openInventory(inv);
        bg.getGui().getSound().play(player);
    }

    @Override
    public void renderTopInventory(@NotNull Player player, @NotNull PlayerGui apg){
        Condition.argNotNull("player", player);
        Condition.argNotNull("apg", apg);
        BattleGui gui = apg.getTopGui();
        if(gui == null || apg.getTopInv() == null) return;
        apg.getTopInv().setContents(renderItems(player, gui));
    }

    @Override
    public void destroyPlayerGui(@NotNull Player player){
        Condition.argNotNull("player", player);
        PLAYER_GUI.remove(player.getUniqueId());
    }

    private ItemStack[] renderItems(Player player, BattleGui bg){
        ItemStack[] items = new ItemStack[bg.getGui().getSize()];
        BattleGuiSlot[] bs = bg.getSlots();
        for(int i = 0; i < bs.length; i++){
            BattleGuiSlot x = bs[i];
            if(x != null && x.getSlot() != null && x.getSlot().getItemConf() != null)
                items[i] = formatStrings(ABIF.read(x.getSlot().getItemConf()), player).build();
        }
        if(bg.getPagination() != null && bg.getGui().getPagination() != null){
            BattleGuiSlot[] ps = bg.getPagination().collect(); // get all slots in current page
            int[] is = bg.getGui().getPagination().getSlots(); // get all slot indexes
            int len = Math.min(is.length, ps.length);
            for(int i = 0; i < len; i++) {
                if(ps[i] != null)
                    items[is[i]] = ps[i].getCachedItem();
            }
        }
        return items;
    }

    private PreparedItem formatStrings(PreparedItem pi, Player player) {
        pi.name(PlaceholderUtils.formatPAPI(player, PlaceholderUtils.localizeString(pi.name(), plugin.getLocaleConf())));
        pi.lore(PlaceholderUtils.formatPAPI(player, PlaceholderUtils.localizeStrings(pi.lore(), plugin.getLocaleConf())));
        return pi;
    }
}
