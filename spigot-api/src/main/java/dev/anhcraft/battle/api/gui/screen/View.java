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
package dev.anhcraft.battle.api.gui.screen;

import com.google.common.base.Preconditions;
import dev.anhcraft.battle.api.gui.Gui;
import dev.anhcraft.battle.api.gui.struct.Component;
import dev.anhcraft.battle.api.gui.struct.Slot;
import dev.anhcraft.battle.api.misc.TempDataContainer;
import dev.anhcraft.battle.api.misc.info.InfoHolder;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class View extends TempDataContainer {
    private final Map<String, Integer> PAGE = new HashMap<>();
    private Slot[] slots;
    private Gui gui;
    private Window window;
    private Inventory inventory;

    public View(@NotNull Gui gui, @NotNull Window window, @NotNull Inventory inventory) {
        Preconditions.checkNotNull(gui);
        Preconditions.checkNotNull(window);
        Preconditions.checkNotNull(inventory);
        this.gui = gui;
        this.window = window;
        this.inventory = inventory;
        slots = new Slot[gui.getSize()];
        for (String s : gui.getAllPagination()){
            PAGE.put(s, 0);
        }
    }

    @NotNull
    public Gui getGui() {
        return gui;
    }

    @NotNull
    public Window getWindow() {
        return window;
    }

    @NotNull
    public Inventory getInventory() {
        return inventory;
    }

    @Nullable
    public Slot getSlot(int position){
        if(position < 0 || position >= gui.getSize()) return null;
        Slot slot = slots[position];
        if(slot == null){
            Component c = gui.getComponentAt(position);
            if(c == null) return null;
            slots[position] = (slot = new Slot(position, c));
        }
        return slot;
    }

    @NotNull
    public Integer getPage(@Nullable String pagination){
        Integer f = PAGE.get(pagination);
        if(f == null) {
            throw new IllegalArgumentException("The given pagination was not registered in this GUI");
        }
        return f;
    }

    public void setPage(@Nullable String pagination, int page){
        if(gui.getAllPagination().contains(pagination)){
            PAGE.put(pagination, page);
        } else {
            throw new IllegalArgumentException("The given pagination was not registered in this GUI");
        }
    }

    public boolean nextPage(@Nullable String pagination){
        if(gui.getAllPagination().contains(pagination)){
            int f = PAGE.getOrDefault(pagination, 0);
            if(f < 0) return false;
            PAGE.put(pagination, f + 1);
            return true;
        } else {
            throw new IllegalArgumentException("The given pagination was not registered in this GUI");
        }
    }

    public boolean prevPage(@Nullable String pagination){
        if(gui.getAllPagination().contains(pagination)){
            int f = PAGE.getOrDefault(pagination, 0);
            if(f <= 0) return false;
            PAGE.put(pagination, f - 1);
            return true;
        } else {
            throw new IllegalArgumentException("The given pagination was not registered in this GUI");
        }
    }

    @Override
    public void inform(@NotNull InfoHolder holder) {
        super.inform(holder);
        InfoHolder gh = new InfoHolder("gui_");
        gui.inform(gh);
        holder.link(gh);
    }
}
