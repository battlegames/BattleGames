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

import dev.anhcraft.craftkit.kits.abif.PreparedItem;
import dev.anhcraft.abm.api.APIProvider;
import dev.anhcraft.abm.api.BattleAPI;
import dev.anhcraft.abm.api.gui.*;
import dev.anhcraft.abm.api.inventory.items.AmmoModel;
import dev.anhcraft.abm.api.inventory.items.ItemType;
import dev.anhcraft.abm.api.storage.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;

public class AmmoInventory extends GuiHandler implements PaginationHandler {
    @Override
    public void pullData(Pagination pagination, Player player, List<PaginationItem> data) {
        BattleAPI api = APIProvider.get();
        Optional<PlayerData> pd = api.getPlayerData(player);
        pd.ifPresent(playerData -> {
            playerData.getInventory().getStorage(ItemType.AMMO).list().forEach(ent -> {
                Optional<AmmoModel> oam = api.getAmmoModel(ent.getKey());
                if (oam.isPresent()) {
                    AmmoModel am = oam.get();
                    PreparedItem pi = api.getItemManager().make(am);
                    if(pi == null) return;
                    ItemStack item = am.getSkin().transform(pi).build();
                    data.add(new PaginationItem(item, new GuiListener<SlotClickReport>(SlotClickReport.class) {
                        @Override
                        public void call(SlotClickReport event) {
                            event.getClickEvent().setCancelled(true);
                        }
                    }));
                }
            });
        });
    }
}
