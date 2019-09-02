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
package dev.anhcraft.abm.api.gui;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class BattleGuiSlot {
    private int index;
    private GuiSlot slot;
    private Collection<GuiListener<? extends GuiReport>> events;
    private ItemStack cachedItem;

    public BattleGuiSlot(int index, GuiSlot slot, Collection<GuiListener<? extends GuiReport>> events) {
        this.index = index;
        this.slot = slot;
        this.events = events;
    }

    public GuiSlot getSlot() {
        return slot;
    }

    public Collection<GuiListener<? extends GuiReport>> getEvents() {
        return events;
    }

    @Nullable
    public ItemStack getCachedItem() {
        return cachedItem;
    }

    public void setCachedItem(@Nullable ItemStack cachedItem) {
        this.cachedItem = cachedItem;
    }

    public int getIndex() {
        return index;
    }
}
