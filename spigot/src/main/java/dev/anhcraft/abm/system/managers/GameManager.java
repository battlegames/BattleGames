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
package dev.anhcraft.abm.system.managers;

import dev.anhcraft.abm.BattleComponent;
import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.BattleGameManager;
import dev.anhcraft.abm.api.BattleModeController;
import dev.anhcraft.abm.api.events.GameJoinEvent;
import dev.anhcraft.abm.api.events.GameQuitEvent;
import dev.anhcraft.abm.api.game.*;
import dev.anhcraft.abm.system.cleaners.GameCleaner;
import dev.anhcraft.abm.system.controllers.CTFController;
import dev.anhcraft.abm.system.controllers.DeathmatchController;
import dev.anhcraft.abm.system.controllers.ModeController;
import dev.anhcraft.abm.system.controllers.TeamDeathmatchController;
import dev.anhcraft.abm.system.integrations.VaultApi;
import dev.anhcraft.abm.utils.PlaceholderUtils;
import dev.anhcraft.jvmkit.utils.Condition;
import dev.anhcraft.jvmkit.utils.MathUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class GameManager extends BattleComponent implements BattleGameManager {
    private final Map<Arena, Game> ARENA_GAME_MAP = new HashMap<>();
    private final Map<UUID, Game> PLAYER_GAME_MAP = new HashMap<>();
    private final Object LOCK = new Object();
    public final GameCleaner cleaner = new GameCleaner();

    public GameManager(BattlePlugin plugin) {
        super(plugin);
        initController(Mode.DEATHMATCH, new DeathmatchController(plugin));
        initController(Mode.TEAM_DEATHMATCH, new TeamDeathmatchController(plugin));
        initController(Mode.CTF, new CTFController(plugin));
    }

    private void initController(Mode mode, ModeController controller) {
        mode.setController(controller);
        Bukkit.getPluginManager().registerEvents(controller, plugin);
    }

    @NotNull
    @Override
    public Optional<GamePlayer> getGamePlayer(@NotNull Player player){
        Condition.argNotNull("player", player);
        synchronized (LOCK) {
            Game x = PLAYER_GAME_MAP.get(player.getUniqueId());
            if(x.isLocal()) return Optional.ofNullable(((LocalGame) getGames()).getPlayer(player));
            else return Optional.empty();
        }
    }

    @NotNull
    @Override
    public Optional<LocalGame> getGame(@NotNull Player player){
        Condition.argNotNull("player", player);
        synchronized (LOCK) {
            return Optional.ofNullable((LocalGame) PLAYER_GAME_MAP.get(player.getUniqueId()));
        }
    }

    @NotNull
    @Override
    public Optional<Game> getGame(@NotNull UUID playerId){
        Condition.argNotNull("playerId", playerId);
        synchronized (LOCK) {
            return Optional.ofNullable(PLAYER_GAME_MAP.get(playerId));
        }
    }

    @NotNull
    @Override
    public Optional<Game> getGame(@NotNull Arena arena){
        Condition.argNotNull("arena", arena);
        return Optional.ofNullable(ARENA_GAME_MAP.get(arena));
    }

    private boolean join(Player player, LocalGame localGame, BattleModeController controller) {
        GamePlayer gp = new GamePlayer(player);
        localGame.getPlayers().put(player, gp);
        PLAYER_GAME_MAP.put(player.getUniqueId(), localGame);
        controller.onJoin(player, localGame);
        Bukkit.getPluginManager().callEvent(new GameJoinEvent(localGame, gp));
        return true;
    }

    @Override
    public boolean join(@NotNull Player player, @NotNull Arena arena){
        Condition.argNotNull("player", player);
        Condition.argNotNull("arena", arena);
        synchronized (LOCK) {
            if (PLAYER_GAME_MAP.containsKey(player.getUniqueId())) {
                plugin.chatManager.sendPlayer(player, "arena.error_already_joined");
                return false;
            }
            Game game = ARENA_GAME_MAP.get(arena);
            if(!game.isLocal()) return false;
            LocalGame localGame = (LocalGame) game;
            if(ARENA_GAME_MAP.containsKey(arena)){
                if (localGame.getPhase() == GamePhase.END || localGame.getPhase() == GamePhase.CLEANING) {
                    plugin.chatManager.sendPlayer(player, "arena.error_attendance_disabled");
                    return false;
                }
                if(localGame.getPlayers().size() == arena.getMaxPlayers()){
                    plugin.chatManager.sendPlayer(player, "arena.error_full_players");
                    return false;
                }
            } else ARENA_GAME_MAP.put(arena, localGame = new LocalGame(arena));
            BattleModeController controller = localGame.getMode().getController();
            if (controller == null) {
                plugin.chatManager.sendPlayer(player, "arena.error_mode_controller_unavailable");
                return false;
            }
            if (!controller.canJoin(player, localGame)) {
                plugin.chatManager.sendPlayer(player, "arena.error_attendance_refused");
                return false;
            }
            return join(player, localGame, controller);
        }
    }

    @Override
    public boolean forceJoin(@NotNull Player player, @NotNull Arena arena) {
        Condition.argNotNull("player", player);
        Condition.argNotNull("arena", arena);
        synchronized (LOCK) {
            if (PLAYER_GAME_MAP.containsKey(player.getUniqueId())) return false;
            Game game = ARENA_GAME_MAP.get(arena);
            if(!game.isLocal()) return false;
            LocalGame localGame = (LocalGame) game;
            if(!ARENA_GAME_MAP.containsKey(arena)) ARENA_GAME_MAP.put(arena, localGame = new LocalGame(arena));
            BattleModeController controller = localGame.getMode().getController();
            if (controller == null) return false;
            return join(player, localGame, controller);
        }
    }

    @Override
    public boolean quit(@NotNull Player player){
        Condition.argNotNull("player", player);
        synchronized (LOCK) {
            Game game = PLAYER_GAME_MAP.get(player.getUniqueId());
            if (!game.isLocal()) return false;
            LocalGame localGame = (LocalGame) game;
            // don't save the player data here!!!
            // plugin.getPlayerData(player);
            localGame.getMode().getController(c -> c.onQuit(player, localGame));
            GamePlayer gp = localGame.getPlayer(player);
            if(gp == null) return false;
            Bukkit.getPluginManager().callEvent(new GameQuitEvent(localGame, gp));
            localGame.getPlayers().remove(player);
            PLAYER_GAME_MAP.remove(player.getUniqueId());
            if(localGame.getPlayerCount().get() == 0) {
                localGame.setPhase(GamePhase.CLEANING);
                cleaner.doClean(localGame.getArena(), ARENA_GAME_MAP::remove);
            }
            return true;
        }
    }

    @Override
    public void destroy(@NotNull LocalGame localGame){
        Condition.argNotNull("game", localGame);
        synchronized (LOCK) {
            localGame.getPlayers().forEach((player, gp) -> {
                Bukkit.getPluginManager().callEvent(new GameQuitEvent(localGame, gp));
                PLAYER_GAME_MAP.remove(player.getUniqueId());
            });
            localGame.setPhase(GamePhase.CLEANING);
            cleaner.doClean(localGame.getArena(), ARENA_GAME_MAP::remove);
        }
    }

    private void runCmd(String s, Player player){
        s = PlaceholderUtils.formatPAPI(player, s);
        s = PlaceholderUtils.formatExpression(s);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s);
    }

    public void rewardAndSaveCache(LocalGame localGame) {
        localGame.getPlayers().values().forEach(gamePlayer -> plugin.getPlayerData(gamePlayer.getPlayer()).ifPresent(playerData -> {
            double m = Math.max(0, localGame.getArena().calculateFinalMoney(gamePlayer));
            long e = Math.max(0, localGame.getArena().calculateFinalExp(gamePlayer));
            VaultApi.getEconomyApi().depositPlayer(gamePlayer.getPlayer(), m);
            playerData.getExp().addAndGet(e);
            plugin.chatManager.sendPlayer(gamePlayer.getPlayer(), "arena.reward_message", s -> s.replace("{__money__}", MathUtil.formatRound(m)).replace("{__exp__}", Long.toString(e)));

            playerData.getKillCounter().addAndGet(gamePlayer.getKillCounter().get());
            playerData.getHeadshotCounter().addAndGet(gamePlayer.getHeadshotCounter().get());
            playerData.getAssistCounter().addAndGet(gamePlayer.getAssistCounter().get());
            playerData.getDeathCounter().addAndGet(gamePlayer.getDeathCounter().get());
            if(gamePlayer.isWinner()) {
                playerData.getWinCounter().incrementAndGet();
                localGame.getArena().getEndCommandWinners().forEach(s -> runCmd(s, gamePlayer.getPlayer()));
            } else {
                playerData.getLoseCounter().incrementAndGet();
                localGame.getArena().getEndCommandLosers().forEach(s -> runCmd(s, gamePlayer.getPlayer()));
            }
        }));
    }

    @NotNull
    @Override
    public Collection<Game> getGames(){
        synchronized (LOCK) {
            return Collections.unmodifiableCollection(ARENA_GAME_MAP.values());
        }
    }
}
