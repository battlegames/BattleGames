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

import dev.anhcraft.battle.api.events.ItemChooseEvent;
import dev.anhcraft.battle.api.game.LocalGame;
import dev.anhcraft.battle.api.game.Mode;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.jetbrains.annotations.NotNull;

public interface BattleModeController {
    default boolean canJoin(Player player, LocalGame game){
        return true;
    }

    void onJoin(Player player, LocalGame game);

    void onEnd(LocalGame game);

    default void onQuit(Player player, LocalGame game){

    }

    default void onRespawn(PlayerRespawnEvent event, LocalGame game){

    }

    default void onTick(LocalGame game){

    }

    default void onDeath(PlayerDeathEvent event, LocalGame game){

    }

    default void onSwapItem(PlayerSwapHandItemsEvent event, LocalGame game){

    }

    default void onDropItem(PlayerDropItemEvent event, LocalGame game){

    }

    default void onClickInventory(InventoryClickEvent event, LocalGame game, Player player){

    }

    default void onChooseItem(ItemChooseEvent event, LocalGame game){

    }

    @NotNull Mode getMode();
}
