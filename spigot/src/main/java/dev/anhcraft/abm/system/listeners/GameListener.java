package dev.anhcraft.abm.system.listeners;

import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.events.GameJoinEvent;
import dev.anhcraft.abm.api.events.GameQuitEvent;
import dev.anhcraft.abm.api.ext.BattleComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class GameListener extends BattleComponent implements Listener {
    public GameListener(BattlePlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void join(GameJoinEvent event){
        Player p = event.getGamePlayer().getPlayer();
        PlayerInventory i = p.getInventory();
        event.getGamePlayer().setBackupInventory(i.getContents());
        i.clear();
        plugin.guiManager.setPlayerInventory(p, "game_player_inv");
    }

    @EventHandler
    public void quit(GameQuitEvent event){
        Player p = event.getGamePlayer().getPlayer();
        ItemStack[] inv = event.getGamePlayer().getBackupInventory();
        if(inv != null) p.getInventory().setContents(inv);
        // although the inventory got backup, its handler still
        // didn't change so we must set it again
        plugin.guiManager.setPlayerInventory(p, "main_player_inv");

        plugin.queueTitleTask.remove(p);

        // its better to execute the following code later
        // we can ignore if the player is going to quit the server
        plugin.taskManager.newTask(() -> {
            if(p.isOnline()) {
                plugin.resetScoreboard(p);
                p.teleport(plugin.getServerData().getSpawnPoint());
            }
        });
    }
}
