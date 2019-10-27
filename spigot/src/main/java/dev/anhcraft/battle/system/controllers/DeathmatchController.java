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
import dev.anhcraft.battle.api.events.ItemChooseEvent;
import dev.anhcraft.battle.api.game.GamePhase;
import dev.anhcraft.battle.api.game.GamePlayer;
import dev.anhcraft.battle.api.game.LocalGame;
import dev.anhcraft.battle.api.game.Mode;
import dev.anhcraft.battle.api.inventory.items.GrenadeModel;
import dev.anhcraft.battle.api.inventory.items.GunModel;
import dev.anhcraft.battle.api.inventory.items.ItemType;
import dev.anhcraft.battle.system.handlers.GrenadeHandler;
import dev.anhcraft.battle.system.handlers.GunHandler;
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
    public void onQuit(Player player, LocalGame localGame){
        broadcast(localGame, "player_quit_broadcast", s -> s.replace("{__target__}", player.getDisplayName()));
    }

    @Override
    public void onJoin(Player player, LocalGame localGame) {
        broadcast(localGame, "player_join_broadcast", s -> s.replace("{__target__}", player.getDisplayName()));
        int m = Math.min(localGame.getArena().getAttributes().getInt("min_players"), 1);
        switch (localGame.getPhase()){
            case WAITING:{
                respw(localGame, player);
                if(localGame.getMode().isWaitingScoreboardEnabled()) {
                    String title = localGame.getMode().getWaitingScoreboardTitle();
                    List<String> content = localGame.getMode().getWaitingScoreboardContent();
                    int len = localGame.getMode().isWaitingScoreboardFixedLength();
                    plugin.scoreboardRenderer.setScoreboard(new PlayerScoreboard(player, title, content, len));
                }
                if(m <= localGame.getPlayerCount()) countdown(localGame);
                break;
            }
            case PLAYING: addPlayer(localGame, player);
        }
    }

    protected void countdown(LocalGame localGame) {
        if(hasTask(localGame, "countdown")) return;
        AtomicLong current = new AtomicLong(localGame.getArena().getAttributes().getLong("countdown_time")/20L);
        int m = Math.min(localGame.getArena().getAttributes().getInt("min_players"), 1);
        trackTask(localGame, "countdown", plugin.taskHelper.newAsyncTimerTask(() -> {
            if(m <= localGame.getPlayerCount()) {
                broadcastTitle(localGame, "countdown_title", "countdown_subtitle", s -> s.replace("{__current__}", current.toString()));
                playSound(localGame, Sound.BLOCK_FENCE_GATE_OPEN);
                if(current.getAndDecrement() == 0) {
                    cancelTask(localGame, "countdown");
                    play(localGame);
                }
            } else cancelTask(localGame, "countdown");
        }, 0, 20));
    }

    protected void play(LocalGame localGame) {
        broadcast(localGame,"game_start_broadcast");
        plugin.taskHelper.newTask(() -> {
            localGame.setPhase(GamePhase.PLAYING);
            localGame.getPlayers().values().forEach(p -> {
                cancelTask(localGame, "respawn::"+p.toBukkit().getName());
                addPlayer(localGame, p.toBukkit());
            });
        });
    }

    private void addPlayer(LocalGame localGame, Player player) {
        if(localGame.getMode().isPlayingScoreboardEnabled()) {
            String title = localGame.getMode().getPlayingScoreboardTitle();
            List<String> content = localGame.getMode().getPlayingScoreboardContent();
            int len = localGame.getMode().isPlayingScoreboardFixedLength();
            plugin.scoreboardRenderer.setScoreboard(new PlayerScoreboard(player, title, content, len));
        }
        respw(localGame, player);
    }

    protected void respw(LocalGame localGame, Player player) {
        player.setGameMode(GameMode.SURVIVAL);
        switch (localGame.getPhase()) {
            case END:
            case WAITING: {
                String loc = RandomUtil.pickRandom(localGame.getArena().getAttributes().getStringList("waiting_spawn_points"));
                EntityUtil.teleport(player, LocationUtil.fromString(loc));
                break;
            }
            case PLAYING: {
                String loc = RandomUtil.pickRandom(localGame.getArena().getAttributes().getStringList("playing_spawn_points"));
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

    @Override
    public void onChooseItem(ItemChooseEvent event, LocalGame localGame){
        if(localGame.getPhase() != GamePhase.PLAYING) return;
        performCooldownMap(localGame, "item_selection", cooldownMap -> {
            int t = localGame.getArena().getAttributes().getInt("item_selection_time");
            if(cooldownMap.isPassed(event.getPlayer(), t))
                plugin.chatManager.sendPlayer(event.getPlayer(), blp("error_item_selection_overtime"));
            else {
                ItemType type = event.getItemModel().getItemType();
                if (type == ItemType.GUN)
                    plugin.getHandler(GunHandler.class).selectGun(event.getPlayer(), (GunModel) event.getItemModel());
                else if (type == ItemType.GRENADE)
                    plugin.getHandler(GrenadeHandler.class).selectGrenade(event.getPlayer(), (GrenadeModel) event.getItemModel());
                else
                    plugin.chatManager.sendPlayer(event.getPlayer(), blp("error_disabled_item_type"));
            }
        });
    }

    @Override
    public void onRespawn(PlayerRespawnEvent event, LocalGame localGame) {
        Player player = event.getPlayer();
        GamePlayer gp = localGame.getPlayer(player);
        if (gp != null) {
            String sl = RandomUtil.pickRandom(localGame.getArena().getAttributes()
                    .getStringList("waiting_spawn_points"));
            Location loc = LocationUtil.fromString(sl);
            if(loc.getWorld() == null) loc.setWorld(player.getWorld());
            event.setRespawnLocation(loc);
            gp.setSpectator(true);
            player.setGameMode(GameMode.SPECTATOR);
            AtomicLong current = new AtomicLong(localGame.getArena().getAttributes().getLong("respawn_waiting_time")/20L);
            String task = "respawn::"+player.getName();
            trackTask(localGame, task, plugin.taskHelper.newAsyncTimerTask(() -> {
                if(player.isOnline()) {
                    sendTitle(player, "respawn_title", "respawn_subtitle", s -> s.replace("{__current__}", current.toString()));
                    playSound(localGame, Sound.BLOCK_FENCE_GATE_OPEN);
                    if(current.getAndDecrement() == 0) {
                        cancelTask(localGame, task);
                        gp.setSpectator(false);
                        plugin.taskHelper.newTask(() -> respw(localGame, player));
                    }
                } else cancelTask(localGame, task);
            }, 0, 20));
        }
    }

    @EventHandler
    public void damage(GamePlayerDamageEvent e) {
        if(e.getGame().getMode() != getMode()) return;
        performCooldownMap(e.getGame(), "spawn_protection",
            cooldownMap -> {
                int t = e.getGame().getArena().getAttributes().getInt("spawn_protection_time");
                if(!cooldownMap.isPassed(e.getPlayer(), t)) e.setCancelled(true);
            });
    }

    @Override
    public void onEnd(LocalGame localGame) {
        cancelAllTasks(localGame);
        clearCooldown();

        GamePlayer winner = null;
        int maxKill = 0; // cache (don't use #get frequently)
        Iterator<GamePlayer> players = localGame.getPlayers().values().iterator();
        do {
            GamePlayer x = players.next();
            if(winner == null) {
                winner = x;
                maxKill = winner.getKillCounter().get();
            }
            else {
                int nextKill = x.getKillCounter().get();
                if(maxKill < nextKill) {
                    winner = x;
                    maxKill = nextKill;
                }
            }
            x.setSpectator(false);
            respw(localGame, x.toBukkit());
        } while(players.hasNext()); // we use do-while since there is always at least one player
        winner.setWinner(true);

        plugin.gameManager.handleEnd(localGame);
    }
}
