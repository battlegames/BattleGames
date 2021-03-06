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
import dev.anhcraft.battle.api.BattleScoreboard;
import dev.anhcraft.battle.api.arena.game.*;
import dev.anhcraft.battle.api.arena.game.controllers.BedWarController;
import dev.anhcraft.battle.api.arena.game.options.BWTeamOptions;
import dev.anhcraft.battle.api.arena.game.options.BedWarOptions;
import dev.anhcraft.battle.api.arena.team.BWTeam;
import dev.anhcraft.battle.api.arena.team.TeamManager;
import dev.anhcraft.battle.api.events.ItemChooseEvent;
import dev.anhcraft.battle.api.events.game.BedBreakEvent;
import dev.anhcraft.battle.api.events.game.WeaponUseEvent;
import dev.anhcraft.battle.api.stats.natives.KillStat;
import dev.anhcraft.battle.api.storage.data.PlayerData;
import dev.anhcraft.battle.system.integrations.PapiExpansion;
import dev.anhcraft.battle.system.renderers.scoreboard.PlayerScoreboard;
import dev.anhcraft.battle.utils.BlockPosition;
import dev.anhcraft.battle.utils.CooldownMap;
import dev.anhcraft.battle.utils.EntityUtil;
import dev.anhcraft.battle.utils.info.InfoHolder;
import dev.anhcraft.jvmkit.utils.RandomUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BWControllerImpl extends DMControllerImpl implements BedWarController {
    protected final Map<LocalGame, TeamManager<BWTeam>> TEAM = new HashMap<>();
    protected final Map<BlockPosition, BWTeam> BEDS = new HashMap<>();

    public BWControllerImpl(BattlePlugin plugin) {
        this(plugin, Mode.BEDWAR);
    }

    BWControllerImpl(BattlePlugin plugin, Mode mode) {
        super(plugin, mode);

        String p = mode.getId() + "_";

        plugin.getPapiExpansion().handlers.put(p + "max_team_players", (player, pd, game, gp) -> {
            if (game == null) return null;
            return String.valueOf(((BedWarOptions) game.getArena().getGameOptions()).getTeamSize());
        });

        plugin.getPapiExpansion().handlers.put(p + "team", (player, pd, game, gp) -> {
            if (game == null) return null;
            TeamManager<BWTeam> t = TEAM.get(game);
            if (t == null) return null;
            BWTeam bwTeam = t.getTeam(player);
            if (bwTeam == null) return null;
            return bwTeam.getLocalizedName();
        });

        plugin.getPapiExpansion().handlers.put(p + "team_players", (player, pd, game, gp) -> {
            if (game == null) return null;
            TeamManager<BWTeam> t = TEAM.get(game);
            if (t == null) return null;
            BWTeam bwTeam = t.getTeam(player);
            if (bwTeam == null) return null;
            return Integer.toString(t.countPlayers(bwTeam));
        });

        plugin.getPapiExpansion().handlers.put(p + "bed_status", (player, pd, game, gp) -> {
            if (game == null) return null;
            TeamManager<BWTeam> t = TEAM.get(game);
            if (t == null) return null;
            BWTeam bwTeam = t.getTeam(player);
            if (bwTeam == null) return null;
            return plugin.getLocalizedMessage(blp(bwTeam.isBedPresent() ? "bed_status.present" : "bed_status.destroyed"));
        });

        plugin.getPapiExpansion().filters.add(new PapiExpansion.Filter() {
            @Override
            public boolean check(String str) {
                return str.startsWith(p + "team_") && !str.substring((p + "team_").length()).contains("_");
            }

            @Override
            public String handle(String str, Player player, PlayerData pd, LocalGame game, GamePlayer gp) {
                TeamManager<BWTeam> t = TEAM.get(game);
                if (t == null) return null;
                String team = str.substring((p + "team_").length());
                Optional<BWTeam> bwt = t.findTeam(bwTeam -> bwTeam.getColor().name().equalsIgnoreCase(team));
                return bwt.map(BWTeam::getLocalizedName).orElse(null);
            }
        });

        plugin.getPapiExpansion().filters.add(new PapiExpansion.Filter() {
            @Override
            public boolean check(String str) {
                return str.startsWith(p + "team_") && str.endsWith("_players");
            }

            @Override
            public String handle(String str, Player player, PlayerData pd, LocalGame game, GamePlayer gp) {
                TeamManager<BWTeam> t = TEAM.get(game);
                if (t == null) return null;
                String team = str.substring((p + "team_").length(), str.length() - ("_players").length());
                Optional<BWTeam> bwt = t.findTeam(bwTeam -> bwTeam.getColor().name().equalsIgnoreCase(team));
                return Integer.toString(bwt.map(t::countPlayers).orElse(0));
            }
        });

        plugin.getPapiExpansion().filters.add(new PapiExpansion.Filter() {
            @Override
            public boolean check(String str) {
                return str.startsWith(p + "bed_status_");
            }

            @Override
            public String handle(String str, Player player, PlayerData pd, LocalGame game, GamePlayer gp) {
                TeamManager<BWTeam> t = TEAM.get(game);
                if (t == null) return null;
                String team = str.substring((p + "bed_status_").length());
                Optional<BWTeam> bwt = t.findTeam(bwTeam -> bwTeam.getColor().name().equalsIgnoreCase(team));
                return bwt.map(bwTeam -> plugin.getLocalizedMessage(blp(bwTeam.isBedPresent() ? "bed_status.present" : "bed_status.destroyed"))).orElse(null);
            }
        });
    }

    @Override
    public void onInitGame(@NotNull Game game) {
        super.onInitGame(game);
        if (game instanceof LocalGame) {
            LocalGame lc = (LocalGame) game;
            if (game.getArena().getGameOptions() instanceof BedWarOptions) {
                BedWarOptions options = (BedWarOptions) game.getArena().getGameOptions();
                for (BWTeamOptions bwt : options.getTeams()) {
                    lc.addInvolvedWorld(bwt.getBedLocation().getWorld());
                    for (Location x : bwt.getSpawnPoints()) {
                        lc.addInvolvedWorld(x.getWorld());
                    }
                }
            }
        }
    }

    @Override
    public void onTick(@NotNull LocalGame game) {
        super.onTick(game);
        TeamManager<BWTeam> x = TEAM.get(game);
        if (x != null && game.getCurrentTime().get() > 100 && x.countPresentTeams() <= 1) {
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
                TeamManager<BWTeam> tm = TEAM.get(game);
                Optional<BWTeam> to = tm.nextAvailableTeam(((BedWarOptions) game.getArena().getGameOptions()).getTeamSize());
                if (!to.isPresent()) {
                    plugin.arenaManager.quit(player);
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
                            if (p.equals(player)) continue;
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
    public void onQuit(@NotNull Player player, @NotNull LocalGame game) {
        super.onQuit(player, game);
        TeamManager<BWTeam> team = TEAM.get(game);
        if (team != null) {
            BWTeam bwTeam = team.removePlayer(player);
            if (bwTeam != null) {
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
        broadcast(game, "game_start_broadcast");

        TeamManager<BWTeam> tm = new TeamManager<>();
        TEAM.put(game, tm);
        Collection<BWTeamOptions> teams = ((BedWarOptions) game.getArena().getGameOptions()).getTeams();
        BWTeam[] bwt = new BWTeam[teams.size()];
        int i = 0;
        for (BWTeamOptions team : teams) {
            BWTeam bt = new BWTeam(team);
            bwt[i++] = bt;
            tm.initTeam(bt);
            BEDS.put(BlockPosition.of(bt.getBedPart1()), bt);
            BEDS.put(BlockPosition.of(bt.getBedPart2()), bt);
        }

        List<Player> players = new ArrayList<>(game.getPlayers().keySet());
        int teammates = 0;
        int teamSize = ((BedWarOptions) game.getArena().getGameOptions()).getTeamSize();
        int teamIndex = 0;
        for (Player p : players) {
            if (teamIndex == bwt.length) {
                plugin.arenaManager.quit(p);
                continue;
            }
            BWTeam t = bwt[teamIndex];
            if (t == null) {
                plugin.arenaManager.quit(p);
                continue;
            }
            tm.addPlayer(p, t);
            if (++teammates == teamSize) {
                teamIndex++;
                teammates = 0;
            }
        }

        final int j_ = teammates == 0 ? teamIndex : teamIndex + 1;
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            for (int j = j_; j < bwt.length; j++) {
                BWTeam t = bwt[j];
                if (t != null) {
                    t.getBedPart1().setType(Material.AIR);
                    t.getBedPart2().setType(Material.AIR);
                }
            }

            game.setPhase(GamePhase.PLAYING);
            Multimap<BWTeam, Player> f = tm.toMultimap();
            f.forEach((bwTeam, player) -> {
                cancelTask(game, "respawn::" + player.getName());
                PlayerScoreboard ps = addPlayer(game, player, bwTeam);
                if (ps != null) {
                    for (BWTeam bt : f.keys()) {
                        ps.addTeamPlayers(bt.getLocalizedName(), f.get(bt));
                    }
                }
            });
        });
    }

    @Nullable
    private PlayerScoreboard addPlayer(LocalGame game, Player player, BWTeam dt) {
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
    public void onChooseItem(@NotNull ItemChooseEvent event, @NotNull LocalGame game) {
        // prevent use battle items
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreakBed(BlockBreakEvent event) {
        Block b = event.getBlock();
        if (b.getType().name().equals("BED_BLOCK") || b.getType().name().endsWith("_BED")) {
            LocalGame game = plugin.arenaManager.getGame(event.getPlayer());
            if (game != null && game.getMode() == getMode()) {
                TeamManager<BWTeam> tm = TEAM.get(game);
                if (tm == null) return;
                BWTeam pteam = tm.getTeam(event.getPlayer());
                if (pteam == null) return;
                BlockPosition bp = BlockPosition.of(b);
                BWTeam targetTeam = BEDS.get(bp);
                if (targetTeam == null) return;
                BedBreakEvent e = new BedBreakEvent(game, event.getPlayer(), b, pteam, targetTeam);
                Bukkit.getPluginManager().callEvent(e);
                if (pteam.equals(targetTeam)) {
                    event.setCancelled(true);
                } else {
                    targetTeam.getBedPart1().setType(Material.AIR);
                    targetTeam.getBedPart2().setType(Material.AIR);
                    broadcast(game, "bed_destroy_broadcast", new InfoHolder("")
                            .inform("player", event.getPlayer().getName())
                            .inform("team", targetTeam.getLocalizedName()).compile());
                    for (Player p : tm.getPlayers(targetTeam)) {
                        plugin.chatManager.sendPlayer(p, blp("respawn_unable"));
                    }
                    BEDS.remove(bp);
                }
                event.setDropItems(false);
                event.setExpToDrop(0);
            }
        }
    }

    @Override
    public boolean shouldAcceptRespawn(PlayerRespawnEvent event, LocalGame game, GamePlayer gp) {
        if (game.getPhase() != GamePhase.PLAYING) return true;
        TeamManager<BWTeam> tm = TEAM.get(game);
        BWTeam bt = Objects.requireNonNull(tm.getTeam(event.getPlayer()));
        boolean b = bt.isBedPresent();
        if (!b) {
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
                Location loc = RandomUtil.pickRandom(game.getArena().getGameOptions().getWaitSpawnPoints());
                EntityUtil.teleport(player, loc);
                break;
            }
            case PLAYING: {
                EntityUtil.teleport(player, RandomUtil.pickRandom(team.getSpawnPoints()), ok -> {
                    if (ok) {
                        performCooldownMap(game, "spawn_protection",
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
            TeamManager<BWTeam> teamManager = TEAM.get(game);
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
        TeamManager<BWTeam> tm = TEAM.remove(game);

        Multimap<BWTeam, GamePlayer> f = tm.toMultimap(game::getPlayer);
        double avgKill = 0;
        long sumKill = 0;
        BWTeam winTeam = null;
        for (BWTeam bt : f.keys()) {
            IntSummaryStatistics ss = f.get(bt).stream().mapToInt(p -> {
                respw(game, p.toBukkit(), bt);
                p.setSpectator(false);
                return p.getStats().of(KillStat.class).get();
            }).summaryStatistics();
            if (ss.getSum() > sumKill || (ss.getSum() == sumKill && ss.getAverage() > avgKill)) {
                avgKill = ss.getAverage();
                sumKill = ss.getSum();
                winTeam = bt;
            }
        }
        if (winTeam == null) {
            winTeam = f.keys().iterator().next();
        }
        f.get(winTeam).forEach(player -> player.setWinner(true));
        tm.reset();

        plugin.arenaManager.handleEnd(game);
    }

    @Override
    public @Nullable TeamManager<BWTeam> getTeamManager(@NotNull LocalGame game) {
        return TEAM.get(game);
    }

    @Override
    public @Nullable BWTeam getTeamFromBed(@NotNull BlockPosition position) {
        return BEDS.get(position);
    }
}
