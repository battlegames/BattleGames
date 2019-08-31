package dev.anhcraft.abm.system.controllers;

import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.events.GamePlayerDamageEvent;
import dev.anhcraft.abm.api.events.ItemChooseEvent;
import dev.anhcraft.abm.api.game.*;
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
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.ArrayList;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class TeamDeathmatchController extends ModeController {
    private final Map<Game, SimpleTeam<DeathmatchTeam>> TEAM = new ConcurrentHashMap<>();

    @Override
    public void onDeath(PlayerDeathEvent event, Game game){
        plugin.taskManager.newTask(() -> {
            event.getEntity().getInventory().setItem(0, null);
            event.getEntity().spigot().respawn();
        });
    }

    public TeamDeathmatchController(BattlePlugin plugin) {
        super(plugin, Mode.TEAM_DEATHMATCH);

        plugin.getPapiExpansion().handlers.put("tdm_team", player -> plugin.gameManager.getGame(player).map(game -> {
            SimpleTeam<DeathmatchTeam> t = TEAM.get(game);
            if(t == null) return null;
            DeathmatchTeam dt = t.getTeam(player);
            if(dt == null) return null;
            return plugin.getLocaleConf().getString("mode_tdm."+dt.name().toLowerCase());
        }).orElse(null));

        plugin.getPapiExpansion().handlers.put("tdm_team_players", player -> plugin.gameManager.getGame(player).map(game -> {
            SimpleTeam<DeathmatchTeam> t = TEAM.get(game);
            if(t == null) return null;
            DeathmatchTeam dt = t.getTeam(player);
            if(dt == null) return null;
            return Integer.toString(t.countPlayers(dt));
        }).orElse(null));
    }

    @Override
    public void onTask(Game game){
        SimpleTeam<DeathmatchTeam> x = TEAM.get(game);
        if(x != null) {
            int a = x.countPlayers(DeathmatchTeam.TEAM_A);
            int b = x.countPlayers(DeathmatchTeam.TEAM_B);
            if (a == 0 || b == 0) game.end();
        }
    }

    @Override
    public void onQuit(Player player, Game game){
        broadcast(game, "mode_tdm.player_quit_broadcast",
                s -> s.replace("{__target__}", player.getDisplayName()));
    }

    private DeathmatchTeam findTeam(Game game) {
        SimpleTeam<DeathmatchTeam> x = TEAM.get(game);
        int a = x.countPlayers(DeathmatchTeam.TEAM_A);
        int b = x.countPlayers(DeathmatchTeam.TEAM_B);
        return a <= b ? DeathmatchTeam.TEAM_A : DeathmatchTeam.TEAM_B;
    }

    @Override
    public void onJoin(Player player, Game game) {
        broadcast(game, "mode_tdm.player_join_broadcast",
                s -> s.replace("{__target__}", player.getDisplayName()));
        int m = Math.min(game.getArena().getAttributes().getInt("min_players"), 1);
        switch (game.getPhase()){
            case WAITING:{
                respw(game, player, null);
                String title = game.getMode().getWaitingScoreboardTitle();
                List<String> content = game.getMode().getWaitingScoreboardContent();
                boolean b = game.getMode().isWaitingScoreboardFixedLength();
                plugin.scoreboardRenderer.setScoreboard(new PlayerScoreboard(player, title, content, b));
                if(m <= game.countPlayers()) countdown(game);
                break;
            }
            case PLAYING: {
                DeathmatchTeam t = findTeam(game);
                TEAM.get(game).addPlayer(player, t);
                addPlayer(game, player, t);
            }
        }
    }

    private void countdown(Game game) {
        if(hasTask(game, "countdown")) return;
        AtomicLong current = new AtomicLong(game.getArena().getAttributes().getLong("countdown_time")/20L);
        int m = game.getArena().getAttributes().getInt("min_players");
        trackTask(game, "countdown", plugin.taskManager.newAsyncTimerTask(() -> {
            if(m <= game.countPlayers()) {
                broadcastTitle(game, "mode_tdm.countdown_title", "mode_tdm.countdown_subtitle", s -> s.replace("{__current__}", current.toString()));
                playSound(game, Sound.BLOCK_FENCE_GATE_OPEN);
                if(current.getAndDecrement() == 0) {
                    cancelTask(game, "countdown");
                    play(game);
                }
            } else cancelTask(game, "countdown");
        }, 0, 20));
    }

    private void play(Game game) {
        broadcast(game,"mode_tdm.game_start_broadcast");

        List<Player> x = new ArrayList<>(game.getPlayers().keySet());
        int sz = Math.floorDiv(x.size(), 2);
        List<Player> ta = x.subList(0, sz);
        List<Player> tb = x.subList(sz, x.size());

        SimpleTeam<DeathmatchTeam> team = new SimpleTeam<>();
        team.addPlayers(ta, DeathmatchTeam.TEAM_A);
        team.addPlayers(tb, DeathmatchTeam.TEAM_B);
        TEAM.put(game, team);

        plugin.taskManager.newTask(() -> {
            game.setPhase(GamePhase.PLAYING);
            ta.forEach(p -> {
                cancelTask(game, "respawn::"+p.getName());
                addPlayer(game, p, DeathmatchTeam.TEAM_A);
            });
            tb.forEach(p -> {
                cancelTask(game, "respawn::"+p.getName());
                addPlayer(game, p, DeathmatchTeam.TEAM_B);
            });
        });
    }

    private void addPlayer(Game game, Player player, DeathmatchTeam dt) {
        String title = game.getMode().getPlayingScoreboardTitle();
        List<String> content = game.getMode().getPlayingScoreboardContent();
        boolean b = game.getMode().isPlayingScoreboardFixedLength();
        plugin.scoreboardRenderer.setScoreboard(new PlayerScoreboard(player, title, content, b));
        // TODO ADD SPERATE SCOREBOARD TO HIDE NAMETAG HERE
        respw(game, player, dt);
    }

    private void respw(Game game, Player player, DeathmatchTeam dt) {
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
                        .getStringList("playing_spawn_points_"+ (dt == DeathmatchTeam.TEAM_A ? "a" : "b")));
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
                plugin.chatManager.sendPlayer(event.getPlayer(), "mode_tdm.error_item_selection_overtime");
            else {
                if (event.getItemModel().getItemType() == ItemType.GUN)
                    plugin.getHandler(GunHandler.class).selectGun(event.getPlayer(), (GunModel) event.getItemModel());
                else
                    plugin.chatManager.sendPlayer(event.getPlayer(), "mode_tdm.error_disabled_item_type");
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
            trackTask(game, task, plugin.taskManager.newAsyncTimerTask(() -> {
                if(player.isOnline()) {
                    sendTitle(game, player, "mode_tdm.respawn_title", "mode_tdm.respawn_subtitle", s -> s.replace("{__current__}", current.toString()));
                    playSound(game, Sound.BLOCK_FENCE_GATE_OPEN);
                    if(current.getAndDecrement() == 0) {
                        cancelTask(game, task);
                        gp.setSpectator(false);
                        plugin.taskManager.newTask(() -> respw(game, player, TEAM.get(game).getTeam(player)));
                    }
                } else cancelTask(game, task);
            }, 0, 20));
        }
    }

    @EventHandler
    public void damage(GamePlayerDamageEvent e) {
        if(e.getGame().getMode() != getMode()) return;
        SimpleTeam<DeathmatchTeam> x = TEAM.get(e.getGame());
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
        SimpleTeam<DeathmatchTeam> team = TEAM.remove(game);

        Map<DeathmatchTeam, List<GamePlayer>> map = team.reverse(game::getPlayer);
        List<GamePlayer> aPlayers = map.get(DeathmatchTeam.TEAM_A);
        List<GamePlayer> bPlayers = map.get(DeathmatchTeam.TEAM_B);
        if(aPlayers != null & bPlayers != null) {
            IntSummaryStatistics sa = aPlayers.stream().mapToInt(value -> {
                respw(game, value.getPlayer(), DeathmatchTeam.TEAM_A);
                value.setSpectator(false);
                return value.getKillCounter().get();
            }).summaryStatistics();
            IntSummaryStatistics sb = bPlayers.stream().mapToInt(value -> {
                respw(game, value.getPlayer(), DeathmatchTeam.TEAM_B);
                value.setSpectator(false);
                return value.getKillCounter().get();
            }).summaryStatistics();
            DeathmatchTeam winner;
            if (sa.getSum() == sb.getSum())
                winner = sa.getAverage() > sb.getAverage() ? DeathmatchTeam.TEAM_A : DeathmatchTeam.TEAM_B;
            else
                winner = sa.getSum() > sb.getSum() ? DeathmatchTeam.TEAM_A : DeathmatchTeam.TEAM_B;
            map.get(winner).forEach(player -> player.setWinner(true));
            team.reset();
        }

        plugin.gameManager.rewardAndSaveCache(game);
        plugin.gameManager.destroy(game);
    }
}
