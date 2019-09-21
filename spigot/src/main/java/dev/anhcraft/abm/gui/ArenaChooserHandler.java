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
import dev.anhcraft.abm.api.game.Game;
import dev.anhcraft.abm.api.gui.*;
import dev.anhcraft.abm.api.misc.info.InfoHolder;
import dev.anhcraft.abm.utils.PlaceholderUtils;
import dev.anhcraft.craftkit.kits.abif.PreparedItem;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class ArenaChooserHandler extends GuiHandler implements PaginationHandler {
    @Override
    public void pullData(Pagination pagination, Player player, List<PaginationItem> data) {
        BattleAPI api = ApiProvider.consume();
        api.listArenas(arena -> {
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
            PreparedItem icon = arena.getIcon();
            icon.name(ChatColor.translateAlternateColorCodes('&', PlaceholderUtils.formatInfo(icon.name(), infoMap)));
            icon.lore().replaceAll(s -> ChatColor.translateAlternateColorCodes('&', PlaceholderUtils.formatInfo(s, infoMap)));
            data.add(new PaginationItem(icon.build(), new GuiListener<SlotClickReport>(SlotClickReport.class) {
                @Override
                public void call(SlotClickReport event) {
                    event.getPlayer().closeInventory();
                    api.getGameManager().join(event.getPlayer(), arena);
                }
            }));
        });
    }
}
