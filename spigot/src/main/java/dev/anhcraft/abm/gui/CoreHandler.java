package dev.anhcraft.abm.gui;

import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.gui.core.BattleGuiHandler;
import dev.anhcraft.abm.gui.core.PlayerGui;
import dev.anhcraft.abm.gui.core.SlotClickHandler;
import dev.anhcraft.abm.gui.core.SlotHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class CoreHandler extends BattleGuiHandler {
    public CoreHandler(BattlePlugin plugin) {
        super(plugin);
    }

    @SlotClickHandler("close")
    public void close(InventoryClickEvent event){
        event.getWhoClicked().closeInventory();
    }

    @SlotClickHandler("prev_page")
    public void prevPage(InventoryClickEvent event, PlayerGui gui){
        int c = gui.getPage();
        if(c == 0) return;
        gui.setPage(c-1);
        plugin.guiManager.openInventory((Player) event.getWhoClicked(), gui);
    }

    @SlotClickHandler("next_page")
    public void nextPage(InventoryClickEvent event, PlayerGui gui){
        if(gui.isOutOfData()) return;
        gui.setPage(gui.getPage()+1);
        plugin.guiManager.openInventory((Player) event.getWhoClicked(), gui);
    }

    @SlotHandler("choose_arena")
    public void chooseArena(Player player){
        plugin.guiManager.openInventory(player, "arena_chooser");
    }

    @SlotHandler("open_inventory")
    public void openInv(Player player){
        plugin.guiManager.openInventory(player, "inventory_menu");
    }

    @SlotHandler("quit_game")
    public void quitGame(Player player){
        plugin.gameManager.quit(player);
    }
}
