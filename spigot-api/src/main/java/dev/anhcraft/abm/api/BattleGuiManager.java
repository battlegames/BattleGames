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
package dev.anhcraft.abm.api;

import dev.anhcraft.abm.api.gui.Gui;
import dev.anhcraft.abm.api.gui.GuiHandler;
import dev.anhcraft.abm.api.gui.PlayerGui;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface BattleGuiManager {
    void registerGui(@NotNull String id, @NotNull Gui gui);
    void registerGuiHandler(@NotNull String id, @NotNull GuiHandler handler);
    @NotNull PlayerGui getPlayerGui(@NotNull Player player);
    void setBottomInv(@NotNull Player player, @NotNull String name);
    void renderBottomInv(@NotNull Player player, @NotNull PlayerGui apg);
    void openTopInventory(@NotNull Player player, @NotNull String name);
    void renderTopInventory(@NotNull Player player, @NotNull PlayerGui apg);
    void destroyPlayerGui(@NotNull Player player);
}
