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
import dev.anhcraft.abm.api.events.ItemChooseEvent;
import dev.anhcraft.abm.api.game.Game;
import dev.anhcraft.abm.api.game.GamePhase;
import dev.anhcraft.abm.api.game.GamePlayer;
import dev.anhcraft.abm.api.game.Mode;
import dev.anhcraft.abm.api.inventory.items.GunModel;
import dev.anhcraft.abm.api.inventory.items.ItemType;
import dev.anhcraft.abm.system.handlers.GunHandler;
import dev.anhcraft.abm.system.renderers.scoreboard.PlayerScoreboard;
import dev.anhcraft.abm.utils.CooldownMap;
import dev.anhcraft.abm.utils.LocationUtil;
import dev.anhcraft.jvmkit.utils.RandomUtil;
import org.bukkit.GameMode;
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
    public void onQuit(Player player, Game game){
        broadcast(game, "player_quit_broadcast", s -> s.replace("{__target__}", player.getDisplayName()));
    }

    @Override
    public void onJoin(Player player, Game game) {
        broadcast(game, "player_join_broadcast", s -> s.replace("{__target__}", player.getDisplayName()));
        int m = Math.min(game.getArena().getAttributes().getInt("min_players"), 1);
        switch (game.getPhase()){
            case WAITING:{
                respw(game, player);
                if(game.getMode().isWaitingScoreboardEnabled()) {
                    String title = game.getMode().getWaitingScoreboardTitle();
                    List<String> content = game.getMode().getWaitingScoreboardContent();
                    boolean b = game.getMode().isWaitingScoreboardFixedLength();
                    plugin.scoreboardRenderer.setScoreboard(new PlayerScoreboard(player, title, content, b));
                }
                if(m <= game.countPlayers()) countdown(game);
                break;
            }
            case PLAYING: addPlayer(game, player);
        }
    }

    protected void countdown(Game game) {
        if(hasTask(game, "countdown")) return;
        AtomicLong current = new AtomicLong(game.getArena().getAttributes().getLong("countdown_time")/20L);
        int m = Math.min(game.getArena().getAttributes().getInt("min_players"), 1);
        trackTask(game, "countdown", plugin.taskHelper.newAsyncTimerTask(() -> {
            if(m <= game.countPlayers()) {
                broadcastTitle(game, "countdown_title", "countdown_subtitle", s -> s.replace("{__current__}", current.toString()));
                playSound(game, Sound.BLOCK_FENCE_GATE_OPEN);
                if(current.getAndDecrement() == 0) {
                    cancelTask(game, "countdown");
                    play(game);
                }
            } else cancelTask(game, "countdown");
        }, 0, 20));
    }

    protected void play(Game game) {
        broadcast(game,"game_start_broadcast");
        plugin.taskHelper.newTask(() -> {
            game.setPhase(GamePhase.PLAYING);
            game.getPlayers().values().forEach(p -> {
                cancelTask(game, "respawn::"+p.getPlayer().getName());
                addPlayer(game, p.getPlayer());
            });
        });
    }

    private void addPlayer(Game game, Player player) {
        if(game.getMode().isPlayingScoreboardEnabled()) {
            String title = game.getMode().getPlayingScoreboardTitle();
            List<String> content = game.getMode().getPlayingScoreboardContent();
            boolean b = game.getMode().isPlayingScoreboardFixedLength();
            plugin.scoreboardRenderer.setScoreboard(new PlayerScoreboard(player, title, content, b));
        }
        respw(game, player);
    }

    protected void respw(Game game, Player player) {
        player.setGameMode(GameMode.SURVIVAL);
        switch (game.getPhase()) {
            case END:
            case WAITING: {
                String loc = RandomUtil.pickRandom(game.getArena().getAttributes().getStringList("waiting_spawn_points"));
                player.teleport(LocationUtil.fromString(loc));
                break;
            }
            case PLAYING: {
                String loc = RandomUtil.pickRandom(game.getArena().getAttributes().getStringList("playing_spawn_points"));
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

    @Override
    public void onChooseItem(ItemChooseEvent event, Game game){
        if(game.getPhase() != GamePhase.PLAYING) return;
        performCooldownMap(game, "item_selection", cooldownMap -> {
            int t = game.getArena().getAttributes().getInt("item_selection_time");
            if(cooldownMap.isPassed(event.getPlayer(), t))
                plugin.chatManager.sendPlayer(event.getPlayer(), blp("error_item_selection_overtime"));
            else {
                if (event.getItemModel().getItemType() == ItemType.GUN)
                    plugin.getHandler(GunHandler.class).selectGun(event.getPlayer(), (GunModel) event.getItemModel());
                else
                    plugin.chatManager.sendPlayer(event.getPlayer(), blp("error_disabled_item_type"));
            }
        });
    }

    @Override
    public void onRespawn(PlayerRespawnEvent event, Game game) {
        Player player = event.getPlayer();
        GamePlayer gp = game.getPlayer(player);
        if (gp != null) {
            String loc = RandomUtil.pickRandom(game.getArena().getAttributes()
                    .getStringList("waiting_spawn_points"));
            event.setRespawnLocation(LocationUtil.fromString(loc));
            gp.setSpectator(true);
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
    public void damage(GamePlayerDamageEvent e) {
        if(e.getGame().getMode() != getMode()) return;
        performCooldownMap(e.getGame(), "spawn_protection",
            cooldownMap -> {
                int t = e.getGame().getArena().getAttributes().getInt("spawn_protection_time");
                if(!cooldownMap.isPassed(e.getPlayer(), t)) e.setCancelled(true);
            });
    }

    @Override
    public void onEnd(Game game) {
        cancelAllTasks(game);
        clearCooldown();

        GamePlayer winner = null;
        int maxKill = 0; // cache (don't use #get frequently)
        Iterator<GamePlayer> players = game.getPlayers().values().iterator();
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
            respw(game, x.getPlayer());
        } while(players.hasNext()); // we use do-while since there is always at least one player
        winner.setWinner(true);
        plugin.gameManager.rewardAndSaveCache(game);
        plugin.gameManager.destroy(game);
    }
}
