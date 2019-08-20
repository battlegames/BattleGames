package dev.anhcraft.abm.gui;

import dev.anhcraft.abm.api.APIProvider;
import dev.anhcraft.abm.api.BattleGuiManager;
import dev.anhcraft.abm.api.gui.BattleGui;
import dev.anhcraft.abm.api.gui.GuiHandler;
import dev.anhcraft.abm.api.gui.SlotCancelReport;
import dev.anhcraft.jvmkit.lang.annotation.Label;
import org.bukkit.entity.Player;

public class CoreHandler extends GuiHandler {
    @Label({"cancel_event", "onCancellableSlot"})
    public void prevent(SlotCancelReport report) {
        report.getCancelEvent().setCancelled(true);
    }

    @Label("close")
    public void close(Player player) {
        player.closeInventory();
    }

    private void renderGui(Player player, BattleGui gui){
        BattleGuiManager guiManager = APIProvider.get().getGuiManager();
        if(gui.getPlayerGui().getTopGui() == gui)
            guiManager.renderTopInventory(player, gui.getPlayerGui());
        else if(gui.getPlayerGui().getBottomGui() == gui)
            guiManager.renderBottomInv(player, gui.getPlayerGui());
    }

    @Label("prev_page")
    public void prevPage(Player player, BattleGui gui){
        if(gui.getPagination() != null) {
            gui.getPagination().prev();
            gui.updatePagination(); // refresh listener
            renderGui(player, gui);
        }
    }

    @Label("next_page")
    public void nextPage(Player player, BattleGui gui){
        if(gui.getPagination() != null) {
            gui.getPagination().next();
            gui.updatePagination();
            renderGui(player, gui);
        }
    }

    @Label("choose_arena")
    public void chooseArena(Player player){
        APIProvider.get().getGuiManager().openTopInventory(player, "arena_chooser");
    }

    @Label("open_kit_menu")
    public void openKitMenu(Player player){
        APIProvider.get().getGuiManager().openTopInventory(player, "kit_menu");
    }

    @Label("open_inventory")
    public void openInv(Player player){
        APIProvider.get().getGuiManager().openTopInventory(player, "inventory_menu");
    }

    @Label("quit_game")
    public void quitGame(Player player){
        APIProvider.get().getGameManager().quit(player);
    }
}
