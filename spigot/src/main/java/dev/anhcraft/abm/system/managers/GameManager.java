package dev.anhcraft.abm.system.managers;

import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.enums.GamePhase;
import dev.anhcraft.abm.api.enums.Mode;
import dev.anhcraft.abm.api.events.GameJoinEvent;
import dev.anhcraft.abm.api.events.GameQuitEvent;
import dev.anhcraft.abm.api.ext.BattleComponent;
import dev.anhcraft.abm.api.impl.BattleGameManager;
import dev.anhcraft.abm.api.objects.Arena;
import dev.anhcraft.abm.api.objects.Game;
import dev.anhcraft.abm.api.objects.GamePlayer;
import dev.anhcraft.abm.system.cleaners.GameCleaner;
import dev.anhcraft.abm.system.controllers.DeathmatchController;
import dev.anhcraft.abm.system.controllers.ModeController;
import dev.anhcraft.abm.system.controllers.TeamDeathmatchController;
import dev.anhcraft.abm.system.integrations.VaultApi;
import dev.anhcraft.jvmkit.utils.MathUtil;
import dev.anhcraft.abm.utils.PlaceholderUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class GameManager extends BattleComponent implements BattleGameManager {
    private final Map<Arena, Game> ARENA_GAME_MAP = new HashMap<>();
    private final Map<Player, Game> PLAYER_GAME_MAP = new HashMap<>();
    private final Object LOCK = new Object();
    private final GameCleaner cleaner = new GameCleaner();

    public GameManager(BattlePlugin plugin) {
        super(plugin);
        initController(Mode.DEATHMATCH, new DeathmatchController(plugin));
        initController(Mode.TEAM_DEATHMATCH, new TeamDeathmatchController(plugin));
    }

    private void initController(Mode mode, ModeController controller) {
        mode.setController(controller);
        Bukkit.getPluginManager().registerEvents(controller, plugin);
    }

    @Override
    public Optional<GamePlayer> getGamePlayer(Player player){
        synchronized (LOCK) {
            Game x = PLAYER_GAME_MAP.get(player);
            return Optional.ofNullable(x == null ? null : x.getPlayer(player));
        }
    }

    @Override
    public Optional<Game> getGame(Player player){
        synchronized (LOCK) {
            return Optional.ofNullable(PLAYER_GAME_MAP.get(player));
        }
    }

    @Override
    public Optional<Game> getGame(Arena arena){
        return Optional.ofNullable(ARENA_GAME_MAP.get(arena));
    }

    private boolean join(Player player, Game game, ModeController controller) {
        GamePlayer gp = new GamePlayer(player);
        game.getPlayers().put(player, gp);
        PLAYER_GAME_MAP.put(player, game);
        controller.onJoin(player, game);
        Bukkit.getPluginManager().callEvent(new GameJoinEvent(gp, game));
        return true;
    }

    @Override
    public boolean join(Player player, Arena arena){
        synchronized (LOCK) {
            if (PLAYER_GAME_MAP.containsKey(player)) {
                plugin.chatProvider.sendPlayer(player, "arena.error_already_joined");
                return false;
            }
            Game game = ARENA_GAME_MAP.get(arena);
            if(ARENA_GAME_MAP.containsKey(arena)){
                if (game.getPhase() == GamePhase.END || game.getPhase() == GamePhase.CLEANING) {
                    plugin.chatProvider.sendPlayer(player, "arena.error_attendance_disabled");
                    return false;
                }
                if(game.getPlayers().size() == arena.getMaxPlayers()){
                    plugin.chatProvider.sendPlayer(player, "arena.error_full_players");
                    return false;
                }
            } else ARENA_GAME_MAP.put(arena, game = new Game(arena));
            ModeController controller = game.getMode().getController();
            if (controller == null) {
                plugin.chatProvider.sendPlayer(player, "arena.error_mode_controller_unavailable");
                return false;
            }
            if (!controller.canJoin(player, game)) {
                plugin.chatProvider.sendPlayer(player, "arena.error_attendance_refused");
                return false;
            }
            return join(player, game, controller);
        }
    }

    @Override
    public boolean forceJoin(Player player, Arena arena) {
        synchronized (LOCK) {
            if (PLAYER_GAME_MAP.containsKey(player)) return false;
            Game game = ARENA_GAME_MAP.get(arena);
            if(!ARENA_GAME_MAP.containsKey(arena)) ARENA_GAME_MAP.put(arena, game = new Game(arena));
            ModeController controller = game.getMode().getController();
            if (controller == null) return false;
            return join(player, game, controller);
        }
    }

    @Override
    public boolean quit(Player player){
        synchronized (LOCK) {
            Game game = PLAYER_GAME_MAP.get(player);
            if (game == null) return false;
            // don't save the player data here!!!
            // plugin.getPlayerData(player);
            Objects.requireNonNull(game.getMode().getController()).onQuit(player, game);
            Bukkit.getPluginManager().callEvent(new GameQuitEvent(game.getPlayer(player), game));
            game.getPlayers().remove(player);
            PLAYER_GAME_MAP.remove(player);
            if(game.countPlayers() == 0) cleaner.doClean(game.getArena(), ARENA_GAME_MAP::remove);
            return true;
        }
    }

    @Override
    public void destroy(Game game){
        synchronized (LOCK) {
            game.getPlayers().forEach((player, gp) -> {
                Bukkit.getPluginManager().callEvent(new GameQuitEvent(gp, game));
                PLAYER_GAME_MAP.remove(player);
            });
            game.setPhase(GamePhase.CLEANING);
            cleaner.doClean(game.getArena(), ARENA_GAME_MAP::remove);
        }
    }

    private void runCmd(String s, Player player){
        s = PlaceholderUtils.formatPAPI(player, s);
        s = PlaceholderUtils.formatExpression(s);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s);
    }

    public void rewardAndSaveCache(Game game) {
        game.getPlayers().values().forEach(gamePlayer -> plugin.getPlayerData(gamePlayer.getPlayer()).ifPresent(playerData -> {
            double m = Math.max(0, game.getArena().calculateFinalMoney(gamePlayer));
            long e = Math.max(0, game.getArena().calculateFinalExp(gamePlayer));
            VaultApi.getEconomyApi().depositPlayer(gamePlayer.getPlayer(), m);
            playerData.getExp().addAndGet(e);
            plugin.chatProvider.sendPlayer(gamePlayer.getPlayer(), "arena.reward_message", s -> s.replace("{__money__}", MathUtil.formatRound(m)).replace("{__exp__}", Long.toString(e)));

            playerData.getKillCounter().addAndGet(gamePlayer.getKillCounter().get());
            playerData.getHeadshotCounter().addAndGet(gamePlayer.getHeadshotCounter().get());
            playerData.getAssistCounter().addAndGet(gamePlayer.getAssistCounter().get());
            playerData.getDeathCounter().addAndGet(gamePlayer.getDeathCounter().get());
            if(gamePlayer.isWinner()) {
                playerData.getWinCounter().incrementAndGet();
                game.getArena().getEndCommandWinners().forEach(s -> runCmd(s, gamePlayer.getPlayer()));
            } else {
                playerData.getLoseCounter().incrementAndGet();
                game.getArena().getEndCommandLosers().forEach(s -> runCmd(s, gamePlayer.getPlayer()));
            }
        }));
    }

    @Override
    public Collection<Game> getGames(){
        synchronized (LOCK) {
            return Collections.unmodifiableCollection(ARENA_GAME_MAP.values());
        }
    }
}
