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
package dev.anhcraft.battle.system.listeners;

import dev.anhcraft.battle.BattleComponent;
import dev.anhcraft.battle.BattlePlugin;
import dev.anhcraft.battle.api.arena.game.GamePhase;
import dev.anhcraft.battle.api.arena.game.LocalGame;
import dev.anhcraft.battle.api.arena.game.controllers.GameController;
import dev.anhcraft.battle.api.events.game.GameJoinEvent;
import dev.anhcraft.battle.api.events.game.GamePhaseChangeEvent;
import dev.anhcraft.battle.api.events.game.GameQuitEvent;
import dev.anhcraft.battle.api.gui.NativeGui;
import dev.anhcraft.battle.system.controllers.GameControllerImpl;
import dev.anhcraft.battle.utils.EntityUtil;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

import java.util.Arrays;

public class GameListener extends BattleComponent implements Listener {
    public GameListener(BattlePlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void join(GameJoinEvent event) {
        Player p = event.getGamePlayer().toBukkit();
        PlayerInventory i = p.getInventory();
        event.getGamePlayer().setBackupInventory(Arrays.copyOf(i.getContents(), i.getSize()));
        i.clear();
        String mode = event.getGame().getMode().getId();
        if (plugin.guiManager.setBottomGui(p, NativeGui.GAME_PLAYER_INV + "_" + mode) == null) {
            plugin.guiManager.setBottomGui(p, NativeGui.GAME_PLAYER_INV);
        }
    }

    @EventHandler
    public void quit(GameQuitEvent event) {
        Player p = event.getGamePlayer().toBukkit();
        ItemStack[] inv = event.getGamePlayer().getBackupInventory();
        if (inv != null) p.getInventory().setContents(inv);
        // although the inventory got backup, its handler still
        // didn't change so we must set it again
        plugin.guiManager.setBottomGui(p, NativeGui.MAIN_PLAYER_INV);

        for (PotionEffect pe : p.getActivePotionEffects()) {
            p.removePotionEffect(pe.getType());
        }

        plugin.queueTitleTask.remove(p);

        // its better to execute the following code later
        // we can ignore if the player is going to quit the server
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            if (p.isOnline()) {
                plugin.resetScoreboard(p);
                EntityUtil.teleport(p, plugin.getServerData().getSpawnPoint(), ok -> {
                    event.getGame().getMode().getController(c -> ((GameControllerImpl) c).cancelReloadGun(p));
                    plugin.guiManager.getWindow(p).cleanData(s -> s.startsWith("game_"));
                });
            }
        });
    }

    @EventHandler
    public void phaseChange(GamePhaseChangeEvent event) {
        if (event.getGame() instanceof LocalGame) {
            GameController controller = event.getGame().getMode().getController();
            if (controller != null) {
                if (event.getOldPhase() == GamePhase.PLAYING) {
                    if (controller instanceof GameControllerImpl) {
                        GameControllerImpl mc = (GameControllerImpl) controller;
                        ((LocalGame) event.getGame()).getPlayers().keySet().forEach(mc::cancelReloadGun);
                    }
                } else if (event.getNewPhase() == GamePhase.PLAYING) {
                    if (plugin.generalConf.shouldHealOnGameStart()) {
                        ((LocalGame) event.getGame()).getPlayers().keySet().forEach(p -> p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
                    }
                }
            }
        }
    }
}
