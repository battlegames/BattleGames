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
import dev.anhcraft.jvmkit.utils.Condition;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GuiReport {
    private Player player;
    private View gui;

    public GuiReport(@NotNull Player player, @NotNull View gui) {
        Condition.argNotNull("player", player);
        Condition.argNotNull("gui", gui);
        this.player = player;
        this.gui = gui;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    @NotNull
    public View getGui() {
        return gui;
    }
}
