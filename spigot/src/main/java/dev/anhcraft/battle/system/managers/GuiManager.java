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
import dev.anhcraft.battle.api.gui.*;
import dev.anhcraft.battle.api.gui.page.Pagination;
import dev.anhcraft.battle.api.gui.page.SlotChain;
import dev.anhcraft.battle.api.gui.screen.View;
import dev.anhcraft.battle.api.gui.screen.Window;
import dev.anhcraft.battle.api.gui.struct.Component;
import dev.anhcraft.battle.api.gui.struct.Slot;
import dev.anhcraft.battle.utils.functions.FunctionLinker;
import dev.anhcraft.craftkit.abif.PreparedItem;
import dev.anhcraft.jvmkit.utils.Condition;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import static dev.anhcraft.battle.utils.PlaceholderUtil.*;

public class GuiManager extends BattleComponent implements BattleGuiManager {
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
        int page = view.getPage(pgn);
        if(page < 0) page *= -1;
        PagedSlotChain chain = new PagedSlotChain(cpn.getSlots().iterator(), view, page * cpn.getSlots().size());
        pg.supply(player, view, chain);
        if(chain.getAllocatedSlot() < cpn.getSlots().size()){
            view.setPage(pgn, -page);
        }
    }

    private void drawComponent(Player player, View view, Component c){
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
                            formatTranslations(item.duplicate(), plugin.getLocaleConf()),
                            player
                    ).build());
        }
    }

    private void drawItems(Player player, View view, boolean paginationOnly){
        for(Component c : view.getGui().getComponents()){
            if(c.getPagination() == null && paginationOnly) {
                continue;
            }
            drawComponent(player, view, c);
        }
    }

    private View prepareView(Player player, Window window, Gui gui){
        return prepareView(
                player,
                window, gui,
                Bukkit.createInventory(
                        null,
                        gui.getSize(),
                        formatPAPI(player, localizeString(gui.getTitle(), plugin.getLocaleConf()))
                )
        );
    }

    private View prepareView(Player player, Window window, Gui gui, Inventory inventory){
        View view = new View(gui, window, inventory);
        for(Component c : view.getGui().getComponents()){
            if(c.getPagination() == null) {
                continue;
            }
            Pagination pagination = PAGES.get(c.getPagination());
            if(pagination == null){
                plugin.getLogger().warning("Unknown pagination in component: "+c.getId());
                continue;
            }
            updatePagination(player, view, c, pagination, c.getPagination());
            drawComponent(player, view, c);
        }
        return view;
    }

    public GuiManager(BattlePlugin plugin) {
        super(plugin);
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
    public Window getWindow(@NotNull Player player){
        Condition.argNotNull("player", player);
        Window x = WINDOWS.get(player.getUniqueId());
        if(x == null) WINDOWS.put(player.getUniqueId(), x = new Window());
        return x;
    }

    @Override
    public void renderView(@NotNull Player player, @Nullable View view){
        Condition.argNotNull("player", player);
        if (view == null) return;
        drawItems(player, view, false);
    }

    @Override
    public void renderPagination(@NotNull Player player, @Nullable View view) {
        Condition.argNotNull("player", player);
        if (view == null) return;
        drawItems(player, view, true);
    }

    @Override
    public void renderComponent(@NotNull Player player, @Nullable View view, @Nullable Component component) {
        Condition.argNotNull("player", player);
        if (view == null || component == null) return;
        drawComponent(player, view, component);
    }

    @Override
    public void updatePagination(@NotNull Player player, @Nullable View view) {
        Condition.argNotNull("player", player);
        if (view == null) return;
        for(Component c : view.getGui().getComponents()){
            if(c.getPagination() == null) {
                continue;
            }
            Pagination pagination = PAGES.get(c.getPagination());
            if(pagination == null){
                plugin.getLogger().warning("Unknown pagination in component: "+c.getId());
                continue;
            }
            updatePagination(player, view, c, pagination, c.getPagination());
            drawComponent(player, view, c);
        }
    }

    @Override
    public View setBottomGui(@NotNull Player player, @NotNull String name){
        Condition.argNotNull("player", player);
        Condition.argNotNull("name", name);
        Window gui = getWindow(player);
        Gui g = GUI.get(name);
        if(g == null) return null;
        View v = prepareView(player, gui, g, player.getInventory());
        gui.setBottomView(v);
        renderView(player, v);
        return v;
    }

    @Override
    public View openTopGui(@NotNull Player player, @NotNull String name){
        Condition.argNotNull("player", player);
        Condition.argNotNull("name", name);
        Window w = getWindow(player);
        Gui g = GUI.get(name);
        if(g == null) return null;
        View v = prepareView(player, w, g);
        w.setTopView(v);
        renderView(player, v);
        player.openInventory(v.getInventory());
        if (v.getGui().getSound() != null) {
            v.getGui().getSound().play(player);
        }
        return v;
    }

    private static class PagedSlotChain implements SlotChain {
        private Iterator<Integer> it;
        private View view;
        private int sc;
        private int as;

        private PagedSlotChain(Iterator<Integer> it, View view, int sc) {
            this.it = it;
            this.view = view;
            this.sc = sc;
        }

        @Override
        @NotNull
        public Slot next() {
            Slot s = view.getSlot(it.next());
            if(s == null){
                throw new IllegalStateException("Pagination slot is null");
            }
            as--;
            return s;
        }

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public boolean shouldSkip() {
            if(sc > 0){
                sc--;
                return true;
            }
            return false;
        }

        int getAllocatedSlot() {
            return as;
        }
    }
}
