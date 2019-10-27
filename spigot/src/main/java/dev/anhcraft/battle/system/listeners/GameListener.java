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
import dev.anhcraft.battle.api.BattleModeController;
import dev.anhcraft.battle.api.events.GameJoinEvent;
import dev.anhcraft.battle.api.events.GamePhaseChangeEvent;
import dev.anhcraft.battle.api.events.GameQuitEvent;
import dev.anhcraft.battle.api.game.GamePhase;
import dev.anhcraft.battle.api.game.LocalGame;
import dev.anhcraft.battle.system.controllers.ModeController;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Arrays;

public class GameListener extends BattleComponent implements Listener {
    public GameListener(BattlePlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void join(GameJoinEvent event){
        Player p = event.getGamePlayer().toBukkit();
        PlayerInventory i = p.getInventory();
        event.getGamePlayer().setBackupInventory(Arrays.copyOf(i.getContents(), i.getSize()));
        i.clear();
        plugin.guiManager.setBottomInv(p, "game_player_inv");
    }

    @EventHandler
    public void quit(GameQuitEvent event){
        Player p = event.getGamePlayer().toBukkit();
        ItemStack[] inv = event.getGamePlayer().getBackupInventory();
        if(inv != null) p.getInventory().setContents(inv);
        // although the inventory got backup, its handler still
        // didn't change so we must set it again
        plugin.guiManager.setBottomInv(p, "main_player_inv");

        plugin.queueTitleTask.remove(p);

        // its better to execute the following code later
        // we can ignore if the player is going to quit the server
        plugin.taskHelper.newTask(() -> {
            if(p.isOnline()) {
                plugin.resetScoreboard(p);
                p.teleport(plugin.getServerData().getSpawnPoint());

                event.getGame().getMode().getController(c -> ((ModeController) c).cancelReloadGun(p));
            }
        });
    }

    @EventHandler
    public void phaseChange(GamePhaseChangeEvent event){
        if(event.getGame() instanceof LocalGame) {
            BattleModeController bmc = event.getGame().getMode().getController();
            if (bmc != null && event.getOldPhase() == GamePhase.PLAYING) {
                ModeController mc = (ModeController) bmc;
                ((LocalGame) event.getGame()).getPlayers().keySet().forEach(mc::cancelReloadGun);
            }
        }
    }
}
