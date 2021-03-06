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

import dev.anhcraft.battle.api.gui.page.Pagination;
import dev.anhcraft.battle.api.gui.screen.View;
import dev.anhcraft.battle.api.gui.screen.Window;
import dev.anhcraft.battle.api.gui.struct.Component;
import dev.anhcraft.battle.utils.info.InfoHolder;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface GuiManager {
    /**
     * Collects the info of the given {@link View}
     *
     * @param view view
     * @return info holder
     */
    @NotNull
    InfoHolder collectInfo(@NotNull View view);

    /**
     * Registers the given GUI.
     *
     * @param gui the GUI
     * @return {@code true} if success, or {@code false} if not
     */
    boolean registerGui(@NotNull Gui gui);

    /**
     * Registers a new GUI handler.
     *
     * @param handlerClass the handler's class
     * @param <T>          gui handler
     * @return {@code true} if success, or {@code false} if not
     */
    <T extends GuiHandler> boolean registerGuiHandler(@NotNull Class<T> handlerClass);

    /**
     * Registers the given pagination.
     *
     * @param id         the identifier
     * @param pagination the pagination
     * @return {@code true} if success, or {@code false} if not
     */
    boolean registerPagination(@NotNull String id, @NotNull Pagination pagination);

    /**
     * Gets the {@link Window} of the given player.<br>
     * If it does not exist yet, a new instance will be created automatically.
     *
     * @param player the player
     * @return a unique {@link Window} that belongs to the player
     */
    @NotNull
    Window getWindow(@NotNull HumanEntity player);

    /**
     * Updates the given view.
     *
     * @param player who is viewing
     * @param view   the view
     */
    void updateView(@NotNull Player player, @Nullable View view);

    /**
     * Updates the given component .
     *
     * @param player    who is viewing
     * @param view      the view
     * @param component the component
     */
    void updateComponent(@NotNull Player player, @Nullable View view, @Nullable Component component);

    /**
     * Sets the GUI for the bottom inventory.
     * <br>
     * Calling this method will reset the current handlers and
     * re-render the bottom inventory of the given player.
     *
     * @param player the player
     * @param name   the name of the GUI
     * @return the view
     */
    @Nullable
    View setBottomGui(@NotNull Player player, @NotNull String name);

    /**
     * Opens a GUI as the top inventory.
     * <br>
     * Calling this method will reset the current handlers and
     * re-render the top inventory of the given player.
     *
     * @param player the player
     * @param name   the name of the GUI
     * @return the view
     */
    @Nullable
    View openTopGui(@NotNull Player player, @NotNull String name);
}
