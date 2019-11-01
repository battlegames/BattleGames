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
import dev.anhcraft.battle.api.events.GamePlayerDamageEvent;
import dev.anhcraft.battle.api.game.*;
import dev.anhcraft.battle.system.renderers.scoreboard.PlayerScoreboard;
import dev.anhcraft.battle.utils.CooldownMap;
import dev.anhcraft.battle.utils.EntityUtil;
import dev.anhcraft.battle.utils.LocationUtil;
import dev.anhcraft.jvmkit.utils.RandomUtil;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

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
    public void onTick(LocalGame localGame){
        SimpleTeam<ABTeam> x = TEAM.get(localGame);
        if(x != null) {
            int a = x.countPlayers(ABTeam.TEAM_A);
            int b = x.countPlayers(ABTeam.TEAM_B);
            if (a == 0 || b == 0) localGame.end();
        }
    }

    private ABTeam nextTeam(LocalGame localGame) {
        SimpleTeam<ABTeam> x = TEAM.get(localGame);
        int a = x.countPlayers(ABTeam.TEAM_A);
        int b = x.countPlayers(ABTeam.TEAM_B);
        return a <= b ? ABTeam.TEAM_A : ABTeam.TEAM_B;
    }

    @Override
    public void onJoin(Player player, LocalGame localGame) {
        broadcast(localGame, "player_join_broadcast", s -> s.replace("{__target__}", player.getDisplayName()));
        int m = Math.max(localGame.getArena().getAttributes().getInt("min_players"), 1);
        switch (localGame.getPhase()){
            case WAITING:{
                respw(localGame, player, null);
                if(localGame.getMode().isWaitingScoreboardEnabled()) {
                    String title = localGame.getMode().getWaitingScoreboardTitle();
                    List<String> content = localGame.getMode().getWaitingScoreboardContent();
                    int len = localGame.getMode().isWaitingScoreboardFixedLength();
                    plugin.scoreboardRenderer.setScoreboard(new PlayerScoreboard(player, title, content, len));
                }
                if(m <= localGame.getPlayerCount()) countdown(localGame);
                break;
            }
            case PLAYING: {
                ABTeam t = nextTeam(localGame);
                TEAM.get(localGame).addPlayer(player, t);
                addPlayer(localGame, player, t);
            }
        }
    }

    @Override
    public void onQuit(Player player, LocalGame localGame){
        super.onQuit(player, localGame);
        SimpleTeam<ABTeam> team = TEAM.get(localGame);
        if(team != null)
            team.removePlayer(player);
    }

    @Override
    protected void play(LocalGame localGame) {
        broadcast(localGame,"game_start_broadcast");

        List<Player> x = new ArrayList<>(localGame.getPlayers().keySet());
        int sz = Math.floorDiv(x.size(), 2);
        List<Player> ta = x.subList(0, sz);
        List<Player> tb = x.subList(sz, x.size());

        SimpleTeam<ABTeam> team = new SimpleTeam<>();
        team.addPlayers(ta, ABTeam.TEAM_A);
        team.addPlayers(tb, ABTeam.TEAM_B);
        TEAM.put(localGame, team);

        plugin.taskHelper.newTask(() -> {
            localGame.setPhase(GamePhase.PLAYING);
            ta.forEach(p -> {
                cancelTask(localGame, "respawn::"+p.getName());
                addPlayer(localGame, p, ABTeam.TEAM_A);
            });
            tb.forEach(p -> {
                cancelTask(localGame, "respawn::"+p.getName());
                addPlayer(localGame, p, ABTeam.TEAM_B);
            });
        });
    }

    private void addPlayer(LocalGame localGame, Player player, ABTeam dt) {
        if(localGame.getMode().isPlayingScoreboardEnabled()) {
            String title = localGame.getMode().getPlayingScoreboardTitle();
            List<String> content = localGame.getMode().getPlayingScoreboardContent();
            int len = localGame.getMode().isPlayingScoreboardFixedLength();
            plugin.scoreboardRenderer.setScoreboard(new PlayerScoreboard(player, title, content, len));
        }
        // TODO ADD SPERATE SCOREBOARD TO HIDE NAMETAG HERE
        respw(localGame, player, dt);
    }

    @Override
    protected void respw(LocalGame localGame, Player player) {
        SimpleTeam<ABTeam> t = TEAM.get(localGame);
        respw(localGame, player, t == null ? null : t.getTeam(player));
    }

    private void respw(LocalGame localGame, Player player, ABTeam team) {
        player.setGameMode(GameMode.SURVIVAL);
        switch (localGame.getPhase()) {
            case END:
            case WAITING: {
                String loc = RandomUtil.pickRandom(localGame.getArena().getAttributes().getStringList("waiting_spawn_points"));
                EntityUtil.teleport(player, LocationUtil.fromString(loc));
                break;
            }
            case PLAYING: {
                String loc = RandomUtil.pickRandom(localGame.getArena().getAttributes().getStringList("playing_spawn_points_"+ (team == ABTeam.TEAM_A ? "a" : "b")));
                EntityUtil.teleport(player, LocationUtil.fromString(loc));
                performCooldownMap(localGame, "spawn_protection",
                        cooldownMap -> cooldownMap.resetTime(player),
                        () -> new CooldownMap(player));
                performCooldownMap(localGame, "item_selection",
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
    public void onEnd(LocalGame localGame) {
        cancelAllTasks(localGame);
        SimpleTeam<ABTeam> team = TEAM.remove(localGame);

        Map<ABTeam, List<GamePlayer>> map = team.reverse(localGame::getPlayer);
        List<GamePlayer> aPlayers = map.get(ABTeam.TEAM_A);
        List<GamePlayer> bPlayers = map.get(ABTeam.TEAM_B);
        if(aPlayers != null & bPlayers != null) {
            IntSummaryStatistics sa = aPlayers.stream().mapToInt(value -> {
                respw(localGame, value.toBukkit(), ABTeam.TEAM_A);
                value.setSpectator(false);
                return value.getKillCounter().get();
            }).summaryStatistics();
            IntSummaryStatistics sb = bPlayers.stream().mapToInt(value -> {
                respw(localGame, value.toBukkit(), ABTeam.TEAM_B);
                value.setSpectator(false);
                return value.getKillCounter().get();
            }).summaryStatistics();
            ABTeam winner = handleResult(localGame, sa, sb, aPlayers, bPlayers);
            map.get(winner).forEach(player -> player.setWinner(true));
            team.reset();
        }

        plugin.gameManager.handleEnd(localGame);
    }

    protected ABTeam handleResult(LocalGame localGame, IntSummaryStatistics sa, IntSummaryStatistics sb, List<GamePlayer> aPlayers, List<GamePlayer> bPlayers) {
        if (sa.getSum() == sb.getSum())
            return sa.getAverage() > sb.getAverage() ? ABTeam.TEAM_A : ABTeam.TEAM_B;
        else
            return sa.getSum() > sb.getSum() ? ABTeam.TEAM_A : ABTeam.TEAM_B;
    }
}