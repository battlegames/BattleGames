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

import com.google.common.collect.Table;
import dev.anhcraft.battle.BattlePlugin;
import dev.anhcraft.battle.api.BattleApi;
import dev.anhcraft.battle.api.BattleSound;
import dev.anhcraft.battle.api.arena.game.Game;
import dev.anhcraft.battle.api.arena.game.GamePhase;
import dev.anhcraft.battle.api.arena.game.GamePlayer;
import dev.anhcraft.battle.api.arena.game.LocalGame;
import dev.anhcraft.battle.api.arena.game.Mode;
import dev.anhcraft.battle.api.arena.game.options.DeathmatchOptions;
import dev.anhcraft.battle.api.events.ItemChooseEvent;
import dev.anhcraft.battle.api.events.game.WeaponUseEvent;
import dev.anhcraft.battle.api.inventory.item.BattleItem;
import dev.anhcraft.battle.api.inventory.item.ItemType;
import dev.anhcraft.battle.api.inventory.item.NullBattleItem;
import dev.anhcraft.battle.api.BattleScoreboard;
import dev.anhcraft.battle.api.stats.natives.KillStat;
import dev.anhcraft.battle.api.stats.natives.RespawnStat;
import dev.anhcraft.battle.system.renderers.scoreboard.PlayerScoreboard;
import dev.anhcraft.battle.utils.CooldownMap;
import dev.anhcraft.battle.utils.EntityUtil;
import dev.anhcraft.battle.utils.info.InfoHolder;
import dev.anhcraft.jvmkit.utils.RandomUtil;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class DMControllerImpl extends GameControllerImpl {
    public DMControllerImpl(BattlePlugin plugin) {
        this(plugin, Mode.DEATHMATCH);
    }

    public DMControllerImpl(BattlePlugin plugin, Mode mode) {
        super(plugin, mode);
    }

    @Override
    public void onInitGame(@NotNull Game game){
        super.onInitGame(game);
        if(game instanceof LocalGame) {
            LocalGame lc = (LocalGame) game;
            if(game.getArena().getGameOptions() instanceof DeathmatchOptions) {
                DeathmatchOptions options = (DeathmatchOptions) game.getArena().getGameOptions();
                for (Location loc : options.getPlaySpawnPoints()) {
                    lc.addInvolvedWorld(loc.getWorld());
                }
            }
        }
    }

    @Override
    public void onQuit(@NotNull Player player, @NotNull LocalGame game){
        broadcast(game, "player_quit_broadcast", new InfoHolder("").inform("player", player.getName()).compile());
        for (Player p : game.getPlayers().keySet()){
            PlayerScoreboard ps = plugin.scoreboardRenderer.getScoreboard(p);
            if (ps != null) {
                ps.removeTeamPlayer("global", player);
            }
        }
    }

    @Override
    public void onJoin(@NotNull Player player, @NotNull LocalGame game) {
        broadcast(game, "player_join_broadcast", new InfoHolder("").inform("player", player.getName()).compile());
        int m = Math.min(game.getArena().getGameOptions().getMinPlayers(), 1);
        switch (game.getPhase()){
            case WAITING:{
                respw(game, player);
                BattleScoreboard bs = game.getMode().getWaitingScoreboard();
                if(bs.isEnabled()) {
                    plugin.scoreboardRenderer.setScoreboard(new PlayerScoreboard(player, bs.getTitle(), bs.getContent(), bs.getFixedLength()));
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
        AtomicLong current = new AtomicLong(game.getArena().getGameOptions().getCountdownTime()/20L);
        int m = Math.min(game.getArena().getGameOptions().getMinPlayers(), 1);
        trackTask(game, "countdown", plugin.extension.getTaskHelper().newAsyncTimerTask(() -> {
            if(m <= game.getPlayerCount()) {
                broadcastTitle(game, "countdown_title", "countdown_subtitle", new InfoHolder("").inform("current", current.get()).compile());
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
        plugin.extension.getTaskHelper().newTask(() -> {
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
        BattleScoreboard bs = game.getMode().getPlayingScoreboard();
        if(bs.isEnabled()) {
            ps = new PlayerScoreboard(player, bs.getTitle(), bs.getContent(), bs.getFixedLength());
            ps.setNameTagVisibility(Team.OptionStatus.NEVER);
            plugin.scoreboardRenderer.setScoreboard(ps);
        }
        respw(game, player);
        return ps;
    }

    protected void respw(LocalGame game, Player player) {
        player.setGameMode(org.bukkit.GameMode.SURVIVAL);
        switch (game.getPhase()) {
            case END:
            case WAITING: {
                Location loc = RandomUtil.pickRandom(game.getArena().getGameOptions().getWaitSpawnPoints());
                EntityUtil.teleport(player, loc);
                break;
            }
            case PLAYING: {
                Location loc = RandomUtil.pickRandom(((DeathmatchOptions) game.getArena().getGameOptions()).getPlaySpawnPoints());
                EntityUtil.teleport(player, loc, ok -> {
                    if(ok){
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
    public void onChooseItem(@NotNull ItemChooseEvent event, @NotNull LocalGame game){
        if(game.getPhase() != GamePhase.PLAYING) return;
        Player player = event.getPlayer();
        Table<ItemType, String, BattleItem<?>> igbp = Objects.requireNonNull(game.getPlayer(player)).getIgBackpack();
        ItemType type = event.getItemModel().getItemType();
        if (type == ItemType.GUN || type == ItemType.SCOPE || type == ItemType.MAGAZINE || type == ItemType.GRENADE) {
            String id = event.getItemModel().getId();
            BattleItem<?> item = igbp.get(type, id);
            if(item == null) {
                performCooldownMap(game, "item_selection", cooldownMap -> {
                    long t = game.getArena().getGameOptions().getItemSelectTime();
                    if(cooldownMap.isPassed(player, t)) {
                        plugin.chatManager.sendPlayer(player, blp("error_item_selection_overtime"));
                    } else {
                        if (plugin.itemManager.selectItem(player, event.getItemModel())) {
                            igbp.put(type, id, NullBattleItem.INSTANCE);
                            BattleSound bs = BattleApi.getInstance().getGeneralConfig().getBackpackSoundRemoveItem();
                            if (bs != null) {
                                bs.play(player);
                            }
                            BattleApi.getInstance().getGuiManager().updateView(player, event.getSlotReport().getView());
                        } else {
                            plugin.chatManager.sendPlayer(player, "inv.hotbar_full");
                        }
                    }
                });
            } else if(item.getModel() != null && item.getModel().getItemType() == type) {
                if(plugin.itemManager.selectItem(player, item)) {
                    igbp.put(type, id, NullBattleItem.INSTANCE);
                    BattleSound bs = BattleApi.getInstance().getGeneralConfig().getBackpackSoundRemoveItem();
                    if (bs != null) {
                        bs.play(player);
                    }
                    BattleApi.getInstance().getGuiManager().updateView(player, event.getSlotReport().getView());
                } else {
                    plugin.chatManager.sendPlayer(player, "inv.hotbar_full");
                }
            }
        } else {
            plugin.chatManager.sendPlayer(player, blp("error_disabled_item_type"));
        }
    }

    public boolean shouldAcceptRespawn(PlayerRespawnEvent event, LocalGame game, GamePlayer gp){
        return true;
    }

    @Override
    public void onRespawn(@NotNull PlayerRespawnEvent event, @NotNull LocalGame game) {
        Player player = event.getPlayer();
        GamePlayer gp = game.getPlayer(player);
        if (gp != null) {
            Location loc = Objects.requireNonNull(RandomUtil.pickRandom(game.getArena().getGameOptions().getWaitSpawnPoints()));
            if(loc.getWorld() == null) loc.setWorld(player.getWorld());
            event.setRespawnLocation(loc);
            gp.setSpectator(true);
            if(!shouldAcceptRespawn(event, game, gp)) return;
            player.setGameMode(org.bukkit.GameMode.SPECTATOR);
            gp.getStats().of(RespawnStat.class).increase(player);
            AtomicLong current = new AtomicLong(game.getArena().getGameOptions().getRespawnWaitTime()/20L);
            String task = "respawn::"+player.getName();
            trackTask(game, task, plugin.extension.getTaskHelper().newAsyncTimerTask(() -> {
                if(player.isOnline()) {
                    sendTitle(player, "respawn_title", "respawn_subtitle", new InfoHolder("").inform("current", current.get()).compile());
                    playSound(game, Sound.BLOCK_FENCE_GATE_OPEN);
                    if(current.getAndDecrement() == 0) {
                        cancelTask(game, task);
                        gp.setSpectator(false);
                        plugin.extension.getTaskHelper().newTask(() -> respw(game, player));
                    }
                } else cancelTask(game, task);
            }, 0, 20));
        }
    }

    @Override
    public void onUseWeapon(@NotNull WeaponUseEvent event, @NotNull LocalGame game) {
        if(event.getReport().getEntity() instanceof Player){
            performCooldownMap(game, "spawn_protection",
                cooldownMap -> {
                    long t = game.getArena().getGameOptions().getSpawnProtectionTime();
                    if(!cooldownMap.isPassed((Player) event.getReport().getEntity(), t)) {
                        event.setCancelled(true);
                    }
                });
        }
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
