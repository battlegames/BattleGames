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
package dev.anhcraft.battle.gui;

import dev.anhcraft.battle.api.ApiProvider;
import dev.anhcraft.battle.api.gui.GuiHandler;
import dev.anhcraft.battle.api.gui.SlotReport;
import dev.anhcraft.battle.utils.functions.Function;
import org.bukkit.event.Cancellable;

public class CommonHandler extends GuiHandler {
    @Function("cancel_event")
    public void prevent(SlotReport report) {
        if(report.getEvent() instanceof Cancellable) {
            ((Cancellable) report.getEvent()).setCancelled(true);
        }
    }

    @Function("close_gui")
    public void close(SlotReport report) {
        report.getPlayer().closeInventory();
    }

    @Function("open_top_gui")
    public void openTop(SlotReport report, String gui) {
        ApiProvider.consume().getGuiManager().openTopGui(report.getPlayer(), gui);
    }

    @Function("set_bottom_gui")
    public void setBottom(SlotReport report, String gui) {
        ApiProvider.consume().getGuiManager().setBottomGui(report.getPlayer(), gui);
    }

    @Function("prev_page")
    public void prevPage(SlotReport report, String pagination){
        if(report.getView().prevPage(pagination)) {
            ApiProvider.consume().getGuiManager().updatePagination(report.getPlayer(), report.getView());
        }
    }

    @Function("next_page")
    public void nextPage(SlotReport report, String pagination){
        if(report.getView().nextPage(pagination)) {
            ApiProvider.consume().getGuiManager().updatePagination(report.getPlayer(), report.getView());
        }
    }

    @Function("quit_game")
    public void quitGame(SlotReport report){
        ApiProvider.consume().getGameManager().quit(report.getPlayer());
    }
}
