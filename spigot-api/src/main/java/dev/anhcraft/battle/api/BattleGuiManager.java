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

import dev.anhcraft.battle.api.gui.*;
import dev.anhcraft.battle.api.gui.page.Pagination;
import dev.anhcraft.battle.api.gui.screen.View;
import dev.anhcraft.battle.api.gui.screen.Window;
import dev.anhcraft.battle.api.gui.struct.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface BattleGuiManager {
    /**
     * Registers the given GUI.
     * @param gui the GUI
     * @return {@code true} if success, or {@code false} if not
     */
    boolean registerGui(@NotNull Gui gui);

    /**
     * Registers a new GUI handler.
     * @param namespace the namespace
     * @param handler the handler
     * @return {@code true} if success, or {@code false} if not
     */
    boolean registerGuiHandler(@NotNull String namespace, @NotNull GuiHandler handler);

    /**
     * Registers the given pagination.
     * @param id the identifier
     * @param pagination the pagination
     * @return {@code true} if success, or {@code false} if not
     */
    boolean registerPagination(@NotNull String id, @NotNull Pagination pagination);

    /**
     * Gets the gui handler from its namespace.
     * @param namespace the namespace
     * @return the gui handler (or {@code null} if not exist)
     */
    @Nullable
    GuiHandler getGuiHandler(@Nullable String namespace);

    /**
     * Gets the {@link Window} of the given player.<br>
     * If it does not exist yet, a new instance will be created automatically.
     * @param player the player
     * @return a unique {@link Window} that belongs to the player
     */
    @NotNull
    Window getWindow(@NotNull Player player);

    /**
     * Renders the given view.
     * @param player who is viewing
     * @param view the view
     */
    void renderView(@NotNull Player player, @Nullable View view);

    /**
     * Renders all pagination that present on the given view.
     * @param player who is viewing
     * @param view the view
     */
    void renderPagination(@NotNull Player player, @Nullable View view);

    /**
     * Renders the given component .
     * @param player who is viewing
     * @param view the view
     * @param component the component
     */
    void renderComponent(@NotNull Player player, @Nullable View view, @Nullable Component component);

    /**
     * Updates all pagination that present on the given view.<br>
     * This method will also render the pagination.
     * @param player who is viewing
     * @param view the view
     */
    void updatePagination(@NotNull Player player, @Nullable View view);

    /**
     * Sets the GUI for the bottom inventory.
     * <br>
     * Calling this method will reset the current handlers and
     * re-render the bottom inventory of the given player.
     * @param player the player
     * @param name the name of the GUI
     * @return the view
     */
    @Nullable
    View setBottomGui(@NotNull Player player, @NotNull String name);

    /**
     * Opens a GUI as the top inventory.
     * <br>
     * Calling this method will reset the current handlers and
     * re-render the top inventory of the given player.
     * @param player the player
     * @param name the name of the GUI
     * @return the view
     */
    @Nullable
    View openTopGui(@NotNull Player player, @NotNull String name);
}
