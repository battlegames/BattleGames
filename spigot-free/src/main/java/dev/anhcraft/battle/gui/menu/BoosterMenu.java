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
package dev.anhcraft.battle.gui.menu;

import dev.anhcraft.battle.ApiProvider;
import dev.anhcraft.battle.api.BattleApi;
import dev.anhcraft.battle.api.gui.struct.Slot;
import dev.anhcraft.battle.api.gui.screen.View;
import dev.anhcraft.battle.api.gui.page.Pagination;
import dev.anhcraft.battle.api.gui.page.SlotChain;
import dev.anhcraft.battle.api.misc.Booster;
import dev.anhcraft.battle.api.storage.data.PlayerData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

public class BoosterMenu implements Pagination {
    @Override
    public void supply(@NotNull Player player, @NotNull View view, @NotNull SlotChain chain) {
        BattleApi api = ApiProvider.consume();
        PlayerData pd = api.getPlayerData(player);
        if(pd != null) {
            for (Map.Entry<String, Long> ent : pd.getBoosters().entrySet()){
                if(!chain.hasNext()) break;
                if(chain.shouldSkip()) continue;
                String id = ent.getKey();
                Booster b = api.getBooster(id);
                if(b == null){
                    continue;
                }
                long date = ent.getValue();
                if(Objects.equals(id, pd.getActiveBooster()) && System.currentTimeMillis() - date > b.getExpiryTime()*50){
                    pd.getBoosters().remove(id);
                   continue;
                }
                Slot slot = chain.next();
                slot.setPaginationItem(b.getIcon().duplicate());
                slot.setExtraClickFunction(object -> {
                    if(pd.getActiveBooster() != null){
                        if(pd.getActiveBooster().equals(id)){
                            api.getChatManager().sendPlayer(object.getPlayer(), "booster.already_used");
                        } else {
                            api.getChatManager().sendPlayer(object.getPlayer(), "booster.another_used");
                        }
                    } else {
                        pd.setActiveBooster(id);
                        api.getChatManager().sendPlayer(object.getPlayer(), "booster.active_success");
                    }
                });
            }
        }
    }
}
