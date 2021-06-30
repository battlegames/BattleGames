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
package dev.anhcraft.battle.system.managers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multiset;
import dev.anhcraft.battle.BattleComponent;
import dev.anhcraft.battle.BattlePlugin;
import dev.anhcraft.battle.api.Booster;
import dev.anhcraft.battle.api.arena.Arena;
import dev.anhcraft.battle.api.arena.ArenaManager;
import dev.anhcraft.battle.api.arena.game.*;
import dev.anhcraft.battle.api.arena.game.controllers.GameController;
import dev.anhcraft.battle.api.events.game.GameJoinEvent;
import dev.anhcraft.battle.api.events.game.GameQuitEvent;
import dev.anhcraft.battle.api.stats.StatisticMap;
import dev.anhcraft.battle.api.stats.natives.*;
import dev.anhcraft.battle.api.storage.data.PlayerData;
import dev.anhcraft.battle.system.QueueServer;
import dev.anhcraft.battle.system.cleaners.GameCleaner;
import dev.anhcraft.battle.system.controllers.*;
import dev.anhcraft.battle.system.integrations.VaultApi;
import dev.anhcraft.battle.utils.PlaceholderUtil;
import dev.anhcraft.battle.utils.SpeedUtil;
import dev.anhcraft.battle.utils.info.InfoHolder;
import dev.anhcraft.battle.utils.info.InfoReplacer;
import dev.anhcraft.jvmkit.utils.Condition;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class BattleArenaManager extends BattleComponent implements ArenaManager {
    private final Map<Arena, Game> ARENA_GAME_MAP = new ConcurrentHashMap<>();
    private final Map<UUID, LocalGame> PLAYER_GAME_MAP = new HashMap<>();
    private final Object LOCK = new Object();
    private final GameCleaner cleaner;

    public BattleArenaManager(BattlePlugin plugin) {
        super(plugin);
        cleaner = new GameCleaner(plugin);
        initController(Mode.DEATHMATCH, new DMControllerImpl(plugin));
        initController(Mode.BEDWAR, new BWControllerImpl(plugin));
        initController(Mode.TEAM_DEATHMATCH, new TDMControllerImpl(plugin));
        initController(Mode.CTF, new CTFControllerImpl(plugin));
        initController(Mode.MOB_RESCUE, new MRControllerImpl(plugin));
    }

    public void initController(Mode mode, GameControllerImpl controller) {
        mode.setController(controller);
        Bukkit.getPluginManager().registerEvents(controller, plugin);
    }

    @Override
    @Nullable
    public GamePlayer getGamePlayer(@NotNull Player player) {
        Condition.argNotNull("player", player);
        synchronized (LOCK) {
            LocalGame x = PLAYER_GAME_MAP.get(player.getUniqueId());
            return x == null ? null : x.getPlayer(player);
        }
    }

    @Override
    @Nullable
    public LocalGame getGame(@NotNull Player player) {
        Condition.argNotNull("player", player);
        synchronized (LOCK) {
            return PLAYER_GAME_MAP.get(player.getUniqueId());
        }
    }

    @Override
    @Nullable
    public LocalGame getGame(@NotNull UUID playerId) {
        Condition.argNotNull("playerId", playerId);
        synchronized (LOCK) {
            return PLAYER_GAME_MAP.get(playerId);
        }
    }

    @Override
    @Nullable
    public Game getGame(@NotNull Arena arena) {
        Condition.argNotNull("arena", arena);
        return ARENA_GAME_MAP.get(arena);
    }

    private Game join(Player player, LocalGame localGame, GameController controller) {
        GamePlayer gp = controller.makeGamePlayer(player);
        gp.getIgBalance().set(plugin.generalConf.getIgEcoInitBalance());
        localGame.getPlayers().put(player, gp);
        PLAYER_GAME_MAP.put(player.getUniqueId(), localGame);
        SpeedUtil.resetSpeed(player);
        controller.onJoin(player, localGame);
        Bukkit.getPluginManager().callEvent(new GameJoinEvent(localGame, gp));
        return localGame;
    }

    @Override
    @Nullable
    public Game join(@NotNull Player player, @NotNull Arena arena, boolean forceLocal) {
        Condition.argNotNull("player", player);
        Condition.argNotNull("arena", arena);
        synchronized (LOCK) {
            if (PLAYER_GAME_MAP.containsKey(player.getUniqueId())) {
                plugin.chatManager.sendPlayer(player, "arena.error_already_joined");
                return null;
            }
            Game game = ARENA_GAME_MAP.get(arena);
            if (game != null) {
                if (game.getPhase() == GamePhase.END || game.getPhase() == GamePhase.CLEANING) {
                    plugin.chatManager.sendPlayer(player, "arena.error_attendance_disabled");
                    return null;
                }
                if (!arena.isAllowLateJoins() && game.getPhase() == GamePhase.PLAYING) {
                    plugin.chatManager.sendPlayer(player, "arena.error_already_playing");
                    return null;
                }
                if (game.getPlayerCount() == arena.getMaxPlayers()) {
                    plugin.chatManager.sendPlayer(player, "arena.error_full_players");
                    return null;
                }
            } else {
                game = arena.hasBungeecordSupport() && !forceLocal ? new RemoteGame(arena) : new LocalGame(arena);
                GameController controller = arena.getMode().getController();
                if (controller != null) controller.onInitGame(game);
                ARENA_GAME_MAP.put(arena, game);
            }
            if (game instanceof LocalGame) {
                LocalGame localGame = (LocalGame) game;
                GameController controller = localGame.getMode().getController();
                if (controller == null) {
                    plugin.chatManager.sendPlayer(player, "arena.error_mode_controller_unavailable");
                } else if (!controller.canJoin(player, localGame)) {
                    plugin.chatManager.sendPlayer(player, "arena.error_attendance_refused");
                } else {
                    return join(player, localGame, controller);
                }
            } else {
                plugin.queueServerTask.QUEUE.add(new QueueServer(player, arena.getRemoteServers(), arena.getId()));
            }
            return game;
        }
    }

    @Override
    @Nullable
    public Game forceJoin(@NotNull Player player, @NotNull Arena arena, boolean forceLocal) {
        Condition.argNotNull("player", player);
        Condition.argNotNull("arena", arena);
        synchronized (LOCK) {
            if (PLAYER_GAME_MAP.containsKey(player.getUniqueId())) return null;
            Game game = ARENA_GAME_MAP.get(arena);
            if (game == null) {
                game = arena.hasBungeecordSupport() && !forceLocal ? new RemoteGame(arena) : new LocalGame(arena);
                ARENA_GAME_MAP.put(arena, game);
            }
            if (game instanceof LocalGame) {
                LocalGame localGame = (LocalGame) game;
                GameController controller = localGame.getMode().getController();
                if (controller != null)
                    return join(player, localGame, controller);
            } else {
                plugin.queueServerTask.QUEUE.add(new QueueServer(player, arena.getRemoteServers(), arena.getId()));
            }
            return game;
        }
    }

    @Override
    public boolean quit(@NotNull Player player) {
        Condition.argNotNull("player", player);
        synchronized (LOCK) {
            LocalGame game = PLAYER_GAME_MAP.get(player.getUniqueId()); // don't remove instantly! we'll handle later
            if (game == null) return false;
            // don't save the player data here!!!
            // plugin.getPlayerData(player);
            game.getMode().getController(c -> c.onQuit(player, game));
            GamePlayer gp = game.getPlayer(player);
            if (gp == null) return false;
            Bukkit.getPluginManager().callEvent(new GameQuitEvent(game, gp));
            game.getPlayers().remove(player);
            game.getMode().getController(c -> {
                if (c instanceof GameControllerImpl) {
                    ((GameControllerImpl) c).broadcast(game, "player_quit_broadcast", new InfoHolder("").inform("player", player.getName()).compile());
                }
            });
            Multiset<String> servers = game.getDownstreamServers().keys();
            for (String s : servers) {
                if (game.getDownstreamServers().remove(s, player)) {
                    plugin.queueServerTask.QUEUE.add(new QueueServer(player, plugin.generalConf.getBungeeLobbies(), null));
                    break;
                }
            }
            PLAYER_GAME_MAP.remove(player.getUniqueId());
            if (game.getPlayerCount() == 0) {
                if (game.getPhase() == GamePhase.PLAYING || game.getPhase() == GamePhase.END) {
                    game.setPhase(GamePhase.CLEANING);
                    cleaner.newSession(game.getArena(), ARENA_GAME_MAP::remove);
                } else {
                    game.setPhase(GamePhase.END);
                    ARENA_GAME_MAP.remove(game.getArena());
                }
            }
            return true;
        }
    }

    @Override
    public void destroy(@NotNull Game game) {
        Condition.argNotNull("game", game);
        synchronized (LOCK) {
            if (game instanceof LocalGame) {
                if (plugin.bungeeMessenger != null) {
                    plugin.bungeeMessenger.sendGameDestroy((LocalGame) game);
                }
                ((LocalGame) game).getPlayers().forEach((player, gp) -> {
                    Bukkit.getPluginManager().callEvent(new GameQuitEvent(game, gp));
                    PLAYER_GAME_MAP.remove(player.getUniqueId());
                });
                if (game.getPhase() == GamePhase.PLAYING || game.getPhase() == GamePhase.END) {
                    game.setPhase(GamePhase.CLEANING);
                    cleaner.newSession(game.getArena(), ARENA_GAME_MAP::remove);
                    return;
                }
            } else {
                game.setPhase(GamePhase.END);
            }
            ARENA_GAME_MAP.remove(game.getArena());
        }
    }

    private void runCmd(String s, Player player) {
        s = PlaceholderUtil.formatPAPI(player, s);
        s = PlaceholderUtil.formatExpression(s);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s);
    }

    public void handleEnd(LocalGame game) {
        Arena arena = game.getArena();
        for (GamePlayer gp : game.getPlayers().values()) {
            Player p = gp.toBukkit();
            if (plugin.generalConf.shouldHealOnGameEnd()) {
                p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            }
            if (plugin.generalConf.isNoHungryOnGameEnd()) {
                p.setFoodLevel(20);
            }
            plugin.gunManager.handleZoomOut(p);
            PlayerData pd = plugin.getPlayerData(p);
            if (pd != null) {
                double money = Math.max(0, arena.calculateFinalMoney(gp));
                long exp = Math.max(0, arena.calculateFinalExp(gp));
                String activeBooster = pd.getActiveBooster();
                if (activeBooster != null) {
                    Booster booster = plugin.getBooster(activeBooster);
                    Long abd = pd.getBoosters().get(activeBooster);
                    if (abd != null && booster != null) {
                        if (System.currentTimeMillis() - abd <= booster.getExpiryTime() * 50) {
                            money *= booster.getMoneyMultiplier();
                            exp *= booster.getExpMultiplier();
                            if (booster.getMoneyLimit() > 0) {
                                money = Math.min(money, booster.getMoneyLimit());
                            }
                            if (booster.getExpLimit() > 0) {
                                exp = Math.min(exp, booster.getExpLimit());
                            }
                        } else {
                            pd.getBoosters().remove(activeBooster);
                        }
                    }
                }
                VaultApi.getEconomyApi().depositPlayer(p, money);
                pd.getStats().of(ExpStat.class).increase(p, exp);

                StatisticMap sm = pd.getStats();
                sm.of(KillStat.class).increase(p, gp.getStats().of(KillStat.class).get());
                sm.of(HeadshotStat.class).increase(p, gp.getStats().of(HeadshotStat.class).get());
                sm.of(AssistStat.class).increase(p, gp.getStats().of(AssistStat.class).get());
                sm.of(DeathStat.class).increase(p, gp.getStats().of(DeathStat.class).get());
                sm.of(RespawnStat.class).increase(p, gp.getStats().of(RespawnStat.class).get());
                sm.of(StolenMobStat.class).increase(p, gp.getStats().of(StolenMobStat.class).get());
                if (gp.hasFirstKill()) sm.of(FirstKillStat.class).increase(p);
                InfoReplacer infoReplacer = new InfoHolder("").inform("money", money).inform("exp", exp).compile();
                if (gp.isWinner()) {
                    if (arena.getWonReport() != null) {
                        for (String s : arena.getWonReport()) {
                            p.sendMessage(infoReplacer.replace(s));
                        }
                    }
                    sm.of(WinStat.class).increase(p);
                    if (arena.getEndCommandWinners() != null) {
                        for (String s : arena.getEndCommandWinners()) {
                            runCmd(s, p);
                        }
                    }
                    if (arena.getEndFirework() != null) arena.getEndFirework().spawn(p.getLocation());
                } else {
                    if (arena.getLostReport() != null) {
                        for (String s : arena.getLostReport()) {
                            p.sendMessage(infoReplacer.replace(s));
                        }
                    }
                    sm.of(LoseStat.class).increase(p);
                    if (arena.getEndCommandLosers() != null) {
                        for (String s : arena.getEndCommandLosers()) {
                            runCmd(s, p);
                        }
                    }
                }
            }
        }

        long ed = arena.getEndDelay();
        if (ed <= 0) plugin.arenaManager.destroy(game);
        else plugin.getServer().getScheduler().runTaskLater(plugin, () -> plugin.arenaManager.destroy(game), ed);
    }

    @NotNull
    @Override
    public List<Game> listGames() {
        return ImmutableList.copyOf(ARENA_GAME_MAP.values());
    }

    @Override
    public void listGames(@NotNull Consumer<Game> consumer) {
        Condition.argNotNull("consumer", consumer);
        ARENA_GAME_MAP.values().forEach(consumer);
    }
}
