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
import dev.anhcraft.battle.api.events.game.GamePlayerWeaponEvent;
import dev.anhcraft.battle.api.events.ItemChooseEvent;
import dev.anhcraft.battle.api.arena.game.GamePhase;
import dev.anhcraft.battle.api.arena.game.GamePlayer;
import dev.anhcraft.battle.api.arena.game.LocalGame;
import dev.anhcraft.battle.api.arena.mode.Mode;
import dev.anhcraft.battle.api.inventory.item.GrenadeModel;
import dev.anhcraft.battle.api.inventory.item.GunModel;
import dev.anhcraft.battle.api.inventory.item.ItemType;
import dev.anhcraft.battle.api.stats.natives.KillStat;
import dev.anhcraft.battle.system.renderers.scoreboard.PlayerScoreboard;
import dev.anhcraft.battle.utils.CooldownMap;
import dev.anhcraft.battle.utils.EntityUtil;
import dev.anhcraft.battle.utils.LocationUtil;
import dev.anhcraft.jvmkit.utils.RandomUtil;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class DeathmatchController extends ModeController {
    public DeathmatchController(BattlePlugin plugin) {
        this(plugin, Mode.DEATHMATCH);
    }

    DeathmatchController(BattlePlugin plugin, Mode mode) {
        super(plugin, mode);
    }

    @Override
    public void onQuit(@NotNull Player player, @NotNull LocalGame game){
        broadcast(game, "player_quit_broadcast", s -> s.replace("{__target__}", player.getDisplayName()));
        for (Player p : game.getPlayers().keySet()){
            PlayerScoreboard ps = plugin.scoreboardRenderer.getScoreboard(p);
            if (ps != null) {
                ps.removeTeamPlayer("global", player);
            }
        }
    }

    @Override
    public void onJoin(@NotNull Player player, @NotNull LocalGame game) {
        broadcast(game, "player_join_broadcast", s -> s.replace("{__target__}", player.getDisplayName()));
        int m = Math.min(game.getArena().getAttributes().getInt("min_players"), 1);
        switch (game.getPhase()){
            case WAITING:{
                respw(game, player);
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
                PlayerScoreboard sps = addPlayer(game, player);
                if(sps != null) {
                    sps.addTeamPlayers("global", game.getPlayers().keySet());
                }
                List<Player> lc = Collections.singletonList(player);
                for (Player p : game.getPlayers().keySet()){
                    if(p.equals(player)) continue;
                    PlayerScoreboard ps = plugin.scoreboardRenderer.getScoreboard(p);
                    if (ps != null) {
                        ps.addTeamPlayers("global", lc);
                    }
                }
            }
        }
    }

    protected void countdown(LocalGame game) {
        if(hasTask(game, "countdown")) return;
        AtomicLong current = new AtomicLong(game.getArena().getAttributes().getLong("countdown_time")/20L);
        int m = Math.min(game.getArena().getAttributes().getInt("min_players"), 1);
        trackTask(game, "countdown", plugin.taskHelper.newAsyncTimerTask(() -> {
            if(m <= game.getPlayerCount()) {
                broadcastTitle(game, "countdown_title", "countdown_subtitle", s -> s.replace("{__current__}", current.toString()));
                playSound(game, Sound.BLOCK_FENCE_GATE_OPEN);
                if(current.getAndDecrement() == 0) {
                    cancelTask(game, "countdown");
                    play(game);
                }
            } else cancelTask(game, "countdown");
        }, 0, 20));
    }

    protected void play(LocalGame game) {
        broadcast(game,"game_start_broadcast");
        plugin.taskHelper.newTask(() -> {
            game.setPhase(GamePhase.PLAYING);
            game.getPlayers().values().forEach(p -> {
                cancelTask(game, "respawn::"+p.toBukkit().getName());
                PlayerScoreboard ps = addPlayer(game, p.toBukkit());
                if(ps != null) ps.addTeamPlayers("global", game.getPlayers().keySet());
            });
        });
    }

    @Nullable
    private PlayerScoreboard addPlayer(LocalGame game, Player player) {
        PlayerScoreboard ps = null;
        if(game.getMode().isPlayingScoreboardEnabled()) {
            String title = game.getMode().getPlayingScoreboardTitle();
            List<String> content = game.getMode().getPlayingScoreboardContent();
            int len = game.getMode().isPlayingScoreboardFixedLength();
            ps = new PlayerScoreboard(player, title, content, len);
            ps.setNameTagVisibility(Team.OptionStatus.NEVER);
            plugin.scoreboardRenderer.setScoreboard(ps);
        }
        respw(game, player);
        return ps;
    }

    protected void respw(LocalGame game, Player player) {
        player.setGameMode(GameMode.SURVIVAL);
        switch (game.getPhase()) {
            case END:
            case WAITING: {
                String loc = RandomUtil.pickRandom(game.getArena().getAttributes().getStringList("waiting_spawn_points"));
                EntityUtil.teleport(player, LocationUtil.fromString(loc));
                break;
            }
            case PLAYING: {
                String loc = RandomUtil.pickRandom(game.getArena().getAttributes().getStringList("playing_spawn_points"));
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

    @Override
    public void onChooseItem(@NotNull ItemChooseEvent event, @NotNull LocalGame game){
        if(game.getPhase() != GamePhase.PLAYING) return;
        performCooldownMap(game, "item_selection", cooldownMap -> {
            int t = game.getArena().getAttributes().getInt("item_selection_time");
            if(cooldownMap.isPassed(event.getPlayer(), t))
                plugin.chatManager.sendPlayer(event.getPlayer(), blp("error_item_selection_overtime"));
            else {
                ItemType type = event.getItemModel().getItemType();
                if (type == ItemType.GUN)
                    plugin.gunManager.selectGun(event.getPlayer(), (GunModel) event.getItemModel());
                else if (type == ItemType.GRENADE)
                    plugin.grenadeManager.selectGrenade(event.getPlayer(), (GrenadeModel) event.getItemModel());
                else
                    plugin.chatManager.sendPlayer(event.getPlayer(), blp("error_disabled_item_type"));
            }
        });
    }

    public boolean shouldAcceptRespawn(PlayerRespawnEvent event, LocalGame game, GamePlayer gp){
        return true;
    }

    @Override
    public void onRespawn(@NotNull PlayerRespawnEvent event, @NotNull LocalGame game) {
        Player player = event.getPlayer();
        GamePlayer gp = game.getPlayer(player);
        if (gp != null) {
            String sl = RandomUtil.pickRandom(game.getArena().getAttributes()
                    .getStringList("waiting_spawn_points"));
            Location loc = LocationUtil.fromString(sl);
            if(loc.getWorld() == null) loc.setWorld(player.getWorld());
            event.setRespawnLocation(loc);
            gp.setSpectator(true);
            if(!shouldAcceptRespawn(event, game, gp)) return;
            player.setGameMode(GameMode.SPECTATOR);
            AtomicLong current = new AtomicLong(game.getArena().getAttributes().getLong("respawn_waiting_time")/20L);
            String task = "respawn::"+player.getName();
            trackTask(game, task, plugin.taskHelper.newAsyncTimerTask(() -> {
                if(player.isOnline()) {
                    sendTitle(player, "respawn_title", "respawn_subtitle", s -> s.replace("{__current__}", current.toString()));
                    playSound(game, Sound.BLOCK_FENCE_GATE_OPEN);
                    if(current.getAndDecrement() == 0) {
                        cancelTask(game, task);
                        gp.setSpectator(false);
                        plugin.taskHelper.newTask(() -> respw(game, player));
                    }
                } else cancelTask(game, task);
            }, 0, 20));
        }
    }

    @EventHandler
    public void damage(GamePlayerWeaponEvent e) {
        if(e.getGame().getMode() != getMode()) return;
        performCooldownMap(e.getGame(), "spawn_protection",
            cooldownMap -> {
                int t = e.getGame().getArena().getAttributes().getInt("spawn_protection_time");
                if(!cooldownMap.isPassed(e.getPlayer(), t)) e.setCancelled(true);
            });
    }

    @Override
    public void onEnd(@NotNull LocalGame game) {
        cancelAllTasks(game);
        clearCooldown();

        GamePlayer winner = null;
        int maxKill = 0; // cache (don't use #get frequently)
        Iterator<GamePlayer> players = game.getPlayers().values().iterator();
        do {
            GamePlayer x = players.next();
            if(winner == null) {
                winner = x;
                maxKill = winner.getStats().of(KillStat.class).get();
            }
            else {
                int nextKill = x.getStats().of(KillStat.class).get();
                if(maxKill < nextKill) {
                    winner = x;
                    maxKill = nextKill;
                }
            }
            x.setSpectator(false);
            respw(game, x.toBukkit());
        } while(players.hasNext()); // we use do-while since there is always at least one player
        winner.setWinner(true);

        plugin.arenaManager.handleEnd(game);
    }
}
