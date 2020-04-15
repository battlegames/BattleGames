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

package dev.anhcraft.battle.api.events.gui;

import dev.anhcraft.battle.api.gui.Gui;
import dev.anhcraft.battle.api.gui.page.Pagination;
import dev.anhcraft.battle.api.gui.screen.View;
import dev.anhcraft.battle.api.gui.screen.Window;
import dev.anhcraft.battle.api.gui.struct.Component;
import dev.anhcraft.battle.api.gui.struct.Slot;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class PaginationUpdateEvent extends ComponentEvent implements Cancellable {
    public static final HandlerList handlers = new HandlerList();
    private final Pagination pagination;
    private Predicate<Slot> slotFilter;
    private boolean cancelled;

    public PaginationUpdateEvent(@NotNull Player who, @NotNull Gui gui, @NotNull Window window, @NotNull View view, @NotNull Component component, @NotNull Pagination pagination) {
        super(who, gui, window, view, component);
        this.pagination = pagination;
    }

    @NotNull
    public Pagination getPagination() {
        return pagination;
    }

    @Nullable
    public Predicate<Slot> getSlotFilter() {
        return slotFilter;
    }

    /**
     * Set the slot filter.
     * <br>
     * <b>
     *     If you are trying to add a new filter, then instead of using this method, use {@link #getSlotFilter()}.
     *     <br>
     *     Join these filters like a chain with {@link Predicate#and(Predicate)} and {@link Predicate#or(Predicate)}.
     * </b>
     * @param slotFilter slot filter
     */
    public void setSlotFilter(@Nullable Predicate<Slot> slotFilter) {
        this.slotFilter = slotFilter;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
