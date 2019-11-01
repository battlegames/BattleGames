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
package dev.anhcraft.battle.api.gui.window;

import com.google.common.base.Preconditions;
import dev.anhcraft.battle.api.gui.Gui;
import dev.anhcraft.jvmkit.helpers.PaginationHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class View {
    private Gui gui;
    private Window window;
    private Button[] buttons;
    private PaginationHelper<Button> pagination;

    public View(@NotNull Gui gui, @NotNull Window window, @NotNull Button[] buttons) {
        Preconditions.checkNotNull(gui);
        Preconditions.checkNotNull(window);
        Preconditions.checkNotNull(buttons);
        this.gui = gui;
        this.window = window;
        this.buttons = buttons;
    }

    @NotNull
    public Gui getGui() {
        return gui;
    }

    @NotNull
    public Button[] getButtons() {
        return buttons;
    }

    @Nullable
    public PaginationHelper<Button> getPagination() {
        return pagination;
    }

    @NotNull
    public Window getWindow() {
        return window;
    }

    public void setPagination(@Nullable PaginationHelper<Button> pagination) {
        this.pagination = pagination;
    }

    public void updatePagination(){
        pagination.each(battleGuiSlot -> buttons[battleGuiSlot.getIndex()] = battleGuiSlot);
    }
}
