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
package dev.anhcraft.abm.system.controllers;

import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.events.GamePlayerDamageEvent;
import dev.anhcraft.abm.api.game.*;
import dev.anhcraft.abm.system.renderers.scoreboard.PlayerScoreboard;
import dev.anhcraft.abm.utils.CooldownMap;
import dev.anhcraft.abm.utils.LocationUtil;
import dev.anhcraft.jvmkit.utils.RandomUtil;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.*;

public class TeamDeathmatchController extends DeathmatchController {
    protected final Map<Game, SimpleTeam<ABTeam>> TEAM = new HashMap<>();

    public TeamDeathmatchController(BattlePlugin plugin) {
        this(plugin, Mode.TEAM_DEATHMATCH);
    }

    TeamDeathmatchController(BattlePlugin plugin, Mode mode) {
        super(plugin, mode);

        String p = mode.getId()+"_";

        plugin.getPapiExpansion().handlers.put(p+"team", player -> {
            return plugin.gameManager.getGame(player).map(game -> {
                SimpleTeam<ABTeam> t = TEAM.get(game);
                if(t == null)
                    return null;
                ABTeam dt = t.getTeam(player);
                if(dt == null)
                    return null;
                return dt.getLocalizedName();
            }).orElse(null);
        });

        plugin.getPapiExpansion().handlers.put(p+"team_players", player -> {
            return plugin.gameManager.getGame(player).map(game -> {
                SimpleTeam<ABTeam> t = TEAM.get(game);
                if(t == null)
                    return null;
                ABTeam dt = t.getTeam(player);
                if(dt == null)
                    return null;
                return Integer.toString(t.countPlayers(dt));
            }).orElse(null);
        });
    }

    @Override
    public void onTask(Game game){
        SimpleTeam<ABTeam> x = TEAM.get(game);
        if(x != null) {
            int a = x.countPlayers(ABTeam.TEAM_A);
            int b = x.countPlayers(ABTeam.TEAM_B);
            if (a == 0 || b == 0) game.end();
        }
    }

    private ABTeam findTeam(Game game) {
        SimpleTeam<ABTeam> x = TEAM.get(game);
        int a = x.countPlayers(ABTeam.TEAM_A);
        int b = x.countPlayers(ABTeam.TEAM_B);
        return a <= b ? ABTeam.TEAM_A : ABTeam.TEAM_B;
    }

    @Override
    public void onJoin(Player player, Game game) {
        broadcast(game, "player_join_broadcast", s -> s.replace("{__target__}", player.getDisplayName()));
        int m = Math.min(game.getArena().getAttributes().getInt("min_players"), 1);
        switch (game.getPhase()){
            case WAITING:{
                respw(game, player, null);
                if(game.getMode().isWaitingScoreboardEnabled()) {
                    String title = game.getMode().getWaitingScoreboardTitle();
                    List<String> content = game.getMode().getWaitingScoreboardContent();
                    boolean b = game.getMode().isWaitingScoreboardFixedLength();
                    plugin.scoreboardRenderer.setScoreboard(new PlayerScoreboard(player, title, content, b));
                }
                if(m <= game.countPlayers()) countdown(game);
                break;
            }
            case PLAYING: {
                ABTeam t = findTeam(game);
                TEAM.get(game).addPlayer(player, t);
                addPlayer(game, player, t);
            }
        }
    }

    protected void play(Game game) {
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
                addPlayer(game, p, ABTeam.TEAM_A);
            });
            tb.forEach(p -> {
                cancelTask(game, "respawn::"+p.getName());
                addPlayer(game, p, ABTeam.TEAM_B);
            });
        });
    }

    private void addPlayer(Game game, Player player, ABTeam dt) {
        if(game.getMode().isPlayingScoreboardEnabled()) {
            String title = game.getMode().getPlayingScoreboardTitle();
            List<String> content = game.getMode().getPlayingScoreboardContent();
            boolean b = game.getMode().isPlayingScoreboardFixedLength();
            plugin.scoreboardRenderer.setScoreboard(new PlayerScoreboard(player, title, content, b));
        }
        // TODO ADD SPERATE SCOREBOARD TO HIDE NAMETAG HERE
        respw(game, player, dt);
    }

    @Override
    protected void respw(Game game, Player player) {
        respw(game, player, TEAM.get(game).getTeam(player));
    }

    private void respw(Game game, Player player, ABTeam team) {
        player.setGameMode(GameMode.SURVIVAL);
        switch (game.getPhase()) {
            case END:
            case WAITING: {
                String loc = RandomUtil.pickRandom(game.getArena().getAttributes().getStringList("waiting_spawn_points"));
                player.teleport(LocationUtil.fromString(loc));
                break;
            }
            case PLAYING: {
                String loc = RandomUtil.pickRandom(game.getArena().getAttributes().getStringList("playing_spawn_points_"+ (team == ABTeam.TEAM_A ? "a" : "b")));
                player.teleport(LocationUtil.fromString(loc));
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
    public void damage(GamePlayerDamageEvent e) {
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
    public void onEnd(Game game) {
        cancelAllTasks(game);
        SimpleTeam<ABTeam> team = TEAM.remove(game);

        Map<ABTeam, List<GamePlayer>> map = team.reverse(game::getPlayer);
        List<GamePlayer> aPlayers = map.get(ABTeam.TEAM_A);
        List<GamePlayer> bPlayers = map.get(ABTeam.TEAM_B);
        if(aPlayers != null & bPlayers != null) {
            IntSummaryStatistics sa = aPlayers.stream().mapToInt(value -> {
                respw(game, value.getPlayer(), ABTeam.TEAM_A);
                value.setSpectator(false);
                return value.getKillCounter().get();
            }).summaryStatistics();
            IntSummaryStatistics sb = bPlayers.stream().mapToInt(value -> {
                respw(game, value.getPlayer(), ABTeam.TEAM_B);
                value.setSpectator(false);
                return value.getKillCounter().get();
            }).summaryStatistics();
            ABTeam winner;
            if (sa.getSum() == sb.getSum())
                winner = sa.getAverage() > sb.getAverage() ? ABTeam.TEAM_A : ABTeam.TEAM_B;
            else
                winner = sa.getSum() > sb.getSum() ? ABTeam.TEAM_A : ABTeam.TEAM_B;
            map.get(winner).forEach(player -> player.setWinner(true));
            team.reset();
        }

        plugin.gameManager.rewardAndSaveCache(game);
        plugin.gameManager.destroy(game);
    }
}
