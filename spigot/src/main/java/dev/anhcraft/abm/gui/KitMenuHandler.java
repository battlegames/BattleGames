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
package dev.anhcraft.abm.gui;

import dev.anhcraft.abm.api.ApiProvider;
import dev.anhcraft.abm.api.BattleAPI;
import dev.anhcraft.abm.api.gui.*;
import dev.anhcraft.abm.api.inventory.ItemStorage;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.List;

public class KitMenuHandler extends GuiHandler implements PaginationHandler {
    @Override
    public void pullData(Pagination pagination, Player player, List<PaginationItem> data) {
        BattleAPI api = ApiProvider.consume();
        api.getPlayerData(player).ifPresent(pd -> {
            api.listKits().forEach(kit -> {
                if(kit.getPermission() != null && !player.hasPermission(kit.getPermission())) {
                    data.add(new PaginationItem(kit.getNoAccessIcon().build(), new GuiListener<SlotClickReport>(SlotClickReport.class) {
                        @Override
                        public void call(SlotClickReport event) {
                            api.getChatManager().sendPlayer(event.getPlayer(), "kit.no_permission");
                            event.getClickEvent().setCancelled(true);
                        }
                    }));
                    return;
                }
                long last = pd.getKits().getOrDefault(kit.getId(), 0L);
                if(last != 0){
                    if(kit.getRenewTime() == -1){
                        data.add(new PaginationItem(kit.getNoAccessIcon().build(), new GuiListener<SlotClickReport>(SlotClickReport.class) {
                            @Override
                            public void call(SlotClickReport event) {
                                api.getChatManager().sendPlayer(event.getPlayer(), "kit.one_time_use");
                                event.getClickEvent().setCancelled(true);
                            }
                        }));
                        return;
                    }
                    long next = last + kit.getRenewTime()*50;
                    if(next > System.currentTimeMillis()){
                        data.add(new PaginationItem(kit.getNoAccessIcon().build(), new GuiListener<SlotClickReport>(SlotClickReport.class) {
                            @Override
                            public void call(SlotClickReport event) {
                                String msg = api.getChatManager().getFormattedMessage(event.getPlayer(), "kit.unavailable");
                                msg = String.format(msg, api.formatLongFormDate(new Date(next)));
                                event.getPlayer().sendMessage(msg);
                                event.getClickEvent().setCancelled(true);
                            }
                        }));
                        return;
                    }
                }
                data.add(new PaginationItem(kit.getIcon().build(), new GuiListener<SlotClickReport>(SlotClickReport.class) {
                    @Override
                    public void call(SlotClickReport event) {
                        event.getClickEvent().setCancelled(true);
                        event.getPlayer().getInventory().addItem(kit.getVanillaItems()).values().forEach(itemStack -> event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), itemStack));
                        kit.getAbmItems().forEach((type, x) -> {
                            ItemStorage is = pd.getInventory().getStorage(type);
                            x.forEach(is::put);
                        });
                        pd.getKits().put(kit.getId(), System.currentTimeMillis());
                        api.getGuiManager().openTopInventory(event.getPlayer(), "kit_menu");
                    }
                }));
            });
        });
    }
}
