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
package dev.anhcraft.battle.gui.handlers;

import dev.anhcraft.battle.api.ApiProvider;
import dev.anhcraft.battle.api.BattleGuiManager;
import dev.anhcraft.battle.api.gui.BattleGui;
import dev.anhcraft.battle.api.gui.GuiListener;
import dev.anhcraft.battle.api.gui.SlotCancelReport;
import dev.anhcraft.jvmkit.lang.annotation.Label;
import org.bukkit.entity.Player;

public class CoreListener extends GuiListener {
    @Label({"cancel_event", "onCancellableSlot"})
    public void prevent(SlotCancelReport report) {
        report.getCancelEvent().setCancelled(true);
    }

    @Label("close")
    public void close(Player player) {
        player.closeInventory();
    }

    private void renderGui(Player player, BattleGui gui){
        BattleGuiManager guiManager = ApiProvider.consume().getGuiManager();
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
        ApiProvider.consume().getGuiManager().openTopInventory(player, "arena_chooser");
    }

    @Label("open_kit_menu")
    public void openKitMenu(Player player){
        ApiProvider.consume().getGuiManager().openTopInventory(player, "kit_menu");
    }

    @Label("open_inventory")
    public void openInv(Player player){
        ApiProvider.consume().getGuiManager().openTopInventory(player, "inventory_menu");
    }

    @Label("quit_game")
    public void quitGame(Player player){
        ApiProvider.consume().getGameManager().quit(player);
    }
}