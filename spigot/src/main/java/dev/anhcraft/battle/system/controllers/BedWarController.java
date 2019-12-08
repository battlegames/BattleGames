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
package dev.anhcraft.battle.system.controllers;

import com.google.common.collect.Multimap;
import dev.anhcraft.battle.BattlePlugin;
import dev.anhcraft.battle.api.events.ItemChooseEvent;
import dev.anhcraft.battle.api.events.game.GamePlayerWeaponEvent;
import dev.anhcraft.battle.api.game.*;
import dev.anhcraft.battle.system.renderers.scoreboard.PlayerScoreboard;
import dev.anhcraft.battle.utils.*;
import dev.anhcraft.jvmkit.utils.RandomUtil;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BedWarController extends DeathmatchController {
    protected final Map<LocalGame, TeamManager<BWTeam>> TEAM = new HashMap<>();
    protected final Map<BlockPosition, BWTeam> BEDS = new HashMap<>();

    public BedWarController(BattlePlugin plugin) {
        this(plugin, Mode.BEDWAR);
    }

    BedWarController(BattlePlugin plugin, Mode mode) {
        super(plugin, mode);

        String p = mode.getId()+"_";

        plugin.getPapiExpansion().handlers.put(p+"team", player -> {
            LocalGame game = plugin.gameManager.getGame(player);
            if(game == null) return null;
            TeamManager<BWTeam> t = TEAM.get(game);
            if(t == null) return null;
            BWTeam bwTeam = t.getTeam(player);
            if(bwTeam == null) return null;
            return bwTeam.getLocalizedName();
        });

        plugin.getPapiExpansion().handlers.put(p+"team_players", player -> {
            LocalGame game = plugin.gameManager.getGame(player);
            if(game == null) return null;
            TeamManager<BWTeam> t = TEAM.get(game);
            if(t == null) return null;
            BWTeam bwTeam = t.getTeam(player);
            if(bwTeam == null) return null;
            return Integer.toString(t.countPlayers(bwTeam));
        });

        plugin.getPapiExpansion().handlers.put(p+"max_team_players", player -> {
            LocalGame game = plugin.gameManager.getGame(player);
            if(game == null) return null;
            return game.getArena().getAttributes().getString("team_size");
        });

        plugin.getPapiExpansion().handlers.put(p+"bed_status", player -> {
            LocalGame game = plugin.gameManager.getGame(player);
            if(game == null) return null;
            TeamManager<BWTeam> t = TEAM.get(game);
            if(t == null) return null;
            BWTeam bwTeam = t.getTeam(player);
            if(bwTeam == null) return null;
            return plugin.chatManager.getFormattedMessages(
                    blp(bwTeam.isBedPresent() ? "bed_status.present" : "bed_status.destroyed")
            ).get(0);
        });
    }

    @Override
    public void onTick(LocalGame game){
        TeamManager<BWTeam> x = TEAM.get(game);
        if(x != null && game.getCurrentTime().get() > 100 && x.countPresentTeams() <= 1) {
            game.end();
        }
    }

    @Override
    public void onJoin(Player player, LocalGame game) {
        broadcast(game, "player_join_broadcast", s -> s.replace("{__target__}", player.getDisplayName()));
        int m = Math.max(game.getArena().getAttributes().getInt("min_players"), 1);
        switch (game.getPhase()){
            case WAITING:{
                respw(game, player, null);
                if(game.getMode().isWaitingScoreboardEnabled()) {
                    String title = game.getMode().getWaitingScoreboardTitle();
                    List<String> content = game.getMode().getWaitingScoreboardContent();
                    int len = game.getMode().isWaitingScoreboardFixedLength();
                    plugin.scoreboardRenderer.setScoreboard(new PlayerScoreboard(player, title, content, len));
                }
                if(m <= game.getPlayerCount()) countdown(game);
                break;
            }
            case PLAYING: {
                TeamManager<BWTeam> tm = TEAM.get(game);
                Optional<BWTeam> to = tm.nextAvailableTeam(game.getArena().getAttributes().getInt("team_size"));
                if(!to.isPresent()){
                    plugin.gameManager.quit(player);
                    break;
                }
                List<Player> lc = Collections.singletonList(player);
                BWTeam t = to.get();
                tm.addPlayer(player, t);

                Multimap<BWTeam, Player> f = tm.toMultimap();
                PlayerScoreboard sps = addPlayer(game, player, t);
                if (sps != null) {
                    for (BWTeam bt : f.keys()) {
                        Collection<Player> players = f.get(bt);
                        sps.addTeamPlayers(bt.getLocalizedName(), players);
                        for (Player p : players) {
                            if(p.equals(player)) continue;
                            PlayerScoreboard ps = plugin.scoreboardRenderer.getScoreboard(p);
                            if (ps != null) {
                                ps.addTeamPlayers(t.getLocalizedName(), lc);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onQuit(Player player, LocalGame game){
        super.onQuit(player, game);
        TeamManager<BWTeam> team = TEAM.get(game);
        if(team != null) {
            BWTeam bwTeam = team.removePlayer(player);
            if(bwTeam != null) {
                for (Map.Entry<Player, BWTeam> ent : team.getPlayerTeam()) {
                    PlayerScoreboard ps = plugin.scoreboardRenderer.getScoreboard(ent.getKey());
                    if (ps != null) {
                        ps.removeTeamPlayer(bwTeam.getLocalizedName(), player);
                    }
                }
            }
        }
    }

    @Override
    protected void play(LocalGame game) {
        broadcast(game,"game_start_broadcast");

        TeamManager<BWTeam> tm = new TeamManager<>();
        TEAM.put(game, tm);
        ConfigurationSection teamConf = game.getArena().getAttributes().getConfigurationSection("teams");
        Set<String> tk = teamConf.getKeys(false);
        BWTeam[] bwt = new BWTeam[tk.size()];
        int i = 0;
        for(String team : tk){
            BWTeam bt = new BWTeam(teamConf.getConfigurationSection(team));
            bwt[i++] = bt;
            tm.initTeam(bt);
            BEDS.put(BlockPosition.of(bt.getBedPart1()), bt);
            BEDS.put(BlockPosition.of(bt.getBedPart2()), bt);
        }

        List<Player> players = new ArrayList<>(game.getPlayers().keySet());
        int teammates = 1;
        int maxTeammates = game.getArena().getAttributes().getInt("team_size");
        int teamIndex = 0;
        for(Player p : players){
            if(teamIndex == bwt.length){
                plugin.gameManager.quit(p);
                continue;
            }
            tm.addPlayer(p, bwt[teamIndex]);
            if(teammates == maxTeammates) teamIndex++;
            else teammates++;
        }

        plugin.taskHelper.newTask(() -> {
            game.setPhase(GamePhase.PLAYING);
            Multimap<BWTeam, Player> f = tm.toMultimap();
            f.forEach((bwTeam, player) -> {
                cancelTask(game, "respawn::"+player.getName());
                PlayerScoreboard ps = addPlayer(game, player, bwTeam);
                if (ps != null) {
                    for(BWTeam bt : f.keys()){
                        ps.addTeamPlayers(bt.getLocalizedName(), f.get(bt));
                    }
                }
            });
        });
    }

    @Nullable
    private PlayerScoreboard addPlayer(LocalGame game, Player player, BWTeam dt) {
        PlayerScoreboard ps = null;
        if(game.getMode().isPlayingScoreboardEnabled()) {
            String title = game.getMode().getPlayingScoreboardTitle();
            List<String> content = game.getMode().getPlayingScoreboardContent();
            int len = game.getMode().isPlayingScoreboardFixedLength();
            ps = new PlayerScoreboard(player, title, content, len);
            plugin.scoreboardRenderer.setScoreboard(ps);
        }
        respw(game, player, dt);
        return ps;
    }

    @Override
    public void onChooseItem(ItemChooseEvent event, LocalGame game){
        // prevent use battle items
    }

    @EventHandler
    public void onBreakBed(BlockBreakEvent event){
        Block b = event.getBlock();
        if(b.getType().name().equals("BED_BLOCK") || b.getType().name().endsWith("_BED")){
            LocalGame game = plugin.gameManager.getGame(event.getPlayer());
            if(game != null && game.getMode() == getMode()){
                TeamManager<BWTeam> tm = TEAM.get(game);
                if(tm == null) return;
                BWTeam pteam = tm.getTeam(event.getPlayer());
                if(pteam == null) return;
                BWTeam targetTeam = BEDS.get(BlockPosition.of(b));
                if(pteam.equals(targetTeam)){
                    event.setCancelled(true);
                    event.setDropItems(false);
                    event.setExpToDrop(0);
                } else {
                    targetTeam.getBedPart1().setType(Material.AIR);
                    targetTeam.getBedPart2().setType(Material.AIR);
                    broadcast(game, "bed_destroy_broadcast", s -> String.format(s, event.getPlayer().getName(), targetTeam.getLocalizedName()));
                    for (Player p : tm.getPlayers(targetTeam)){
                        plugin.chatManager.sendPlayer(p, blp("respawn_unable"));
                    }
                }
            }
        }
    }

    @Override
    public boolean shouldAcceptRespawn(PlayerRespawnEvent event, LocalGame game, GamePlayer gp){
        TeamManager<BWTeam> tm = TEAM.get(game);
        BWTeam bt = Objects.requireNonNull(tm.getTeam(event.getPlayer()));
        boolean b = bt.isBedPresent();
        if(!b){
            tm.removePlayer(event.getPlayer());
            plugin.scoreboardRenderer.removeScoreboard(event.getPlayer());
            event.setRespawnLocation(Objects.requireNonNull(RandomUtil.pickRandom(bt.getSpawnPoints())));
        }
        return b;
    }

    @Override
    protected void respw(LocalGame game, Player player) {
        TeamManager<BWTeam> t = TEAM.get(game);
        respw(game, player, t == null ? null : t.getTeam(player));
    }

    private void respw(LocalGame game, Player player, BWTeam team) {
        player.setGameMode(GameMode.SURVIVAL);
        switch (game.getPhase()) {
            case END:
            case WAITING: {
                String loc = RandomUtil.pickRandom(game.getArena().getAttributes().getStringList("waiting_spawn_points"));
                EntityUtil.teleport(player, LocationUtil.fromString(loc));
                break;
            }
            case PLAYING: {
                EntityUtil.teleport(player, Objects.requireNonNull(RandomUtil.pickRandom(team.getSpawnPoints())));
                performCooldownMap(game, "spawn_protection",
                        cooldownMap -> cooldownMap.resetTime(player),
                        () -> new CooldownMap(player));
            }
        }
    }

    @EventHandler
    public void damage(GamePlayerWeaponEvent e) {
        if(e.getGame().getMode() != getMode()) return;
        TeamManager<BWTeam> x = TEAM.get(e.getGame());
        if(x.getTeam(e.getDamager()) == x.getTeam(e.getPlayer())) e.setCancelled(true);
        else performCooldownMap(e.getGame(), "spawn_protection",
                cooldownMap -> {
                    int t = e.getGame().getArena().getAttributes().getInt("spawn_protection_time");
                    if(!cooldownMap.isPassed(e.getPlayer(), t)) e.setCancelled(true);
                });
    }

    @Override
    public void onEnd(LocalGame game) {
        cancelAllTasks(game);
        TeamManager<BWTeam> tm = TEAM.remove(game);

        Multimap<BWTeam, GamePlayer> f = tm.toMultimap(game::getPlayer);
        double avgKill = 0;
        long sumKill = 0;
        BWTeam winTeam = null;
        for(BWTeam bt : f.keys()){
            IntSummaryStatistics ss = f.get(bt).stream().mapToInt(p -> {
                respw(game, p.toBukkit(), bt);
                p.setSpectator(false);
                return p.getKillCounter().get();
            }).summaryStatistics();
            if(ss.getSum() > sumKill || (ss.getSum() == sumKill && ss.getAverage() > avgKill)){
                avgKill = ss.getAverage();
                sumKill = ss.getSum();
                winTeam = bt;
            }
        }
        if(winTeam == null) {
            winTeam = f.keys().iterator().next();
        }
        f.get(winTeam).forEach(player -> player.setWinner(true));
        tm.reset();

        plugin.gameManager.handleEnd(game);
    }
}
