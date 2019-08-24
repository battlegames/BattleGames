package dev.anhcraft.abm.system.controllers;

import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.events.GameEndEvent;
import dev.anhcraft.abm.api.events.GamePlayerDamageEvent;
import dev.anhcraft.abm.api.events.ItemChooseEvent;
import dev.anhcraft.abm.api.game.Game;
import dev.anhcraft.abm.api.game.GamePhase;
import dev.anhcraft.abm.api.game.GamePlayer;
import dev.anhcraft.abm.api.game.Mode;
import dev.anhcraft.abm.api.inventory.items.*;
import dev.anhcraft.abm.system.handlers.GunHandler;
import dev.anhcraft.abm.system.renderers.scoreboard.PlayerScoreboard;
import dev.anhcraft.abm.utils.CooldownMap;
import dev.anhcraft.abm.utils.LocationUtil;
import dev.anhcraft.jvmkit.utils.RandomUtil;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class DeathmatchController extends ModeController {
    public DeathmatchController(BattlePlugin plugin) {
        super(plugin, Mode.DEATHMATCH);
    }

    @Override
    public void onDeath(PlayerDeathEvent event, Game game){
        plugin.taskManager.newTask(() -> {
            event.getEntity().getInventory().setItem(0, null);
            event.getEntity().spigot().respawn();
        });
    }

    @Override
    public void onQuit(Player player, Game game){
        broadcast(game, "mode_dm.player_quit_broadcast",
                s -> s.replace("{__target__}", player.getDisplayName()));
    }

    @Override
    public void onJoin(Player player, Game game) {
        broadcast(game, "mode_dm.player_join_broadcast",
                s -> s.replace("{__target__}", player.getDisplayName()));
        int m = game.getArena().getAttributes().getInt("min_players");
        switch (game.getPhase()){
            case WAITING:{
                respw(game, player);
                String title = game.getMode().getWaitingScoreboardTitle();
                List<String> content = game.getMode().getWaitingScoreboardContent();
                plugin.scoreboardRenderer.setScoreboard(new PlayerScoreboard(player, title, content));
                if(m <= game.countPlayers()) countdown(game);
                break;
            }
            case PLAYING: addPlayer(game, player);
        }
    }

    private void countdown(Game game) {
        if(hasTask(game, "countdown")) return;
        AtomicLong current = new AtomicLong(game.getArena().getAttributes().getLong("countdown_time")/20L);
        int m = Math.min(game.getArena().getAttributes().getInt("min_players"), 1);
        trackTask(game, "countdown", plugin.taskManager.newAsyncTimerTask(() -> {
            if(m <= game.countPlayers()) {
                broadcastTitle(game, "mode_dm.countdown_title", "mode_dm.countdown_subtitle", s -> s.replace("{__current__}", current.toString()));
                playSound(game, Sound.BLOCK_FENCE_GATE_OPEN);
                if(current.getAndDecrement() == 0) {
                    cancelTask(game, "countdown");
                    play(game);
                }
            } else cancelTask(game, "countdown");
        }, 0, 20));
    }

    private void play(Game game) {
        broadcast(game,"mode_dm.game_start_broadcast");
        plugin.taskManager.newTask(() -> {
            game.setPhase(GamePhase.PLAYING);
            game.getPlayers().values().forEach(p -> {
                cancelTask(game, "respawn::"+p.getPlayer().getName());
                addPlayer(game, p.getPlayer());
            });
        });
    }

    private void addPlayer(Game game, Player player) {
        String title = game.getMode().getPlayingScoreboardTitle();
        List<String> content = game.getMode().getPlayingScoreboardContent();
        plugin.scoreboardRenderer.setScoreboard(new PlayerScoreboard(player, title, content));
        respw(game, player);
    }

    private void respw(Game game, Player player) {
        player.setGameMode(GameMode.SURVIVAL);
        switch (game.getPhase()) {
            case END:
            case WAITING: {
                String loc = RandomUtil.pickRandom(game.getArena().getAttributes()
                        .getStringList("waiting_spawn_points"));
                player.teleport(LocationUtil.fromString(loc));
                break;
            }
            case PLAYING: {
                String loc = RandomUtil.pickRandom(game.getArena().getAttributes()
                        .getStringList("playing_spawn_points"));
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
    public void choose(ItemChooseEvent event){
        Player player = event.getPlayer();
        plugin.gameManager.getGame(player).ifPresent(game -> {
            if(game.getMode() != getMode() && game.getPhase() != GamePhase.PLAYING) return;
            performCooldownMap(game, "item_selection", cooldownMap -> {
                int t = game.getArena().getAttributes().getInt("item_selection_time");
                if(cooldownMap.isPassed(player, t)) plugin.chatManager.sendPlayer(player, "mode_dm.error_item_selection_overtime");
                else {
                    if (event.getItemModel().getItemType() == ItemType.GUN) {
                        plugin.getHandler(GunHandler.class).selectGun(player, (GunModel) event.getItemModel());
                    } else {
                        plugin.chatManager.sendPlayer(player, "mode_dm.error_disabled_item_type");
                    }
                }
            });
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
            trackTask(game, task, plugin.taskManager.newAsyncTimerTask(() -> {
                if(player.isOnline()) {
                    sendTitle(game, player, "mode_dm.respawn_title", "mode_dm.respawn_subtitle", s -> s.replace("{__current__}", current.toString()));
                    playSound(game, Sound.BLOCK_FENCE_GATE_OPEN);
                    if(current.getAndDecrement() == 0) {
                        cancelTask(game, task);
                        gp.setSpectator(false);
                        plugin.taskManager.newTask(() -> respw(game, player));
                    }
                } else cancelTask(game, task);
            }, 0, 20));
        }
    }

    @EventHandler
    public void drop(PlayerDropItemEvent e) {
        Player p = e.getPlayer();
        plugin.gameManager.getGame(p).ifPresent(game -> {
            if (game.getMode() != getMode()) return;
            BattleItem item = plugin.itemManager.read(e.getItemDrop().getItemStack());
            if(item instanceof Gun) e.setCancelled(true);
        });
    }

    @EventHandler
    public void reload(PlayerSwapHandItemsEvent e) {
        Player p = e.getPlayer();
        plugin.gameManager.getGame(p).ifPresent(game -> {
            if(game.getMode() != getMode()) return;
            BattleItem item = plugin.itemManager.read(e.getOffHandItem());
            if(item instanceof Gun){
                Gun gun = (Gun) item;
                e.setCancelled(true);
                doReloadGun(p, gun);
            }
        });
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

    @EventHandler
    public void end(GameEndEvent event){
        Game game = event.getGame();
        if(game.getMode() != getMode()) return;
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
