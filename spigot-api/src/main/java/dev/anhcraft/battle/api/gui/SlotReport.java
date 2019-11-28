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

import dev.anhcraft.battle.api.gui.screen.View;
import dev.anhcraft.battle.api.gui.struct.Component;
import dev.anhcraft.battle.api.gui.struct.Slot;
import dev.anhcraft.jvmkit.utils.Condition;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public class SlotReport {
    private Player player;
    private int position;
    private Event event;
    private View view;

    public SlotReport(@NotNull Player player, @NotNull Event event, @NotNull View view, int position) {
        Condition.argNotNull("player", player);
        Condition.argNotNull("event", event);
        Condition.argNotNull("view", view);
        this.player = player;
        this.event = event;
        this.view = view;
        this.position = position;
    }

    @NotNull
    public Event getEvent() {
        return event;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    @NotNull
    public View getView() {
        return view;
    }

    public int getPosition() {
        return position;
    }

    @NotNull
    public Slot getSlot() {
        // since the function can only be triggered when there is a component, the following slot should be
        // non-null, if null then we are in an invalid state
        Slot s = view.getSlot(position);
        if(s == null)
            throw new IllegalStateException("Invalid slot detected!");
        return s;
    }

    @NotNull
    public Component getComponent() {
        return getSlot().getComponent();
    }
}
