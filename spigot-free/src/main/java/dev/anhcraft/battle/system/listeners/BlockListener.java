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
import dev.anhcraft.battle.api.BattleApi;
import dev.anhcraft.battle.utils.BlockPosition;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;

public class BlockListener extends BattleComponent implements Listener {
    public BlockListener(BattlePlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void placeBlock(BlockPlaceEvent event){
        plugin.guiManager.callClickEvent(event.getPlayer(), event.getPlayer().getInventory().getHeldItemSlot(), false, event);
    }

    @EventHandler(ignoreCancelled = true)
    public void changeSign(SignChangeEvent event){
        String[] lines = event.getLines();
        if(lines.length >= 2 && (lines[0].contains("Battle") || lines[0].contains("battle"))){
            if(BattleApi.getInstance().getArena(lines[1]) != null) {
                BattleApi.getInstance().getServerData().setJoinSign(BlockPosition.of(event.getBlock()), lines[1]);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void breakBlock(BlockBreakEvent event){
        BlockPosition bp = BlockPosition.of(event.getBlock());
        String str = BattleApi.getInstance().getServerData().getJoinSign(bp);
        if(str != null) {
            event.setCancelled(true);
            if (event.getPlayer().isSneaking() && event.getPlayer().hasPermission("battle.join_signs.break")) {
                BattleApi.getInstance().getServerData().setJoinSign(bp, null);
            }
        }
    }
}
