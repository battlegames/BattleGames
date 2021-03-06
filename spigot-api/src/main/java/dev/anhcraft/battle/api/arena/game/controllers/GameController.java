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
package dev.anhcraft.battle.api.arena.game.controllers;

import dev.anhcraft.battle.api.BattleApi;
import dev.anhcraft.battle.api.arena.game.*;
import dev.anhcraft.battle.api.arena.game.options.GameOptions;
import dev.anhcraft.battle.api.events.ItemChooseEvent;
import dev.anhcraft.battle.api.events.game.WeaponUseEvent;
import dev.anhcraft.battle.api.gui.screen.Window;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface GameController {
    default void onInitGame(@NotNull Game game) {
        if (game instanceof LocalGame) {
            LocalGame lc = (LocalGame) game;
            GameOptions options = game.getArena().getGameOptions();
            for (Location loc : options.getWaitSpawnPoints()) {
                lc.addInvolvedWorld(loc.getWorld());
            }
        }
    }

    @NotNull
    default GamePlayer makeGamePlayer(@NotNull Player player) {
        return new GamePlayer(player);
    }

    default boolean canJoin(@NotNull Player player, @NotNull LocalGame game) {
        return true;
    }

    void onJoin(@NotNull Player player, @NotNull LocalGame game);

    void onEnd(@NotNull LocalGame game);

    default void onQuit(@NotNull Player player, @NotNull LocalGame game) {

    }

    default void onRespawn(@NotNull PlayerRespawnEvent event, @NotNull LocalGame game) {

    }

    default void onTick(@NotNull LocalGame game) {
        if (game.getPhase() == GamePhase.PLAYING && game.getCurrentTime().get() % 100 == 0) {
            List<World> worlds = game.getInvolvedWorlds();
            long time = System.currentTimeMillis();
            for (GamePlayer gp : game.getPlayers().values()) {
                if ((time - gp.getJoinDate()) <= (1000 * 60)) continue;
                Player p = gp.toBukkit();
                if (!worlds.contains(p.getWorld())) {
                    p.sendMessage(BattleApi.getInstance().getLocalizedMessage("game.outside_playable_area"));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 0), true);
                }
            }
        }
    }

    default void onDeath(@NotNull PlayerDeathEvent event, @NotNull LocalGame game) {

    }

    default void onSwapItem(@NotNull PlayerSwapHandItemsEvent event, @NotNull LocalGame game) {

    }

    default void onDropItem(@NotNull PlayerDropItemEvent event, @NotNull LocalGame game) {

    }

    default void onClickInventory(@NotNull InventoryClickEvent event, @NotNull LocalGame game, @NotNull Player player, @NotNull Window window) {

    }

    default void onChooseItem(@NotNull ItemChooseEvent event, @NotNull LocalGame game) {

    }

    default void onUseWeapon(@NotNull WeaponUseEvent event, @NotNull LocalGame game) {

    }

    @NotNull Mode getMode();
}
