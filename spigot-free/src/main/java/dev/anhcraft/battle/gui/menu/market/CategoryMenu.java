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
package dev.anhcraft.battle.gui.menu.market;

import dev.anhcraft.battle.ApiProvider;
import dev.anhcraft.battle.api.BattleApi;
import dev.anhcraft.battle.api.arena.game.Game;
import dev.anhcraft.battle.api.gui.NativeGui;
import dev.anhcraft.battle.api.gui.page.Pagination;
import dev.anhcraft.battle.api.gui.page.SlotChain;
import dev.anhcraft.battle.api.gui.screen.View;
import dev.anhcraft.battle.api.gui.struct.Slot;
import dev.anhcraft.battle.api.market.Category;
import dev.anhcraft.battle.api.market.Market;
import dev.anhcraft.battle.gui.GDataRegistry;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CategoryMenu implements Pagination {
    @Override
    public void supply(@NotNull Player player, @NotNull View view, @NotNull SlotChain chain) {
        BattleApi api = ApiProvider.consume();
        Market mk = api.getMarket();
        Game g = api.getArenaManager().getGame(player);
        for(Category c : mk.getCategories()){
            if(!chain.hasNext()) break;
            if(chain.shouldSkip()) continue;
            if(c.isInGameOnly()) {
                if(g == null) continue;
                if(c.getGameModeReserved() != null && c.getGameModeReserved().stream().map(String::toLowerCase).noneMatch(s -> s.equals(g.getMode().getId()))) continue;
            }
            Slot slot = chain.next();
            slot.setPaginationItem(c.getIcon().duplicate());
            slot.setExtraClickFunction((vm, report) -> {
                view.getWindow().getDataContainer().put(GDataRegistry.MARKET_CATEGORY, c);
                api.getGuiManager().openTopGui(player, NativeGui.MARKET_PRODUCT_MENU);
            });
        }
    }
}
