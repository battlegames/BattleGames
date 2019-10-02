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
import dev.anhcraft.abm.api.events.ItemChooseEvent;
import dev.anhcraft.abm.api.gui.*;
import dev.anhcraft.abm.api.inventory.items.GrenadeModel;
import dev.anhcraft.abm.api.inventory.items.ItemType;
import dev.anhcraft.abm.api.storage.data.PlayerData;
import dev.anhcraft.craftkit.kits.abif.PreparedItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GrenadeInventory extends GuiHandler implements PaginationHandler {
    @Override
    public void pullData(Pagination pagination, Player player, List<PaginationItem> data) {
        BattleAPI api = ApiProvider.consume();
        PlayerData playerData = api.getPlayerData(player);
        if(playerData != null) {
            playerData.getInventory().getStorage(ItemType.GRENADE).list((k, v) -> {
                GrenadeModel gm = api.getGrenadeModel(k);
                if (gm != null) {
                    PreparedItem pi = api.getItemManager().make(gm);
                    if(pi == null) return;
                    ItemStack item = gm.getSkin().transform(pi).build();
                    data.add(new PaginationItem(item, new GuiListener<SlotClickReport>(SlotClickReport.class) {
                        @Override
                        public void call(SlotClickReport event) {
                            event.getClickEvent().setCancelled(true);
                            ItemChooseEvent e = new ItemChooseEvent(event.getPlayer(), event.getClickEvent().getCurrentItem(), gm);
                            Bukkit.getPluginManager().callEvent(e);
                        }
                    }));
                }
            });
        }
    }
}
