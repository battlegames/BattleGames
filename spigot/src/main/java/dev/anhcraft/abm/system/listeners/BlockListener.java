package dev.anhcraft.abm.system.listeners;

import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.ext.BattleComponent;
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
            plugin.chatProvider.sendPlayer(event.getPlayer(), "server.illegal_block_break");
            event.setCancelled(true);
            event.setDropItems(false);
            event.setExpToDrop(0);
        }
    }

    @EventHandler
    public void placeBlock(BlockPlaceEvent event){
        plugin.guiManager.callEvent(event.getPlayer(), event.getPlayer().getInventory().getHeldItemSlot(), false, event);
        if(!event.isCancelled() && !event.getPlayer().hasPermission("abm.block.place")){
            plugin.chatProvider.sendPlayer(event.getPlayer(), "server.illegal_block_place");
            event.setCancelled(true);
            event.setBuild(false);
        }
    }
}
