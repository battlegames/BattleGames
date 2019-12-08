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
import dev.anhcraft.battle.api.gui.GuiHandler;
import dev.anhcraft.battle.api.gui.SlotReport;
import dev.anhcraft.battle.api.gui.page.Pagination;
import dev.anhcraft.battle.api.gui.page.SlotChain;
import dev.anhcraft.battle.api.gui.screen.View;
import dev.anhcraft.battle.api.gui.screen.Window;
import dev.anhcraft.battle.api.gui.struct.Component;
import dev.anhcraft.battle.api.gui.struct.Slot;
import dev.anhcraft.battle.utils.functions.FunctionLinker;
import dev.anhcraft.battle.utils.info.InfoHolder;
import dev.anhcraft.craftkit.abif.PreparedItem;
import dev.anhcraft.jvmkit.utils.Condition;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
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
        int page = Math.abs(view.getPage(pgn));
        Iterator<Integer> it = cpn.getSlots().iterator();
        PagedSlotChain chain = new PagedSlotChain(it, view, page * cpn.getSlots().size());
        pg.supply(player, view, chain);
        if(page > 0) {
            if (chain.getAllocatedSlot() == 0) {
                // empty page is something special
                view.setPage(pgn, page - 1);
            } else if (chain.getAllocatedSlot() < cpn.getSlots().size()) {
                view.setPage(pgn, -page);
                while (it.hasNext()) {
                    Slot x = view.getSlot(it.next());
                    if(x != null){
                        x.setAdditionalFunction(null);
                        x.setPaginationItem(null);
                    }
                }
            } else if (chain.getAllocatedSlot() == cpn.getSlots().size()) {
                // if run out of the chain, it MAY have a new page
                // we will set it again to ensure the page number is POSITIVE
                view.setPage(pgn, page);
            }
        }
    }

    private void drawComponent(Player player, View view, Component c, Map<String, String> info){
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
                            formatInfo(
                                formatTranslations(item.duplicate(), plugin.getLocaleConf()),
                                info
                            ),
                            player
                    ).build());
        }
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

    private void refreshComponent(Player player, View view, Component c, Map<String, String> infoMap){
        if(c.getPagination() != null) {
            Pagination pagination = PAGES.get(c.getPagination());
            if(pagination == null) {
                plugin.getLogger().warning("Unknown pagination in component: " + c.getId());
            } else updatePagination(player, view, c, pagination, c.getPagination());
        }
        drawComponent(player, view, c, infoMap);
    }

    private void refreshView(Player player, View view){
        refreshView(player, view, plugin.mapInfo(collectInfo(view)));
    }

    private void refreshView(Player player, View view, Map<String, String> infoMap){
        for(Component c : view.getGui().getComponents()) {
            refreshComponent(player, view, c, infoMap);
        }
    }

    private View createView(Player player, Window window, Gui gui, Map<String, String> infoMap){
        return createView(
                window, gui,
                Bukkit.createInventory(
                        null,
                        gui.getSize(),
                        formatPAPI(
                                player,
                                formatInfo(
                                        localizeString(gui.getTitle(), plugin.getLocaleConf()),
                                        infoMap
                                )
                        )
                )
        );
    }

    private View createView(Window window, Gui gui, Inventory inventory){
        return new View(gui, window, inventory);
    }

    public GuiManager(BattlePlugin plugin) {
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
        refreshComponent(player, view, component, plugin.mapInfo(collectInfo(view)));
    }

    @Override
    public View setBottomGui(@NotNull Player player, @NotNull String name){
        Condition.argNotNull("player", player);
        Condition.argNotNull("name", name);
        Window gui = getWindow(player);
        Gui g = GUI.get(name);
        if(g == null) return null;
        View v = createView(gui, g, player.getInventory());
        gui.setBottomView(v);
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
        if(w.getTopView() != null){
            player.closeInventory();
        }
        Map<String, String> info = plugin.mapInfo(collectInfo(w));
        View v = createView(player, w, g, info);
        w.setTopView(v);
        refreshView(player, v, info);
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
            as++;
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
