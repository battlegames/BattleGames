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
package dev.anhcraft.battle.system.integrations;

import dev.anhcraft.battle.BattlePlugin;
import dev.anhcraft.battle.api.arena.game.GamePlayer;
import dev.anhcraft.battle.api.arena.game.LocalGame;
import dev.anhcraft.battle.api.Icon;
import dev.anhcraft.battle.api.storage.data.PlayerData;
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

        handlers.put("exp", player -> {
            PlayerData pd = plugin.getPlayerData(player);
            return pd == null ? null : Long.toString(pd.getExp().get());
        });
        handlers.put("level", player -> {
            PlayerData pd = plugin.getPlayerData(player);
            return pd == null ? null : Integer.toString(plugin.calculateLevel(pd.getExp().get()));
        });
        handlers.put("level_progress", player -> {
            PlayerData pd = plugin.getPlayerData(player);
            if(pd != null) {
                long midExp = pd.getExp().get();
                int lv = plugin.calculateLevel(midExp);
                long startExp = plugin.calculateExp(lv);
                long endExp = plugin.calculateExp(lv + 1);
                long delta1 = endExp - startExp;
                long delta2 = midExp - startExp;
                return MathUtil.formatRound(100d/delta1*delta2);
            } else return null;
        });
        handlers.put("stats_win_matches", player -> {
            PlayerData pd = plugin.getPlayerData(player);
            return pd == null ? null : Integer.toString(pd.getWinCounter().get());
        });
        handlers.put("stats_lose_matches", player -> {
            PlayerData pd = plugin.getPlayerData(player);
            return pd == null ? null : Integer.toString(pd.getLoseCounter().get());
        });
        handlers.put("stats_total_matches", player -> {
            PlayerData pd = plugin.getPlayerData(player);
            return pd == null ? null : Integer.toString(pd.getWinCounter().get() + pd.getLoseCounter().get());
        });
        handlers.put("stats_headshots", player -> {
            PlayerData pd = plugin.getPlayerData(player);
            return pd == null ? null : Integer.toString(pd.getHeadshotCounter().get());
        });
        handlers.put("stats_assists", player -> {
            PlayerData pd = plugin.getPlayerData(player);
            return pd == null ? null : Integer.toString(pd.getAssistCounter().get());
        });
        handlers.put("stats_first_kills", player -> {
            PlayerData pd = plugin.getPlayerData(player);
            return pd == null ? null : Integer.toString(pd.getFirstKillCounter().get());
        });
        handlers.put("stats_kills", player -> {
            PlayerData pd = plugin.getPlayerData(player);
            return pd == null ? null : Integer.toString(pd.getKillCounter().get());
        });
        handlers.put("stats_deaths", player -> {
            PlayerData pd = plugin.getPlayerData(player);
            return pd == null ? null : Integer.toString(pd.getDeathCounter().get());
        });
        handlers.put("game_stats_headshots", player -> {
            GamePlayer gp = plugin.arenaManager.getGamePlayer(player);
            return gp == null ? null : Integer.toString(gp.getHeadshotCounter().get());
        });
        handlers.put("game_stats_assists", player -> {
            GamePlayer gp = plugin.arenaManager.getGamePlayer(player);
            return gp == null ? null : Integer.toString(gp.getAssistCounter().get());
        });
        handlers.put("game_stats_kills", player -> {
            GamePlayer gp = plugin.arenaManager.getGamePlayer(player);
            return gp == null ? null : Integer.toString(gp.getKillCounter().get());
        });
        handlers.put("game_stats_deaths", player -> {
            GamePlayer gp = plugin.arenaManager.getGamePlayer(player);
            return gp == null ? null : Integer.toString(gp.getDeathCounter().get());
        });
        handlers.put("game_total_players", player -> {
            LocalGame game = plugin.arenaManager.getGame(player);
            return game == null ? null : Integer.toString(game.getPlayerCount());
        });
        handlers.put("game_current_time", player -> {
            LocalGame game = plugin.arenaManager.getGame(player);
            return game == null ? null : Long.toString(game.getCurrentTime().get());
        });
        handlers.put("game_current_time_formatted",  player -> {
            LocalGame game = plugin.arenaManager.getGame(player);
            return game == null ? null : plugin.formatShortFormTime(game.getCurrentTime().get()*50);
        });
        handlers.put("game_remaining_time", player -> {
            LocalGame game = plugin.arenaManager.getGame(player);
            return game == null ? null : Long.toString(game.getArena().getMaxTime() - game.getCurrentTime().get());
        });
        handlers.put("game_remaining_time_formatted", player -> {
            LocalGame game = plugin.arenaManager.getGame(player);
            return game == null ? null : plugin.formatShortFormTime((game.getArena().getMaxTime() - game.getCurrentTime().get())*50);
        });
        handlers.put("arena_id", player -> {
            LocalGame game = plugin.arenaManager.getGame(player);
            return game == null ? null : game.getArena().getId();
        });
        handlers.put("arena_name", player -> {
            LocalGame game = plugin.arenaManager.getGame(player);
            return game == null ? null : game.getArena().getName();
        });
        handlers.put("arena_max_players", player -> {
            LocalGame game = plugin.arenaManager.getGame(player);
            return game == null ? null : Integer.toString(game.getArena().getMaxPlayers());
        });
        handlers.put("arena_max_time", player -> {
            LocalGame game = plugin.arenaManager.getGame(player);
            return game == null ? null : Long.toString(game.getArena().getMaxTime());
        });
        handlers.put("arena_max_time_formatted", player -> {
            LocalGame game = plugin.arenaManager.getGame(player);
            return game == null ? null : plugin.formatShortFormTime(game.getArena().getMaxTime()*50);
        });
        handlers.put("mode_name", player -> {
            LocalGame game = plugin.arenaManager.getGame(player);
            return game == null ? null : game.getArena().getMode().getName();
        });
        handlers.put("mode_description", player -> {
            LocalGame game = plugin.arenaManager.getGame(player);
            return game == null ? null : game.getArena().getMode().getDescription();
        });
        handlers.put("ig_eco_currency", player -> plugin.GENERAL_CONF.getIgEcoCurrencyName());
        handlers.put("ig_eco_balance", player -> {
            GamePlayer gp = plugin.arenaManager.getGamePlayer(player);
            return gp == null ? null : String.format(plugin.GENERAL_CONF.getIgEcoCurrencyFormat(), gp.getIgBalance().get());
        });
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return "battle";
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
        if(player == null) return null;
        Function<Player, String> x = handlers.get(identifier);
        return x == null ? null : x.apply(player);
    }
}
