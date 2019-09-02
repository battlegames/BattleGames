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
package dev.anhcraft.abm.system.listeners;

import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.BattleComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockListener extends BattleComponent implements Listener {
    public BlockListener(BattlePlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void breakBlock(BlockBreakEvent event){
        if(!event.getPlayer().hasPermission("abm.block.break")){
            plugin.chatManager.sendPlayer(event.getPlayer(), "server.illegal_block_break");
            event.setCancelled(true);
            event.setDropItems(false);
            event.setExpToDrop(0);
        }
    }

    @EventHandler
    public void placeBlock(BlockPlaceEvent event){
        plugin.guiManager.callEvent(event.getPlayer(), event.getPlayer().getInventory().getHeldItemSlot(), false, event);
        if(!event.isCancelled() && !event.getPlayer().hasPermission("abm.block.place")){
            plugin.chatManager.sendPlayer(event.getPlayer(), "server.illegal_block_place");
            event.setCancelled(true);
            event.setBuild(false);
        }
    }
}
