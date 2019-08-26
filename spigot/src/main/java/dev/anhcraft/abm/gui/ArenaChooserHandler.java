package dev.anhcraft.abm.gui;

import dev.anhcraft.craftkit.kits.abif.PreparedItem;
import dev.anhcraft.abm.api.APIProvider;
import dev.anhcraft.abm.api.BattleAPI;
import dev.anhcraft.abm.api.game.Game;
import dev.anhcraft.abm.api.gui.*;
import dev.anhcraft.abm.api.misc.info.InfoHolder;
import dev.anhcraft.abm.utils.ListUtil;
import dev.anhcraft.abm.utils.PlaceholderUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ArenaChooserHandler extends GuiHandler implements PaginationHandler {
    @Override
    public void pullData(Pagination pagination, Player player, List<PaginationItem> data) {
        BattleAPI api = APIProvider.get();
        api.listArenas().forEach(arena -> {
            InfoHolder infoHolder;
            Optional<Game> gameOpt = APIProvider.get().getGameManager().getGame(arena);
            if(gameOpt.isPresent()){
                infoHolder = new InfoHolder("game_");
                gameOpt.get().inform(infoHolder);
            } else {
                infoHolder = new InfoHolder("arena_");
                arena.inform(infoHolder);
            }
            Map<String, String> infoMap = APIProvider.get().mapInfo(infoHolder);
            PreparedItem icon = arena.getIcon();
            icon.name(ChatColor.translateAlternateColorCodes('&', PlaceholderUtils.formatInfo(icon.name(), infoMap)));
            ListUtil.update(icon.lore(), s -> ChatColor.translateAlternateColorCodes('&', PlaceholderUtils.formatInfo(s, infoMap)));
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
