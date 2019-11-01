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
import dev.anhcraft.battle.api.gui.*;
import dev.anhcraft.battle.api.gui.pagination.Pagination;
import dev.anhcraft.battle.api.gui.pagination.PaginationFactory;
import dev.anhcraft.battle.api.gui.pagination.PaginationItem;
import dev.anhcraft.battle.api.gui.reports.SlotClickReport;
import dev.anhcraft.battle.api.gui.window.Window;
import dev.anhcraft.battle.api.storage.data.PlayerData;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;

public class KitMenu extends GuiListener implements PaginationFactory {
    @Override
    public void pullData(Player player, Window window, Gui gui, Pagination pagination, List<PaginationItem> data) {
        BattleAPI api = ApiProvider.consume();
        PlayerData pd = api.getPlayerData(player);
        if(pd != null) {
            api.listKits(kit -> {
                if(kit.getPermission() != null && !player.hasPermission(kit.getPermission())) {
                    data.add(new PaginationItem(kit.getNoAccessIcon().build(), new GuiCallback<SlotClickReport>(SlotClickReport.class) {
                        @Override
                        public void call(@NotNull SlotClickReport event) {
                            api.getChatManager().sendPlayer(event.getPlayer(), "kit.no_permission");
                            event.getClickEvent().setCancelled(true);
                        }
                    }));
                    return;
                }
                long last = pd.getKits().getOrDefault(kit.getId(), 0L);
                if(last != 0){
                    if(kit.getRenewTime() == -1){
                        data.add(new PaginationItem(kit.getNoAccessIcon().build(), new GuiCallback<SlotClickReport>(SlotClickReport.class) {
                            @Override
                            public void call(@NotNull SlotClickReport event) {
                                api.getChatManager().sendPlayer(event.getPlayer(), "kit.one_time_use");
                                event.getClickEvent().setCancelled(true);
                            }
                        }));
                        return;
                    }
                    long next = last + kit.getRenewTime()*50;
                    if(next > System.currentTimeMillis()){
                        data.add(new PaginationItem(kit.getNoAccessIcon().build(), new GuiCallback<SlotClickReport>(SlotClickReport.class) {
                            @Override
                            public void call(@NotNull SlotClickReport event) {
                                api.getChatManager().sendPlayer(event.getPlayer(), "kit.unavailable", ChatMessageType.CHAT, x -> String.format(x, api.formatLongFormDate(new Date(next))));
                                event.getClickEvent().setCancelled(true);
                            }
                        }));
                        return;
                    }
                }
                data.add(new PaginationItem(kit.getIcon().build(), new GuiCallback<SlotClickReport>(SlotClickReport.class) {
                    @Override
                    public void call(@NotNull SlotClickReport event) {
                        event.getClickEvent().setCancelled(true);
                        kit.givePlayer(event.getPlayer(), pd);
                        pd.getKits().put(kit.getId(), System.currentTimeMillis());
                        api.getGuiManager().openTopGui(event.getPlayer(), "kit_menu");
                    }
                }));
            });
        }
    }
}
