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

import dev.anhcraft.abm.api.events.ItemChooseEvent;
import dev.anhcraft.abm.api.game.Game;
import dev.anhcraft.abm.api.game.Mode;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.jetbrains.annotations.NotNull;

public interface BattleModeController {
    default boolean canJoin(Player player, Game game){
        return true;
    }

    void onJoin(Player player, Game game);

    void onEnd(Game game);

    default void onQuit(Player player, Game game){

    }

    default void onRespawn(PlayerRespawnEvent event, Game game){

    }

    default void onTask(Game game){

    }

    default void onDeath(PlayerDeathEvent event, Game game){

    }

    default void onSwapHand(PlayerSwapHandItemsEvent event, Game game){

    }

    default void onDropItem(PlayerDropItemEvent event, Game game){

    }

    default void onClickInventory(InventoryClickEvent event, Game game, Player player){

    }

    default void onChooseItem(ItemChooseEvent event, Game game){

    }

    @NotNull Mode getMode();
}
