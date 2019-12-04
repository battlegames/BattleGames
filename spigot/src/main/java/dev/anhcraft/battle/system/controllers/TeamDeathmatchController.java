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

import dev.anhcraft.battle.BattlePlugin;
import dev.anhcraft.battle.api.events.PlayerWeaponDamageEvent;
import dev.anhcraft.battle.api.game.*;
import dev.anhcraft.battle.system.renderers.scoreboard.PlayerScoreboard;
import dev.anhcraft.battle.utils.CooldownMap;
import dev.anhcraft.battle.utils.EntityUtil;
import dev.anhcraft.battle.utils.LocationUtil;
import dev.anhcraft.jvmkit.utils.RandomUtil;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TeamDeathmatchController extends DeathmatchController {
    protected final Map<LocalGame, SimpleTeam<ABTeam>> TEAM = new HashMap<>();

    public TeamDeathmatchController(BattlePlugin plugin) {
        this(plugin, Mode.TEAM_DEATHMATCH);
    }

    TeamDeathmatchController(BattlePlugin plugin, Mode mode) {
        super(plugin, mode);

        String p = mode.getId()+"_";

        plugin.getPapiExpansion().handlers.put(p+"team", player -> {
            LocalGame game = plugin.gameManager.getGame(player);
            if(game == null) return null;
            SimpleTeam<ABTeam> t = TEAM.get(game);
            if(t == null)
                return null;
            ABTeam dt = t.getTeam(player);
            if(dt == null)
                return null;
            return dt.getLocalizedName();
        });

        plugin.getPapiExpansion().handlers.put(p+"team_players", player -> {
            LocalGame game = plugin.gameManager.getGame(player);
            if(game == null) return null;
            SimpleTeam<ABTeam> t = TEAM.get(game);
            if(t == null)
                return null;
            ABTeam dt = t.getTeam(player);
            if(dt == null)
                return null;
            return Integer.toString(t.countPlayers(dt));
        });
    }

    @Override
    public void onTick(LocalGame game){
        SimpleTeam<ABTeam> x = TEAM.get(game);
        if(x != null) {
            int a = x.countPlayers(ABTeam.TEAM_A);
            int b = x.countPlayers(ABTeam.TEAM_B);
            if (a == 0 || b == 0) game.end();
        }
    }

    private ABTeam nextTeam(LocalGame game) {
        SimpleTeam<ABTeam> x = TEAM.get(game);
        int a = x.countPlayers(ABTeam.TEAM_A);
        int b = x.countPlayers(ABTeam.TEAM_B);
        return a <= b ? ABTeam.TEAM_A : ABTeam.TEAM_B;
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
                ABTeam t = nextTeam(game);
                SimpleTeam<ABTeam> tm = TEAM.get(game);
                tm.addPlayer(player, t);

                List<Player> ta = tm.getPlayers(ABTeam.TEAM_A);
                List<Player> tb = tm.getPlayers(ABTeam.TEAM_B);
                PlayerScoreboard sps = addPlayer(game, player, t);
                if (sps != null) {
                    sps.addTeamPlayers(ABTeam.TEAM_A.name(), ta);
                    sps.addTeamPlayers(ABTeam.TEAM_B.name(), tb);
                }

                List<Player> lc = Collections.singletonList(player);

                for(Player p : ta) {
                    if(p.equals(player)) continue;
                    PlayerScoreboard ps = plugin.scoreboardRenderer.getScoreboard(p);
                    if (ps != null) {
                        ps.addTeamPlayers(t.name(), lc);
                    }
                }

                for(Player p : tb) {
                    if(p.equals(player)) continue;
                    PlayerScoreboard ps = plugin.scoreboardRenderer.getScoreboard(p);
                    if (ps != null) {
                        ps.addTeamPlayers(t.name(), lc);
                    }
                }
            }
        }
    }

    @Override
    public void onQuit(Player player, LocalGame game){
        super.onQuit(player, game);
        SimpleTeam<ABTeam> team = TEAM.get(game);
        if(team != null) {
            ABTeam abTeam = team.removePlayer(player);
            for(Map.Entry<Player, ABTeam> ent : team.getPlayerTeam()) {
                PlayerScoreboard ps = plugin.scoreboardRenderer.getScoreboard(ent.getKey());
                if (ps != null) {
                    ps.removeTeamPlayer(abTeam.name(), player);
                }
            }
        }
    }

    @Override
    protected void play(LocalGame game) {
        broadcast(game,"game_start_broadcast");

        List<Player> x = new ArrayList<>(game.getPlayers().keySet());
        int sz = Math.floorDiv(x.size(), 2);
        List<Player> ta = x.subList(0, sz);
        List<Player> tb = x.subList(sz, x.size());

        SimpleTeam<ABTeam> team = new SimpleTeam<>();
        team.addPlayers(ta, ABTeam.TEAM_A);
        team.addPlayers(tb, ABTeam.TEAM_B);
        TEAM.put(game, team);

        plugin.taskHelper.newTask(() -> {
            game.setPhase(GamePhase.PLAYING);
            ta.forEach(p -> {
                cancelTask(game, "respawn::"+p.getName());
                PlayerScoreboard ps = addPlayer(game, p, ABTeam.TEAM_A);
                if (ps != null) {
                    ps.addTeamPlayers(ABTeam.TEAM_A.name(), ta);
                    ps.addTeamPlayers(ABTeam.TEAM_B.name(), tb);
                }
            });
            tb.forEach(p -> {
                cancelTask(game, "respawn::"+p.getName());
                PlayerScoreboard ps = addPlayer(game, p, ABTeam.TEAM_B);
                if (ps != null) {
                    ps.addTeamPlayers(ABTeam.TEAM_A.name(), ta);
                    ps.addTeamPlayers(ABTeam.TEAM_B.name(), tb);
                }
            });
        });
    }

    @Nullable
    private PlayerScoreboard addPlayer(LocalGame game, Player player, ABTeam dt) {
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
    protected void respw(LocalGame game, Player player) {
        SimpleTeam<ABTeam> t = TEAM.get(game);
        respw(game, player, t == null ? null : t.getTeam(player));
    }

    private void respw(LocalGame game, Player player, ABTeam team) {
        player.setGameMode(GameMode.SURVIVAL);
        switch (game.getPhase()) {
            case END:
            case WAITING: {
                String loc = RandomUtil.pickRandom(game.getArena().getAttributes().getStringList("waiting_spawn_points"));
                EntityUtil.teleport(player, LocationUtil.fromString(loc));
                break;
            }
            case PLAYING: {
                String loc = RandomUtil.pickRandom(game.getArena().getAttributes().getStringList("playing_spawn_points_"+ (team == ABTeam.TEAM_A ? "a" : "b")));
                EntityUtil.teleport(player, LocationUtil.fromString(loc));
                performCooldownMap(game, "spawn_protection",
                        cooldownMap -> cooldownMap.resetTime(player),
                        () -> new CooldownMap(player));
                performCooldownMap(game, "item_selection",
                        cooldownMap -> cooldownMap.resetTime(player),
                        () -> new CooldownMap(player));
            }
        }
    }

    @EventHandler
    public void damage(PlayerWeaponDamageEvent e) {
        if(e.getGame().getMode() != getMode()) return;
        SimpleTeam<ABTeam> x = TEAM.get(e.getGame());
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
        SimpleTeam<ABTeam> team = TEAM.remove(game);

        Map<ABTeam, List<GamePlayer>> map = team.reverse(game::getPlayer);
        List<GamePlayer> aPlayers = map.get(ABTeam.TEAM_A);
        List<GamePlayer> bPlayers = map.get(ABTeam.TEAM_B);
        if(aPlayers != null & bPlayers != null) {
            IntSummaryStatistics sa = aPlayers.stream().mapToInt(value -> {
                respw(game, value.toBukkit(), ABTeam.TEAM_A);
                value.setSpectator(false);
                return value.getKillCounter().get();
            }).summaryStatistics();
            IntSummaryStatistics sb = bPlayers.stream().mapToInt(value -> {
                respw(game, value.toBukkit(), ABTeam.TEAM_B);
                value.setSpectator(false);
                return value.getKillCounter().get();
            }).summaryStatistics();
            ABTeam winner = handleResult(game, sa, sb, aPlayers, bPlayers);
            map.get(winner).forEach(player -> player.setWinner(true));
            team.reset();
        }

        plugin.gameManager.handleEnd(game);
    }

    protected ABTeam handleResult(LocalGame game, IntSummaryStatistics sa, IntSummaryStatistics sb, List<GamePlayer> aPlayers, List<GamePlayer> bPlayers) {
        if (sa.getSum() == sb.getSum())
            return sa.getAverage() > sb.getAverage() ? ABTeam.TEAM_A : ABTeam.TEAM_B;
        else
            return sa.getSum() > sb.getSum() ? ABTeam.TEAM_A : ABTeam.TEAM_B;
    }
}
