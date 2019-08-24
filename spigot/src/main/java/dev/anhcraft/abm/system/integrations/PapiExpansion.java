package dev.anhcraft.abm.system.integrations;

import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.misc.Icon;
import dev.anhcraft.jvmkit.utils.MathUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class PapiExpansion extends PlaceholderExpansion {
    public final Map<String, Function<Player, String>> handlers = new HashMap<>();
    private BattlePlugin plugin;

    public PapiExpansion(BattlePlugin plugin){
        this.plugin = plugin;
        for(Icon icon : Icon.values())
            handlers.put("icon_" + icon.name().toLowerCase(), player -> icon.getChar());

        handlers.put("exp", player -> plugin.getPlayerData(player)
                .map(playerData -> Long.toString(playerData.getExp().get()))
                .orElse(null));
        handlers.put("level", player -> plugin.getPlayerData(player)
                .map(playerData -> Integer.toString(plugin.calculateLevel(playerData.getExp().get())))
                .orElse(null));
        handlers.put("level_progress", player -> plugin.getPlayerData(player)
                .map(playerData -> {
                    long current = playerData.getExp().get();
                    int nextLv = plugin.calculateLevel(current)+1;
                    long next = plugin.calculateExp(nextLv);
                    return MathUtil.formatRound(100d/next*current);
                })
                .orElse(null));
        handlers.put("stats_win_matches", player -> plugin.getPlayerData(player)
                .map(playerData -> Integer.toString(playerData.getWinCounter().get()))
                .orElse(null));
        handlers.put("stats_lose_matches", player -> plugin.getPlayerData(player)
                .map(playerData -> Integer.toString(playerData.getLoseCounter().get()))
                .orElse(null));
        handlers.put("stats_total_matches", player -> plugin.getPlayerData(player)
                .map(playerData -> Integer.toString(playerData.getWinCounter().get()+
                        playerData.getLoseCounter().get()))
                .orElse(null));
        handlers.put("stats_headshots", player -> plugin.getPlayerData(player)
                .map(playerData -> Integer.toString(playerData.getHeadshotCounter().get()))
                .orElse(null));
        handlers.put("stats_assists", player -> plugin.getPlayerData(player)
                .map(playerData -> Integer.toString(playerData.getAssistCounter().get()))
                .orElse(null));
        handlers.put("stats_kills", player -> plugin.getPlayerData(player)
                .map(playerData -> Integer.toString(playerData.getKillCounter().get()))
                .orElse(null));
        handlers.put("stats_deaths", player -> plugin.getPlayerData(player)
                .map(playerData -> Integer.toString(playerData.getDeathCounter().get()))
                .orElse(null));
        handlers.put("game_stats_headshots", player -> plugin.gameManager.getGamePlayer(player)
                .map(gp -> Integer.toString(gp.getHeadshotCounter().get()))
                .orElse(null));
        handlers.put("game_stats_assists", player -> plugin.gameManager.getGamePlayer(player)
                .map(gp -> Integer.toString(gp.getAssistCounter().get()))
                .orElse(null));
        handlers.put("game_stats_kills", player -> plugin.gameManager.getGamePlayer(player)
                .map(gp -> Integer.toString(gp.getKillCounter().get()))
                .orElse(null));
        handlers.put("game_stats_deaths", player -> plugin.gameManager.getGamePlayer(player)
                .map(gp -> Integer.toString(gp.getDeathCounter().get()))
                .orElse(null));
        handlers.put("game_total_players", player -> plugin.gameManager.getGame(player)
                .map(gp -> Integer.toString(gp.countPlayers()))
                .orElse(null));
        handlers.put("game_current_time", player -> plugin.gameManager.getGame(player)
                .map(gp -> Long.toString(gp.getCurrentTime().get()))
                .orElse(null));
        handlers.put("game_current_time_formatted", player -> plugin.gameManager.getGame(player)
                .map(gp -> plugin.formatShortForm(gp.getCurrentTime().get()*50))
                .orElse(null));
        handlers.put("game_remaining_time", player -> plugin.gameManager.getGame(player)
                .map(gp -> Long.toString(gp.getArena().getMaxTime() - gp.getCurrentTime().get()))
                .orElse(null));
        handlers.put("game_remaining_time_formatted", player -> plugin.gameManager.getGame(player)
                .map(gp -> plugin.formatShortForm((gp.getArena().getMaxTime() - gp.getCurrentTime().get())*50))
                .orElse(null));
        handlers.put("arena_id", player -> plugin.gameManager.getGame(player)
                .map(gp -> gp.getArena().getId())
                .orElse(null));
        handlers.put("arena_name", player -> plugin.gameManager.getGame(player)
                .map(gp -> gp.getArena().getName())
                .orElse(null));
        handlers.put("arena_max_players", player -> plugin.gameManager.getGame(player)
                .map(gp -> Integer.toString(gp.getArena().getMaxPlayers()))
                .orElse(null));
        handlers.put("arena_max_time", player -> plugin.gameManager.getGame(player)
                .map(gp -> Long.toString(gp.getArena().getMaxTime()))
                .orElse(null));
        handlers.put("arena_max_time_formatted", player -> plugin.gameManager.getGame(player)
                .map(gp -> plugin.formatShortForm(gp.getArena().getMaxTime()*50))
                .orElse(null));
        handlers.put("mode_name", player -> plugin.gameManager.getGame(player)
                .map(gp -> gp.getArena().getMode().getName())
                .orElse(null));
        handlers.put("mode_description", player -> plugin.gameManager.getGame(player)
                .map(gp -> gp.getArena().getMode().getDescription())
                .orElse(null));
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return "abm";
    }

    @Override
    public String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier){
        Function<Player, String> x = handlers.get(identifier);
        return x == null ? null : x.apply(player);
    }
}
