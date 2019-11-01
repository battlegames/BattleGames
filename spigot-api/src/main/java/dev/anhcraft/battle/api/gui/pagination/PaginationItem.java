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
package dev.anhcraft.battle.api.gui.pagination;

import dev.anhcraft.battle.api.gui.GuiCallback;
import dev.anhcraft.battle.api.gui.reports.SlotReport;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Collections;

public class PaginationItem {
    private ItemStack itemStack;
    private Collection<GuiCallback<? extends SlotReport>> guiCallbacks;

    public PaginationItem(ItemStack itemStack, GuiCallback<? extends SlotReport> guiCallback) {
        this.itemStack = itemStack;
        this.guiCallbacks = Collections.singleton(guiCallback);
    }

    public PaginationItem(ItemStack itemStack, Collection<GuiCallback<? extends SlotReport>> guiCallbacks) {
        this.itemStack = itemStack;
        this.guiCallbacks = guiCallbacks;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public Collection<GuiCallback<? extends SlotReport>> getGuiCallbacks() {
        return guiCallbacks;
    }
}
