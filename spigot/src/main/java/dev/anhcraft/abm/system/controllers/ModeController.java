package dev.anhcraft.abm.system.controllers;

import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.enums.Mode;
import dev.anhcraft.abm.api.ext.BattleComponent;
import dev.anhcraft.abm.api.objects.Game;
import dev.anhcraft.abm.utils.CooldownMap;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class ModeController extends BattleComponent implements Listener {
    private final Map<String, Integer> RUNNING_TASKS = Collections.synchronizedMap(new HashMap<>()); // fix bug!
    private final Map<String, CooldownMap> COOLDOWN = new ConcurrentHashMap<>();
    private Mode mode;

    ModeController(BattlePlugin plugin, Mode mode) {
        super(plugin);
        this.mode = mode;
    }

    public boolean canJoin(Player player, Game game){
        return true;
    }
    public abstract void onJoin(Player player, Game game);
    public void onQuit(Player player, Game game){}
    public void onRespawn(PlayerRespawnEvent event, Game game){}
    public void onTask(Game game){}

    void broadcast(Game game, String localePath){
        game.getPlayers().keySet().forEach(player -> plugin.chatProvider.sendPlayer(player, localePath));
    }

    void broadcast(Game game, String localePath, Function<String, String> x){
        game.getPlayers().keySet().forEach(player -> plugin.chatProvider.sendPlayer(player, localePath, x));
    }

    void broadcast(Game game, String localePath, ChatMessageType type){
        game.getPlayers().keySet().forEach(player -> plugin.chatProvider.sendPlayer(player, localePath, type));
    }

    void broadcast(Game game, String localePath, ChatMessageType type, Function<String, String> x){
        game.getPlayers().keySet().forEach(player -> plugin.chatProvider.sendPlayer(player, localePath, type, x));
    }

    void broadcastTitle(Game game, String titleLocalePath, String subtitleLocalePath){
        game.getPlayers().keySet().forEach(player -> plugin.titleProvider.send(player, titleLocalePath, subtitleLocalePath));
    }

    void broadcastTitle(Game game, String titleLocalePath, String subtitleLocalePath, Function<String, String> x){
        game.getPlayers().keySet().forEach(player -> plugin.titleProvider.send(player, titleLocalePath, subtitleLocalePath, x));
    }

    void sendTitle(Game game, Player player, String titleLocalePath, String subtitleLocalePath, Function<String, String> x){
        plugin.titleProvider.send(player, titleLocalePath, subtitleLocalePath, x);
    }

    void trackTask(Game game, String id, int task){
        RUNNING_TASKS.put(game.getArena().getId()+id, task);
    }

    boolean hasTask(Game game, String id){
        return RUNNING_TASKS.containsKey(game.getArena().getId()+id);
    }

    void cancelTask(Game game, String id){
        Integer x = RUNNING_TASKS.remove(game.getArena().getId()+id);
        if(x != null) plugin.taskManager.cancelTask(x);
    }

    void cancelAllTasks(Game game){
        List<Map.Entry<String, Integer>> x = RUNNING_TASKS.entrySet().stream()
                .filter(e -> e.getKey().startsWith(game.getArena().getId()))
                .collect(Collectors.toList());
        x.forEach(e -> plugin.taskManager.cancelTask(RUNNING_TASKS.remove(e.getKey())));
    }

    void playSound(Game game, Sound sound){
        game.getPlayers().keySet().forEach(p -> p.playSound(p.getLocation(), sound, 3f, 1f));
    }

    void playSound(Game game, String sound){
        game.getPlayers().keySet().forEach(p -> p.playSound(p.getLocation(), sound, 3f, 1f));
    }

    @Nullable
    CooldownMap getCooldownMap(Game game, String id){
        return COOLDOWN.get(game.getArena().getId()+id);
    }

    void clearCooldown(){
        COOLDOWN.clear();
    }

    void performCooldownMap(Game game, String id, Consumer<CooldownMap> ifPresent){
        String k = game.getArena().getId()+id;
        CooldownMap m = COOLDOWN.get(k);
        if(m != null) ifPresent.accept(m);
    }

    void performCooldownMap(Game game, String id, Consumer<CooldownMap> ifPresent, Callable<CooldownMap> otherwise){
        String k = game.getArena().getId()+id;
        CooldownMap m = COOLDOWN.get(k);
        if(m == null) {
            try {
                COOLDOWN.put(k, otherwise.call());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else ifPresent.accept(m);
    }

    @NotNull
    public Mode getMode() {
        return mode;
    }
}
