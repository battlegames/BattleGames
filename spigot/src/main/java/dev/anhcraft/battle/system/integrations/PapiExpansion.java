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
import dev.anhcraft.battle.api.Icon;
import dev.anhcraft.battle.api.arena.game.GamePlayer;
import dev.anhcraft.battle.api.arena.game.LocalGame;
import dev.anhcraft.battle.api.stats.natives.*;
import dev.anhcraft.battle.api.storage.data.PlayerData;
import dev.anhcraft.jvmkit.utils.MathUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PapiExpansion extends PlaceholderExpansion {
    public final Map<String, Callback> handlers = new HashMap<>();
    public final List<Filter> filters = new ArrayList<>();
    private final BattlePlugin plugin;
    public PapiExpansion(BattlePlugin plugin) {
        this.plugin = plugin;
        for (Icon icon : Icon.values())
            handlers.put("icon_" + icon.name().toLowerCase(), (player, pd, game, gp) -> icon.getChar());

        handlers.put("exp", (player, pd, game, gp) -> pd == null ? null : Long.toString(pd.getStats().of(ExpStat.class).get()));
        handlers.put("level", (player, pd, game, gp) -> pd == null ? null : Integer.toString(plugin.calculateLevel(pd.getStats().of(ExpStat.class).get())));
        handlers.put("level_progress", (player, pd, game, gp) -> {
            if (pd != null) {
                long midExp = pd.getStats().of(ExpStat.class).get();
                int lv = plugin.calculateLevel(midExp);
                long startExp = plugin.calculateExp(lv);
                long endExp = plugin.calculateExp(lv + 1);
                long delta1 = endExp - startExp;
                long delta2 = midExp - startExp;
                return MathUtil.formatRound(100d / delta1 * delta2);
            } else return null;
        });
        handlers.put("stats_win_matches", (player, pd, game, gp) -> pd == null ? null : Integer.toString(pd.getStats().of(WinStat.class).get()));
        handlers.put("stats_lose_matches", (player, pd, game, gp) -> pd == null ? null : Integer.toString(pd.getStats().of(LoseStat.class).get()));
        handlers.put("stats_total_matches", (player, pd, game, gp) -> pd == null ? null : Integer.toString(
                pd.getStats().of(WinStat.class).get() +
                        pd.getStats().of(LoseStat.class).get()
        ));
        handlers.put("stats_headshots", (player, pd, game, gp) -> pd == null ? null : Integer.toString(pd.getStats().of(HeadshotStat.class).get()));
        handlers.put("stats_assists", (player, pd, game, gp) -> pd == null ? null : Integer.toString(pd.getStats().of(AssistStat.class).get()));
        handlers.put("stats_first_kills", (player, pd, game, gp) -> pd == null ? null : Integer.toString(pd.getStats().of(FirstKillStat.class).get()));
        handlers.put("stats_kills", (player, pd, game, gp) -> pd == null ? null : Integer.toString(pd.getStats().of(KillStat.class).get()));
        handlers.put("stats_deaths", (player, pd, game, gp) -> pd == null ? null : Integer.toString(pd.getStats().of(DeathStat.class).get()));
        handlers.put("stats_respawns", (player, pd, game, gp) -> pd == null ? null : Integer.toString(pd.getStats().of(RespawnStat.class).get()));
        handlers.put("stats_stolen_mobs", (player, pd, game, gp) -> pd == null ? null : Integer.toString(pd.getStats().of(StolenMobStat.class).get()));
        handlers.put("game_stats_headshots", (player, pd, game, gp) -> gp == null ? null : Integer.toString(gp.getStats().of(HeadshotStat.class).get()));
        handlers.put("game_stats_assists", (player, pd, game, gp) -> gp == null ? null : Integer.toString(gp.getStats().of(AssistStat.class).get()));
        handlers.put("game_stats_kills", (player, pd, game, gp) -> gp == null ? null : Integer.toString(gp.getStats().of(KillStat.class).get()));
        handlers.put("game_stats_deaths", (player, pd, game, gp) -> gp == null ? null : Integer.toString(gp.getStats().of(DeathStat.class).get()));
        handlers.put("game_stats_respawns", (player, pd, game, gp) -> gp == null ? null : Integer.toString(gp.getStats().of(RespawnStat.class).get()));
        handlers.put("game_stats_stolen_mobs", (player, pd, game, gp) -> gp == null ? null : Integer.toString(gp.getStats().of(StolenMobStat.class).get()));
        handlers.put("game_total_players", (player, pd, game, gp) -> game == null ? null : Integer.toString(game.getPlayerCount()));
        handlers.put("game_current_time", (player, pd, game, gp) -> game == null ? null : Long.toString(game.getCurrentTime().get()));
        handlers.put("game_current_time_formatted", (player, pd, game, gp) -> game == null ? null : plugin.formatShortFormTime(game.getCurrentTime().get() * 50));
        handlers.put("game_remaining_time", (player, pd, game, gp) -> game == null ? null : Long.toString(game.getArena().getMaxTime() - game.getCurrentTime().get()));
        handlers.put("game_remaining_time_formatted", (player, pd, game, gp) -> game == null ? null : plugin.formatShortFormTime((game.getArena().getMaxTime() - game.getCurrentTime().get()) * 50));
        handlers.put("arena_id", (player, pd, game, gp) -> game == null ? null : game.getArena().getId());
        handlers.put("arena_name", (player, pd, game, gp) -> game == null ? null : game.getArena().getName());
        handlers.put("arena_max_players", (player, pd, game, gp) -> game == null ? null : Integer.toString(game.getArena().getMaxPlayers()));
        handlers.put("arena_max_time", (player, pd, game, gp) -> game == null ? null : Long.toString(game.getArena().getMaxTime()));
        handlers.put("arena_max_time_formatted", (player, pd, game, gp) -> game == null ? null : plugin.formatShortFormTime(game.getArena().getMaxTime() * 50));
        handlers.put("mode_name", (player, pd, game, gp) -> game == null ? null : game.getArena().getMode().getName());
        handlers.put("ig_eco_currency", (player, pd, game, gp) -> plugin.generalConf.getIgEcoCurrencyName());
        handlers.put("ig_eco_balance", (player, pd, game, gp) -> gp == null ? null : String.format(plugin.generalConf.getIgEcoCurrencyFormat(), gp.getIgBalance().get()));
        filters.add(new PapiExpansion.Filter() {
            @Override
            public boolean check(String str) {
                return str.startsWith("mc_locale_");
            }

            @Override
            public String handle(String str, Player player, PlayerData pd, LocalGame game, GamePlayer gp) {
                return plugin.getMinecraftLocale().get(str.substring("mc_locale_".length())).getAsString();
            }
        });
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "battle";
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @NotNull
    public List<String> getPlaceholders() {
        return handlers.keySet().stream().map(s -> "%battle_" + s + "%").collect(Collectors.toList());
    }

    @Override
    public String onRequest(@Nullable OfflinePlayer offlinePlayer, @NotNull String identifier) {
        if (offlinePlayer == null) return null;
        Player player = offlinePlayer.getPlayer();
        if (player == null) return null;
        Callback x = handlers.get(identifier);
        if (x != null) {
            PlayerData pd = plugin.getPlayerData(player);
            LocalGame game = plugin.getArenaManager().getGame(player);
            GamePlayer gp = null;
            if (game != null) {
                gp = game.getPlayers().get(player);
            }
            return x.handle(player, pd, game, gp);
        }
        for (Filter f : filters) {
            if (f.check(identifier)) {
                PlayerData pd = plugin.getPlayerData(player);
                LocalGame game = plugin.getArenaManager().getGame(player);
                GamePlayer gp = null;
                if (game != null) {
                    gp = game.getPlayers().get(player);
                }
                return f.handle(identifier, player, pd, game, gp);
            }
        }
        return null;
    }

    public interface Callback {
        String handle(Player player, PlayerData pd, LocalGame game, GamePlayer gp);
    }

    public interface Filter {
        boolean check(String str);

        String handle(String str, Player player, PlayerData pd, LocalGame game, GamePlayer gp);
    }
}
