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
package dev.anhcraft.battle.api.gui.reports;

import dev.anhcraft.battle.api.gui.window.View;
import dev.anhcraft.battle.api.gui.window.Button;
import dev.anhcraft.jvmkit.utils.Condition;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public class SlotClickReport extends SlotReport {
    private InventoryClickEvent clickEvent;

    public SlotClickReport(@NotNull Player player, @NotNull View gui, @NotNull Button slot, @NotNull InventoryClickEvent clickEvent) {
        super(player, gui, slot);
        Condition.argNotNull("clickEvent", clickEvent);
        this.clickEvent = clickEvent;
    }

    @NotNull
    public InventoryClickEvent getClickEvent() {
        return clickEvent;
    }
}
