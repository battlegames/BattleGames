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
import dev.anhcraft.battle.api.gui.window.View;
import dev.anhcraft.battle.api.gui.GuiListener;
import dev.anhcraft.battle.api.gui.reports.SlotCancelReport;
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

    private void renderGui(Player player, View v){
        BattleGuiManager guiManager = ApiProvider.consume().getGuiManager();
        if(v.getWindow().getTopView() == v)
            guiManager.renderTopView(player, v.getWindow());
        else if(v.getWindow().getBottomView() == v)
            guiManager.renderBottomView(player, v.getWindow());
    }

    @Label("prev_page")
    public void prevPage(Player player, View v){
        if(v.getPagination() != null) {
            v.getPagination().prev();
            v.updatePagination(); // refresh listener
            renderGui(player, v);
        }
    }

    @Label("next_page")
    public void nextPage(Player player, View v){
        if(v.getPagination() != null) {
            v.getPagination().next();
            v.updatePagination();
            renderGui(player, v);
        }
    }

    @Label("choose_arena")
    public void chooseArena(Player player){
        ApiProvider.consume().getGuiManager().openTopGui(player, "arena_chooser");
    }

    @Label("open_kit_menu")
    public void openKitMenu(Player player){
        ApiProvider.consume().getGuiManager().openTopGui(player, "kit_menu");
    }

    @Label("open_inventory")
    public void openInv(Player player){
        ApiProvider.consume().getGuiManager().openTopGui(player, "inventory_menu");
    }

    @Label("open_market")
    public void openMarket(Player player){
        ApiProvider.consume().getGuiManager().openTopGui(player, "market_category_menu");
    }

    @Label("quit_game")
    public void quitGame(Player player){
        ApiProvider.consume().getGameManager().quit(player);
    }
}
