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
import dev.anhcraft.battle.utils.SignedInt;
import dev.anhcraft.battle.utils.TempDataContainer;
import dev.anhcraft.battle.utils.info.InfoHolder;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class View extends TempDataContainer {
    private final Map<String, SignedInt> PAGE = new HashMap<>();
    private final Slot[] slots;
    private final Gui gui;
    private final Window window;
    private final Inventory inventory;

    public View(@NotNull Gui gui, @NotNull Window window, @NotNull Inventory inventory) {
        Preconditions.checkNotNull(gui);
        Preconditions.checkNotNull(window);
        Preconditions.checkNotNull(inventory);
        this.gui = gui;
        this.window = window;
        this.inventory = inventory;
        slots = new Slot[gui.getSize()];
        for (int i = 0; i < slots.length; i++){
            Component c = gui.getComponentAt(i);
            if(c != null) {
                slots[i] = new Slot(i, c);
            }
        }
        for (String s : gui.getAllPagination()){
            PAGE.put(s, SignedInt.ZERO);
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
        return slots[position];
    }

    @NotNull
    public SignedInt getPage(@Nullable String pagination){
        SignedInt f = PAGE.get(pagination);
        if(f == null) {
            throw new IllegalArgumentException("The given pagination was not registered in this GUI");
        }
        return f;
    }

    public void setPage(@Nullable String pagination, @NotNull SignedInt page){
        if(gui.getAllPagination().contains(pagination)){
            PAGE.put(pagination, page);
        } else {
            throw new IllegalArgumentException("The given pagination was not registered in this GUI");
        }
    }

    public boolean nextPage(@Nullable String pagination){
        if(gui.getAllPagination().contains(pagination)){
            SignedInt f = PAGE.getOrDefault(pagination, SignedInt.ZERO);
            if(f.isNegative()) return false;
            PAGE.put(pagination, f.add(SignedInt.ONE));
            return true;
        } else {
            throw new IllegalArgumentException("The given pagination was not registered in this GUI");
        }
    }

    public boolean prevPage(@Nullable String pagination){
        if(gui.getAllPagination().contains(pagination)){
            SignedInt f = PAGE.getOrDefault(pagination, SignedInt.ZERO);
            if(f.asInt() == 0) return false;
            PAGE.put(pagination, f.toPositive().subtract(SignedInt.ONE));
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
