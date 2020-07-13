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

package dev.anhcraft.battle.api.gui.struct;

import dev.anhcraft.battle.api.gui.SlotReport;
import dev.anhcraft.craftkit.abif.PreparedItem;
import dev.anhcraft.inst.VM;
import dev.anhcraft.jvmkit.utils.Condition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public class Slot {
    private final int position;
    private final Component component;
    private BiConsumer<VM, SlotReport> extraClickFunction;
    private PreparedItem paginationItem;

    public Slot(int position, @NotNull Component component) {
        Condition.argNotNull("component", component);
        this.position = position;
        this.component = component;
    }

    @NotNull
    public Component getComponent() {
        return component;
    }

    public int getPosition() {
        return position;
    }

    @Nullable
    public PreparedItem getPaginationItem() {
        return paginationItem;
    }

    public void setPaginationItem(@Nullable PreparedItem paginationItem) {
        this.paginationItem = paginationItem;
    }

    @Nullable
    public BiConsumer<VM, SlotReport> getExtraClickFunction() {
        return extraClickFunction;
    }

    public void setExtraClickFunction(@Nullable BiConsumer<VM, SlotReport> extraClickFunction) {
        this.extraClickFunction = extraClickFunction;
    }
}
