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

import dev.anhcraft.battle.api.gui.reports.GuiReport;
import dev.anhcraft.jvmkit.utils.Condition;
import org.jetbrains.annotations.NotNull;

public abstract class GuiCallback<T extends GuiReport> {
    private Class<T> clazz;

    protected GuiCallback(@NotNull Class<T> clazz) {
        Condition.argNotNull("clazz", clazz);
        this.clazz = clazz;
    }

    public abstract void call(@NotNull T event);

    @NotNull
    public Class<T> getClazz() {
        return clazz;
    }
}
