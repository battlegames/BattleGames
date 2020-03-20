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
package dev.anhcraft.battle.gui.menu.backpack;

import dev.anhcraft.battle.ApiProvider;
import dev.anhcraft.battle.api.BattleApi;
import dev.anhcraft.battle.api.arena.game.GamePlayer;
import dev.anhcraft.battle.api.events.ItemChooseEvent;
import dev.anhcraft.battle.api.gui.page.Pagination;
import dev.anhcraft.battle.api.gui.page.SlotChain;
import dev.anhcraft.battle.api.gui.screen.View;
import dev.anhcraft.battle.api.gui.struct.Slot;
import dev.anhcraft.battle.api.inventory.item.*;
import dev.anhcraft.battle.api.storage.data.PlayerData;
import dev.anhcraft.craftkit.abif.PreparedItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;

public abstract class ItemCompartment implements Pagination {
    public abstract ItemType getItemType();
    public abstract BattleItemModel getItem(String id, BattleApi api);
    public abstract boolean isObtainable();

    @Override
    public void supply(@NotNull Player player, @NotNull View view, @NotNull SlotChain chain) {
        BattleApi api = ApiProvider.consume();
        PlayerData pd = api.getPlayerData(player);
        if(pd == null) return;
        Collection<String> ids = pd.getBackpack().getStorage(getItemType()).listIds();
        GamePlayer gp = api.getArenaManager().getGamePlayer(player);
        if(gp != null) {
            for (Map.Entry<String, BattleItem<?>> e : gp.getIgBackpack().row(getItemType()).entrySet()) {
                if(e.getValue() instanceof NullBattleItem) {
                    ids.remove(e.getKey());
                }
            }
        }
        for(String id : ids) {
            if (!chain.hasNext()) break;
            if (chain.shouldSkip()) continue;
            BattleItemModel bi = getItem(id, api);
            if (bi == null) continue;
            PreparedItem pi = api.getItemManager().make(bi);
            if (pi == null) continue;
            Slot slot = chain.next();
            if(bi instanceof SingleSkinItem) {
                slot.setPaginationItem(((SingleSkinItem) bi).getSkin().transform(pi));
            } else if(bi instanceof GunModel) {
                slot.setPaginationItem(((GunModel) bi).getPrimarySkin().transform(pi));
            }
            if (isObtainable()) {
                slot.setAdditionalFunction(report -> {
                    if (report.getEvent() instanceof InventoryClickEvent) {
                        ItemChooseEvent e = new ItemChooseEvent(report.getPlayer(), ((InventoryClickEvent) report.getEvent()).getCurrentItem(), bi);
                        Bukkit.getPluginManager().callEvent(e);
                    }
                });
            }
        }
    }
}
