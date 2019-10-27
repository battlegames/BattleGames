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
package dev.anhcraft.battle.api.gui;

import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Collections;

public class PaginationItem {
    private ItemStack itemStack;
    private Collection<GuiListener<? extends SlotReport>> guiListeners;

    public PaginationItem(ItemStack itemStack, GuiListener<? extends SlotReport> guiListener) {
        this.itemStack = itemStack;
        this.guiListeners = Collections.singleton(guiListener);
    }

    public PaginationItem(ItemStack itemStack, Collection<GuiListener<? extends SlotReport>> guiListeners) {
        this.itemStack = itemStack;
        this.guiListeners = guiListeners;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public Collection<GuiListener<? extends SlotReport>> getGuiListeners() {
        return guiListeners;
    }
}
