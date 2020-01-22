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
import dev.anhcraft.battle.api.events.gui.PaginationUpdateEvent;
import dev.anhcraft.battle.api.gui.GuiManager;
import dev.anhcraft.battle.api.events.gui.ComponentRenderEvent;
import dev.anhcraft.battle.api.events.gui.GuiOpenEvent;
import dev.anhcraft.battle.api.events.gui.ViewRenderEvent;
import dev.anhcraft.battle.api.gui.Gui;
import dev.anhcraft.battle.api.gui.GuiHandler;
import dev.anhcraft.battle.api.gui.SlotReport;
import dev.anhcraft.battle.api.gui.page.Pagination;
import dev.anhcraft.battle.api.gui.page.SlotChain;
import dev.anhcraft.battle.api.gui.screen.View;
import dev.anhcraft.battle.api.gui.screen.Window;
import dev.anhcraft.battle.api.gui.struct.Component;
import dev.anhcraft.battle.api.gui.struct.Slot;
import dev.anhcraft.battle.system.debugger.BattleDebugger;
import dev.anhcraft.battle.utils.functions.FunctionLinker;
import dev.anhcraft.battle.utils.info.InfoHolder;
import dev.anhcraft.battle.utils.info.InfoReplacer;
import dev.anhcraft.craftkit.abif.PreparedItem;
import dev.anhcraft.jvmkit.utils.Condition;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

import static dev.anhcraft.battle.utils.PlaceholderUtil.*;

public class BattleGuiManager extends BattleComponent implements GuiManager {
    public final Map<String, Gui> GUI = new HashMap<>();
    private final Map<String, GuiHandler> GUI_HANDLERS = new HashMap<>();
    private final Map<String, Pagination> PAGES = new HashMap<>();
    private final Map<UUID, Window> WINDOWS = new HashMap<>();

    public void callEvent(Player p, int slot, boolean isTop, Event event) {
        Window w = getWindow(p);
        View v = isTop ? w.getTopView() : w.getBottomView();
        if(v == null) return;
        Slot s = v.getSlot(slot);
        if(s == null) return;
        for (FunctionLinker<SlotReport> fc : s.getComponent().getFunctions()){
            fc.call(new SlotReport(p, event, v, slot));
        }
        if (s.getAdditionalFunction() != null) {
            s.getAdditionalFunction().call(new SlotReport(p, event, v, slot));
        }
    }

    private void updatePagination(Player player, View view, Component cpn, Pagination pg, String pgn){
        BattleDebugger.startTiming("gui-pagination-update");
        PaginationUpdateEvent event = new PaginationUpdateEvent(player, view.getGui(), view.getWindow(), view, cpn, pg);
        Bukkit.getPluginManager().callEvent(event);
        if(!event.isCancelled()) {
            int page = Math.abs(view.getPage(pgn));
            PagedSlotChain chain = new PagedSlotChain(cpn.getSlots(), view, page, event.getSlotFilter());
            pg.supply(player, view, chain);
            if (chain.getAllocatedSlot() == 0) {
                // empty page is something special
                view.setPage(pgn, page - 1);
            } else if (chain.getAllocatedSlot() < cpn.getSlots().size()) {
                view.setPage(pgn, -page);
                while (chain.hasNext()) {
                    Slot x = chain.next();
                    x.setAdditionalFunction(null);
                    x.setPaginationItem(null);
                }
            } else if (chain.getAllocatedSlot() == cpn.getSlots().size()) {
                // if run out of the chain, it MAY have a new page
                // we will set it again to ensure the page number is POSITIVE
                view.setPage(pgn, page);
            }
        }
        BattleDebugger.endTiming("gui-pagination-update");
    }

    private void drawComponent(Player player, View view, Component c, InfoReplacer info){
        BattleDebugger.startTiming("gui-component-render");
        for (int slot : c.getSlots()) {
            PreparedItem item = c.getItem();
            if(c.getPagination() != null){
                Slot s = view.getSlot(slot);
                if(s != null && s.getPaginationItem() != null) {
                    item = s.getPaginationItem();
                }
            }
            view.getInventory().setItem(slot,
                    formatPAPI(
                            info.replace(
                                formatTranslations(item.duplicate(), plugin.getLocaleConf())
                            ),
                            player
                    ).build());
        }
        BattleDebugger.endTiming("gui-component-render");
    }

    private InfoHolder collectInfo(Window window){
        InfoHolder wdHolder = new InfoHolder("window_");
        window.inform(wdHolder);
        return wdHolder;
    }

    private InfoHolder collectInfo(View view, InfoHolder holder){
        InfoHolder vwHolder = new InfoHolder("view_");
        view.inform(vwHolder);
        holder.link(vwHolder);
        return holder;
    }

    private void refreshComponent(Player player, View view, Component c, InfoReplacer infoMap){
        ComponentRenderEvent event = new ComponentRenderEvent(player, view.getGui(), view.getWindow(), view, c);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()) return;
        if(c.getPagination() != null) {
            Pagination pagination = PAGES.get(c.getPagination());
            if(pagination == null) {
                plugin.getLogger().warning("Unknown pagination in component: " + c.getId());
            } else updatePagination(player, view, c, pagination, c.getPagination());
        }
        drawComponent(player, view, c, infoMap);
    }

    private void refreshView(Player player, View view){
        ViewRenderEvent event = new ViewRenderEvent(player, view.getGui(), view.getWindow(), view);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()) return;
        refreshView(player, view, collectInfo(view).compile());
    }

    private void refreshView(Player player, View view, InfoReplacer replacer){
        for(Component c : view.getGui().getComponents()) {
            refreshComponent(player, view, c, replacer);
        }
    }

    private View createView(Player player, Window window, Gui gui, InfoReplacer replacer){
        return createView(
                window, gui,
                Bukkit.createInventory(
                        null,
                        gui.getSize(),
                        formatPAPI(
                                player,
                                replacer.replace(
                                        localizeString(gui.getTitle(), plugin.getLocaleConf())
                                )
                        )
                )
        );
    }

    private View createView(Window window, Gui gui, Inventory inventory){
        return new View(gui, window, inventory);
    }

    public BattleGuiManager(BattlePlugin plugin) {
        super(plugin);
    }

    @NotNull
    public InfoHolder collectInfo(@NotNull View view){
        Condition.argNotNull("view", view);
        return collectInfo(view, collectInfo(view.getWindow()));
    }

    @Override
    public boolean registerGui(@NotNull Gui gui) {
        Condition.argNotNull("gui", gui);
        if(GUI.containsKey(gui.getId())) return false;
        GUI.put(gui.getId(), gui);
        return true;
    }

    public void destroyWindow(Player player){
        WINDOWS.remove(player.getUniqueId());
    }

    @Override
    public boolean registerGuiHandler(@NotNull String namespace, @NotNull GuiHandler handler) {
        Condition.argNotNull("namespace", namespace);
        Condition.argNotNull("handler", handler);
        if(GUI_HANDLERS.containsKey(namespace)) return false;
        GUI_HANDLERS.put(namespace, handler);
        return true;
    }

    @Override
    public boolean registerPagination(@NotNull String id, @NotNull Pagination pagination) {
        Condition.argNotNull("id", id);
        Condition.argNotNull("pagination", pagination);
        if(PAGES.containsKey(id)) return false;
        PAGES.put(id, pagination);
        return true;
    }

    @Override
    @Nullable
    public GuiHandler getGuiHandler(@Nullable String namespace) {
        return GUI_HANDLERS.get(namespace);
    }

    @Override
    @NotNull
    public Window getWindow(@NotNull HumanEntity player){
        Condition.argNotNull("player", player);
        Window x = WINDOWS.get(player.getUniqueId());
        if(x == null) WINDOWS.put(player.getUniqueId(), x = new Window());
        return x;
    }

    @Override
    public void updateView(@NotNull Player player, @Nullable View view){
        Condition.argNotNull("player", player);
        if (view == null) return;
        refreshView(player, view);
    }

    @Override
    public void updateComponent(@NotNull Player player, @Nullable View view, @Nullable Component component) {
        Condition.argNotNull("player", player);
        if (view == null || component == null) return;
        refreshComponent(player, view, component, collectInfo(view).compile());
    }

    @Override
    public View setBottomGui(@NotNull Player player, @NotNull String name){
        Condition.argNotNull("player", player);
        Condition.argNotNull("name", name);
        Window w = getWindow(player);
        Gui g = GUI.get(name);
        if(g == null) return null;
        GuiOpenEvent event = new GuiOpenEvent(player, g, w);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()) return null;
        View v = createView(w, g, player.getInventory());
        w.setBottomView(v);
        refreshView(player, v);
        return v;
    }

    @Override
    public View openTopGui(@NotNull Player player, @NotNull String name){
        Condition.argNotNull("player", player);
        Condition.argNotNull("name", name);
        Window w = getWindow(player);
        Gui g = GUI.get(name);
        if(g == null) return null;
        GuiOpenEvent event = new GuiOpenEvent(player, g, w);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()) return null;
        InfoReplacer info = collectInfo(w).compile();
        View v = createView(player, w, g, info);
        if(w.getTopView() != null){
            w.getDataContainer().put("switchView", true);
        }
        w.setTopView(v);
        refreshView(player, v, info);
        player.openInventory(v.getInventory());
        if (v.getGui().getSound() != null) {
            v.getGui().getSound().play(player);
        }
        return v;
    }

    private static class PagedSlotChain implements SlotChain {
        private View view;
        private List<Integer> slots;
        private int prevSlots;
        private int allocatedSlot;
        private int cursor = -1;
        private Predicate<Slot> slotFilter;

        private PagedSlotChain(@NotNull List<Integer> slots, @NotNull View view, int page, @Nullable Predicate<Slot> slotFilter) {
            this.slots = slots;
            this.view = view;
            this.prevSlots = slots.size() * page;
            this.slotFilter = slotFilter;
        }

        @NotNull
        private Slot get(int cursor){
            Slot s = view.getSlot(slots.get(cursor));
            if(s == null){
                throw new IllegalStateException("Pagination slot is null");
            }
            if (slotFilter == null || slotFilter.test(s)) {
                return s;
            } else {
                // make a dummy one
                // this will never get queried
                return new Slot(s.getPosition(), s.getComponent());
            }
        }

        @NotNull
        public Slot get(){
            return get(cursor);
        }

        @Override
        @NotNull
        public Slot next() {
            if(cursor == slots.size()){
                throw new IndexOutOfBoundsException(cursor+" = "+slots.size());
            }
            cursor++;
            allocatedSlot++;
            return get();
        }

        @NotNull
        public Slot prev() {
            if(cursor == 0){
                throw new IndexOutOfBoundsException(cursor+" = 0");
            }
            cursor--;
            return get();
        }

        @Override
        public boolean hasNext() {
            return !slots.isEmpty() && cursor < slots.size() - 1;
        }

        public boolean hasPrev() {
            return cursor > 0;
        }

        @Override
        public boolean shouldSkip() {
            if(prevSlots > 0){
                prevSlots--;
                return true;
            }
            return false;
        }

        int getAllocatedSlot() {
            return allocatedSlot;
        }
    }
}
