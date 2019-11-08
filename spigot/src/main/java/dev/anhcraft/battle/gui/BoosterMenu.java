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
import dev.anhcraft.battle.api.BattleAPI;
import dev.anhcraft.battle.api.gui.Gui;
import dev.anhcraft.battle.api.gui.GuiCallback;
import dev.anhcraft.battle.api.gui.GuiListener;
import dev.anhcraft.battle.api.gui.pagination.Pagination;
import dev.anhcraft.battle.api.gui.pagination.PaginationFactory;
import dev.anhcraft.battle.api.gui.pagination.PaginationItem;
import dev.anhcraft.battle.api.gui.reports.SlotClickReport;
import dev.anhcraft.battle.api.gui.window.Window;
import dev.anhcraft.battle.api.misc.Booster;
import dev.anhcraft.battle.api.storage.data.PlayerData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class BoosterMenu extends GuiListener implements PaginationFactory {
    @Override
    public void pullData(Player player, Window window, Gui gui, Pagination pagination, List<PaginationItem> data) {
        BattleAPI api = ApiProvider.consume();
        PlayerData pd = api.getPlayerData(player);
        if(pd != null) {
            pd.getBoosters().forEach((id, date) -> {
                Booster b = api.getBooster(id);
                if(b == null){
                    return;
                }
                if(Objects.equals(id, pd.getActiveBooster()) && System.currentTimeMillis() - date > b.getExpiryTime()*50){
                    pd.getBoosters().remove(id);
                    return;
                }
                data.add(new PaginationItem(b.getIcon().build(), new GuiCallback<SlotClickReport>(SlotClickReport.class) {
                    @Override
                    public void call(@NotNull SlotClickReport event) {
                        if(pd.getActiveBooster() != null){
                            if(pd.getActiveBooster().equals(id)){
                                api.getChatManager().sendPlayer(event.getPlayer(), "booster.already_used");
                            } else {
                                api.getChatManager().sendPlayer(event.getPlayer(), "booster.another_used");
                            }
                        } else {
                            pd.setActiveBooster(id);
                            api.getChatManager().sendPlayer(event.getPlayer(), "booster.active_success");
                        }
                    }
                }));
            });
        }
    }
}
