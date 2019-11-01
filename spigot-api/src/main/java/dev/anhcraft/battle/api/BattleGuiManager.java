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
package dev.anhcraft.battle.api;

import dev.anhcraft.battle.api.gui.BattleGui;
import dev.anhcraft.battle.api.gui.Gui;
import dev.anhcraft.battle.api.gui.GuiListener;
import dev.anhcraft.battle.api.gui.PlayerGui;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface BattleGuiManager {
    /**
     * Registers the given GUI.
     * @param id the id
     * @param gui the GUI instance
     */
    void registerGui(@NotNull String id, @NotNull Gui gui);

    /**
     * Registers a new GUI handler.
     * @param id the id
     * @param handler the handler instance
     */
    void registerGuiHandler(@NotNull String id, @NotNull GuiListener handler);

    /**
     * Gets the {@link PlayerGui} of the given player.<br>
     * If it does not exist yet, a new instance will be created automatically.
     * @param player the player
     * @return a unique {@link PlayerGui} that belongs to the player
     */
    @NotNull
    PlayerGui getPlayerGui(@NotNull Player player);

    /**
     * Sets the GUI for the bottom inventory.
     * <br>
     * Calling this method will reset the current handlers and also rerender the bottom inventory of the given player.
     * @param player the player
     * @param name the name of the GUI
     */
    @NotNull
    BattleGui setBottomInv(@NotNull Player player, @NotNull String name);

    /**
     * Re-renders the bottom GUI.
     * @param player the player
     * @param apg the {@link PlayerGui}
     */
    @Nullable
    BattleGui renderBottomInv(@NotNull Player player, @NotNull PlayerGui apg);

    /**
     * Opens a GUI as the top inventory.
     * <br>
     * Calling this method will reset the current handlers and also rerender the top inventory of the given player.
     * @param player the player
     * @param name the name of the GUI
     */
    @NotNull
    BattleGui openTopInventory(@NotNull Player player, @NotNull String name);

    /**
     * Re-renders the top GUI.
     * @param player the player
     * @param apg the {@link PlayerGui}
     */
    @Nullable
    BattleGui renderTopInventory(@NotNull Player player, @NotNull PlayerGui apg);

    /**
     * Destroys the {@link PlayerGui} of the given player.
     * <br>
     * Calling this method is considered as useless, should only provides internal uses.
     * @param player the player
     */
    void destroyPlayerGui(@NotNull Player player);
}
