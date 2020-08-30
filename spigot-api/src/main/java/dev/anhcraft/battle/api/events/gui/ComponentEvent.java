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
import dev.anhcraft.battle.api.gui.screen.View;
import dev.anhcraft.battle.api.gui.screen.Window;
import dev.anhcraft.battle.api.gui.struct.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ComponentEvent extends ViewEvent {
    public static final HandlerList handlers = new HandlerList();
    private final Component component;

    public ComponentEvent(@NotNull Player who, @NotNull Gui gui, @NotNull Window window, @NotNull View view, @NotNull Component component) {
        super(who, gui, window, view);
        this.component = component;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @NotNull
    public Component getComponent() {
        return component;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
