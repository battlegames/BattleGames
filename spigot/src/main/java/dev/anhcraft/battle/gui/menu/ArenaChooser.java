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

import dev.anhcraft.battle.api.ApiProvider;
import dev.anhcraft.battle.api.BattleAPI;
import dev.anhcraft.battle.api.game.Arena;
import dev.anhcraft.battle.api.game.Game;
import dev.anhcraft.battle.api.gui.struct.Slot;
import dev.anhcraft.battle.api.gui.screen.View;
import dev.anhcraft.battle.api.gui.page.Pagination;
import dev.anhcraft.battle.api.gui.page.SlotChain;
import dev.anhcraft.battle.utils.info.InfoHolder;
import dev.anhcraft.battle.utils.PlaceholderUtil;
import dev.anhcraft.craftkit.abif.PreparedItem;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ArenaChooser implements Pagination {
    @Override
    public void supply(@NotNull Player player, @NotNull View view, @NotNull SlotChain chain) {
        BattleAPI api = ApiProvider.consume();
        for (Arena arena : api.listArenas()){
            if(!chain.hasNext()) break;
            if(chain.shouldSkip()) continue;
            Slot slot = chain.next();
            InfoHolder infoHolder;
            Game game = ApiProvider.consume().getGameManager().getGame(arena);
            if(game != null){
                infoHolder = new InfoHolder("game_");
                game.inform(infoHolder);
            } else {
                infoHolder = new InfoHolder("arena_");
                arena.inform(infoHolder);
            }
            Map<String, String> infoMap = ApiProvider.consume().mapInfo(infoHolder);
            PreparedItem icon = arena.getIcon().duplicate();
            icon.name(PlaceholderUtil.formatInfo(icon.name(), infoMap));
            icon.lore().replaceAll(s -> PlaceholderUtil.formatInfo(s, infoMap));
            slot.setPaginationItem(icon);
            slot.setAdditionalFunction(object -> api.getGameManager().join(object.getPlayer(), arena));
        }
    }
}
