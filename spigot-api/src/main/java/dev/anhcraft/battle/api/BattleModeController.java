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
import dev.anhcraft.battle.api.game.GamePlayer;
import dev.anhcraft.battle.api.game.LocalGame;
import dev.anhcraft.battle.api.mode.Mode;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.jetbrains.annotations.NotNull;

public interface BattleModeController {
    @NotNull
    default GamePlayer makeGamePlayer(@NotNull Player player){
        return new GamePlayer(player);
    }

    default boolean canJoin(@NotNull Player player, @NotNull LocalGame game){
        return true;
    }

    void onJoin(@NotNull Player player, @NotNull LocalGame game);

    void onEnd(@NotNull LocalGame game);

    default void onQuit(@NotNull Player player, @NotNull LocalGame game){

    }

    default void onRespawn(@NotNull PlayerRespawnEvent event, @NotNull LocalGame game){

    }

    default void onTick(@NotNull LocalGame game){

    }

    default void onDeath(@NotNull PlayerDeathEvent event, @NotNull LocalGame game){

    }

    default void onSwapItem(@NotNull PlayerSwapHandItemsEvent event, @NotNull LocalGame game){

    }

    default void onDropItem(@NotNull PlayerDropItemEvent event, @NotNull LocalGame game){

    }

    default void onClickInventory(@NotNull InventoryClickEvent event, @NotNull LocalGame game, @NotNull Player player){

    }

    default void onChooseItem(@NotNull ItemChooseEvent event, @NotNull LocalGame game){

    }

    @NotNull Mode getMode();
}
