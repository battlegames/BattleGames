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

package dev.anhcraft.battle.gui.inst;

import dev.anhcraft.battle.api.BattleApi;
import dev.anhcraft.battle.api.arena.game.GamePlayer;
import dev.anhcraft.battle.api.gui.GuiHandler;
import dev.anhcraft.battle.api.gui.SlotReport;
import dev.anhcraft.battle.api.inventory.item.BattleItem;
import dev.anhcraft.battle.api.inventory.item.BattleItemModel;
import dev.anhcraft.craftkit.utils.ItemUtil;
import dev.anhcraft.inst.VM;
import dev.anhcraft.inst.annotations.Function;
import dev.anhcraft.inst.annotations.Namespace;
import dev.anhcraft.inst.values.StringVal;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Namespace("Item")
public class ItemFunctions extends GuiHandler {
    public ItemFunctions(SlotReport report) {
        super(report);
    }

    @Function("HandleItemDrop")
    public void handleItemDrop(VM vm, StringVal type) {
        Player player = report.getPlayer();
        ItemStack item = player.getItemOnCursor();
        if(!ItemUtil.isNull(item)) {
            GamePlayer gp = BattleApi.getInstance().getArenaManager().getGamePlayer(player);
            if(gp != null) {
                BattleItem<?> bi = BattleApi.getInstance().getItemManager().read(item);
                if (bi != null && bi.getModel() != null) {
                    BattleItemModel m = bi.getModel();
                    if (m.getItemType().name().equalsIgnoreCase(type.get())) {
                        gp.getIgBackpack().put(m.getItemType(), m.getId(), bi);
                        player.setItemOnCursor(null);
                        player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 3f, 1f);
                        BattleApi.getInstance().getGuiManager().updateView(report.getPlayer(), report.getView());
                    } else {
                        player.playSound(player.getLocation(), Sound.ITEM_SHIELD_BLOCK, 3f, 1f);
                    }
                }
            }
        }
    }
}
