package dev.anhcraft.abm.gui;

import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.ext.gui.GuiHandler;
import dev.anhcraft.abm.api.objects.gui.BattleGui;
import dev.anhcraft.abm.api.objects.gui.SlotCancelReport;
import dev.anhcraft.jvmkit.lang.annotation.Label;
import org.bukkit.entity.Player;

public class CoreHandler extends GuiHandler {
    public CoreHandler(BattlePlugin plugin) {
        super(plugin);
    }

    @Label({"cancel_event", "onCancellableSlot"})
    public void prevent(SlotCancelReport report) {
        report.getCancelEvent().setCancelled(true);
    }

    @Label("close")
    public void close(Player player) {
        player.closeInventory();
    }

    @Label("prev_page")
    public void prevPage(Player player, BattleGui gui){
        if(gui.getPagination() != null) {
            gui.getPagination().prev();
            gui.updatePagination(); // refresh listener
            plugin.guiManager.renderGui(player, gui);
        }
    }

    @Label("next_page")
    public void nextPage(Player player, BattleGui gui){
        if(gui.getPagination() != null) {
            gui.getPagination().next();
            gui.updatePagination();
            plugin.guiManager.renderGui(player, gui);
        }
    }

    @Label("choose_arena")
    public void chooseArena(Player player){
        plugin.guiManager.openTopInventory(player, "arena_chooser");
    }

    @Label("open_inventory")
    public void openInv(Player player){
        plugin.guiManager.openTopInventory(player, "inventory_menu");
    }

    @Label("quit_game")
    public void quitGame(Player player){
        plugin.gameManager.quit(player);
    }
}
