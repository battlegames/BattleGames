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
import dev.anhcraft.battle.api.BattleScoreboard;
import dev.anhcraft.battle.api.arena.game.*;
import dev.anhcraft.battle.api.arena.game.controllers.TeamDeathmatchController;
import dev.anhcraft.battle.api.arena.game.options.TeamDeathmatchOptions;
import dev.anhcraft.battle.api.arena.team.ABTeam;
import dev.anhcraft.battle.api.arena.team.TeamManager;
import dev.anhcraft.battle.api.events.game.WeaponUseEvent;
import dev.anhcraft.battle.api.stats.natives.KillStat;
import dev.anhcraft.battle.system.renderers.scoreboard.PlayerScoreboard;
import dev.anhcraft.battle.utils.CooldownMap;
import dev.anhcraft.battle.utils.EntityUtil;
import dev.anhcraft.battle.utils.info.InfoHolder;
import dev.anhcraft.jvmkit.utils.RandomUtil;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TDMControllerImpl extends DMControllerImpl implements TeamDeathmatchController {
    protected final Map<LocalGame, TeamManager<ABTeam>> TEAM = new HashMap<>();

    public TDMControllerImpl(BattlePlugin plugin) {
        this(plugin, Mode.TEAM_DEATHMATCH);
    }

    TDMControllerImpl(BattlePlugin plugin, Mode mode) {
        super(plugin, mode);

        String p = mode.getId() + "_";

        plugin.getPapiExpansion().handlers.put(p + "team", (player, pd, game, gp) -> {
            if (game == null) return null;
            TeamManager<ABTeam> t = TEAM.get(game);
            if (t == null)
                return null;
            ABTeam dt = t.getTeam(player);
            if (dt == null)
                return null;
            return dt.getLocalizedName();
        });

        plugin.getPapiExpansion().handlers.put(p + "team_players", (player, pd, game, gp) -> {
            if (game == null) return null;
            TeamManager<ABTeam> t = TEAM.get(game);
            if (t == null)
                return null;
            ABTeam dt = t.getTeam(player);
            if (dt == null)
                return null;
            return Integer.toString(t.countPlayers(dt));
        });

        plugin.getPapiExpansion().handlers.put(p + "team_a", (player, pd, game, gp) -> ABTeam.TEAM_A.getLocalizedName());

        plugin.getPapiExpansion().handlers.put(p + "team_a_players", (player, pd, game, gp) -> {
            if (game == null) return null;
            TeamManager<ABTeam> t = TEAM.get(game);
            return t == null ? null : Integer.toString(t.countPlayers(ABTeam.TEAM_A));
        });

        plugin.getPapiExpansion().handlers.put(p + "team_b", (player, pd, game, gp) -> ABTeam.TEAM_B.getLocalizedName());

        plugin.getPapiExpansion().handlers.put(p + "team_b_players", (player, pd, game, gp) -> {
            if (game == null) return null;
            TeamManager<ABTeam> t = TEAM.get(game);
            return t == null ? null : Integer.toString(t.countPlayers(ABTeam.TEAM_B));
        });
    }

    @Override
    public void onInitGame(@NotNull Game game) {
        super.onInitGame(game);
        if (game instanceof LocalGame) {
            LocalGame lc = (LocalGame) game;
            if (game.getArena().getGameOptions() instanceof TeamDeathmatchOptions) {
                TeamDeathmatchOptions options = (TeamDeathmatchOptions) game.getArena().getGameOptions();
                for (Location loc : options.getPlaySpawnPoints(ABTeam.TEAM_A)) {
                    lc.addInvolvedWorld(loc.getWorld());
                }
                for (Location loc : options.getPlaySpawnPoints(ABTeam.TEAM_B)) {
                    lc.addInvolvedWorld(loc.getWorld());
                }
            }
        }
    }

    @Override
    public void onTick(@NotNull LocalGame game) {
        super.onTick(game);
        TeamManager<ABTeam> x = TEAM.get(game);
        if (x != null && x.nextEmptyTeam().isPresent()) {
            game.end();
        }
    }

    @Override
    public void onJoin(@NotNull Player player, @NotNull LocalGame game) {
        broadcast(game, "player_join_broadcast", new InfoHolder("").inform("player", player.getName()).compile());
        int m = Math.max(game.getArena().getGameOptions().getMinPlayers(), 1);
        switch (game.getPhase()) {
            case WAITING: {
                respw(game, player, null);
                BattleScoreboard bs = game.getMode().getWaitingScoreboard();
                if (bs.isEnabled()) {
                    plugin.scoreboardRenderer.setScoreboard(new PlayerScoreboard(player, bs.getTitle(), bs.getContent(), bs.getFixedLength()));
                }
                if (m <= game.getPlayerCount()) countdown(game);
                break;
            }
            case PLAYING: {
                TeamManager<ABTeam> teamManager = TEAM.get(game);
                int a = teamManager.countPlayers(ABTeam.TEAM_A);
                int b = teamManager.countPlayers(ABTeam.TEAM_B);
                ABTeam t = a <= b ? ABTeam.TEAM_A : ABTeam.TEAM_B;
                teamManager.addPlayer(player, t);

                // don't stack these functions with the player count above
                // this one already include the new player...
                List<Player> ta = teamManager.getPlayers(ABTeam.TEAM_A);
                List<Player> tb = teamManager.getPlayers(ABTeam.TEAM_B);
                PlayerScoreboard sps = addPlayer(game, player, t);
                if (sps != null) {
                    sps.addTeamPlayers(ABTeam.TEAM_A.name(), ta);
                    sps.addTeamPlayers(ABTeam.TEAM_B.name(), tb);
                }

                List<Player> lc = Collections.singletonList(player);

                for (Player p : ta) {
                    if (p.equals(player)) continue;
                    PlayerScoreboard ps = plugin.scoreboardRenderer.getScoreboard(p);
                    if (ps != null) {
                        ps.addTeamPlayers(t.name(), lc);
                    }
                }

                for (Player p : tb) {
                    if (p.equals(player)) continue;
                    PlayerScoreboard ps = plugin.scoreboardRenderer.getScoreboard(p);
                    if (ps != null) {
                        ps.addTeamPlayers(t.name(), lc);
                    }
                }
            }
        }
    }

    @Override
    public void onQuit(@NotNull Player player, @NotNull LocalGame game) {
        super.onQuit(player, game);
        TeamManager<ABTeam> teamManager = TEAM.get(game);
        if (teamManager != null) {
            ABTeam abTeam = teamManager.removePlayer(player);
            for (Map.Entry<Player, ABTeam> ent : teamManager.getPlayerTeam()) {
                PlayerScoreboard ps = plugin.scoreboardRenderer.getScoreboard(ent.getKey());
                if (ps != null) {
                    ps.removeTeamPlayer(abTeam.name(), player);
                }
            }
        }
    }

    @Override
    protected void play(LocalGame game) {
        broadcast(game, "game_start_broadcast");

        List<Player> x = new ArrayList<>(game.getPlayers().keySet());
        int sz = Math.floorDiv(x.size(), 2);
        List<Player> ta = x.subList(0, sz);
        List<Player> tb = x.subList(sz, x.size());

        TeamManager<ABTeam> teamManager = new TeamManager<>();
        teamManager.addPlayers(ta, ABTeam.TEAM_A);
        teamManager.addPlayers(tb, ABTeam.TEAM_B);
        TEAM.put(game, teamManager);

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            game.setPhase(GamePhase.PLAYING);
            ta.forEach(p -> {
                cancelTask(game, "respawn::" + p.getName());
                PlayerScoreboard ps = addPlayer(game, p, ABTeam.TEAM_A);
                if (ps != null) {
                    ps.addTeamPlayers(ABTeam.TEAM_A.name(), ta);
                    ps.addTeamPlayers(ABTeam.TEAM_B.name(), tb);
                }
            });
            tb.forEach(p -> {
                cancelTask(game, "respawn::" + p.getName());
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
        BattleScoreboard bs = game.getMode().getPlayingScoreboard();
        if (bs.isEnabled()) {
            ps = new PlayerScoreboard(player, bs.getTitle(), bs.getContent(), bs.getFixedLength());
            plugin.scoreboardRenderer.setScoreboard(ps);
        }
        respw(game, player, dt);
        return ps;
    }

    @Override
    protected void respw(LocalGame game, Player player) {
        TeamManager<ABTeam> t = TEAM.get(game);
        respw(game, player, t == null ? null : t.getTeam(player));
    }

    private void respw(LocalGame game, Player player, ABTeam team) {
        player.setGameMode(GameMode.SURVIVAL);
        switch (game.getPhase()) {
            case END:
            case WAITING: {
                Location loc = RandomUtil.pickRandom(game.getArena().getGameOptions().getWaitSpawnPoints());
                EntityUtil.teleport(player, loc);
                break;
            }
            case PLAYING: {
                Location loc = RandomUtil.pickRandom(((TeamDeathmatchOptions) game.getArena().getGameOptions()).getPlaySpawnPoints(team));
                EntityUtil.teleport(player, loc, ok -> {
                    if (ok) {
                        performCooldownMap(game, "spawn_protection",
                                cooldownMap -> cooldownMap.resetTime(player),
                                () -> new CooldownMap(player));
                        performCooldownMap(game, "item_selection",
                                cooldownMap -> cooldownMap.resetTime(player),
                                () -> new CooldownMap(player));
                    }
                });
            }
        }
    }

    @Override
    public void onUseWeapon(@NotNull WeaponUseEvent event, @NotNull LocalGame game) {
        if (event.getReport().getEntity() instanceof Player) {
            Player target = (Player) event.getReport().getEntity();
            TeamManager<ABTeam> teamManager = TEAM.get(game);
            if (teamManager.getTeam(event.getReport().getDamager()) == teamManager.getTeam(target)) {
                event.setCancelled(true);
            } else {
                performCooldownMap(game, "spawn_protection",
                        cooldownMap -> {
                            long t = game.getArena().getGameOptions().getSpawnProtectionTime();
                            if (!cooldownMap.isPassed(target, t)) {
                                event.setCancelled(true);
                            }
                        });
            }
        }
    }

    @Override
    public void onEnd(@NotNull LocalGame game) {
        cancelAllTasks(game);
        TeamManager<ABTeam> teamManager = TEAM.remove(game);

        Map<ABTeam, List<GamePlayer>> map = teamManager.reverse(game::getPlayer);
        List<GamePlayer> aPlayers = map.get(ABTeam.TEAM_A);
        List<GamePlayer> bPlayers = map.get(ABTeam.TEAM_B);
        if (aPlayers != null & bPlayers != null) {
            IntSummaryStatistics sa = aPlayers.stream().mapToInt(value -> {
                respw(game, value.toBukkit(), ABTeam.TEAM_A);
                value.setSpectator(false);
                return value.getStats().of(KillStat.class).get();
            }).summaryStatistics();
            IntSummaryStatistics sb = bPlayers.stream().mapToInt(value -> {
                respw(game, value.toBukkit(), ABTeam.TEAM_B);
                value.setSpectator(false);
                return value.getStats().of(KillStat.class).get();
            }).summaryStatistics();
            ABTeam winner = handleResult(game, sa, sb, aPlayers, bPlayers);
            map.get(winner).forEach(player -> player.setWinner(true));
            teamManager.reset();
        }

        plugin.arenaManager.handleEnd(game);
    }

    protected ABTeam handleResult(LocalGame game, IntSummaryStatistics sa, IntSummaryStatistics sb, List<GamePlayer> aPlayers, List<GamePlayer> bPlayers) {
        if (sa.getSum() == sb.getSum())
            return sa.getAverage() > sb.getAverage() ? ABTeam.TEAM_A : ABTeam.TEAM_B;
        else
            return sa.getSum() > sb.getSum() ? ABTeam.TEAM_A : ABTeam.TEAM_B;
    }

    @Override
    public @Nullable TeamManager<ABTeam> getTeamManager(@Nullable LocalGame game) {
        return TEAM.get(game);
    }
}
