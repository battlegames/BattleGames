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

import dev.anhcraft.jvmkit.utils.Condition;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class GuiSlot {
    private ConfigurationSection itemConf;
    private Collection<String> eventHandlers;
    private boolean isPaginationSlot;

    public GuiSlot(@Nullable ConfigurationSection itemConf, @NotNull Collection<String> eventHandlers, boolean isPaginationSlot) {
        Condition.argNotNull("eventHandlers", eventHandlers);
        this.isPaginationSlot = isPaginationSlot;
        this.itemConf = itemConf;
        this.eventHandlers = eventHandlers;
    }

    @Nullable
    public ConfigurationSection getItemConf() {
        return itemConf;
    }

    @NotNull
    public Collection<String> getEventHandlers() {
        return eventHandlers;
    }

    public boolean isPaginationSlot() {
        return isPaginationSlot;
    }
}
