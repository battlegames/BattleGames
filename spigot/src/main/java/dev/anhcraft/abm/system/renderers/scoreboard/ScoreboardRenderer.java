package dev.anhcraft.abm.system.renderers.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ScoreboardRenderer implements Runnable {
    private final Map<Player, PlayerScoreboard> ACTIVE = new ConcurrentHashMap<>();

    public void setScoreboard(PlayerScoreboard newScoreboard){
        PlayerScoreboard currentScoreboard = ACTIVE.get(newScoreboard.getPlayer());
        if(currentScoreboard != null) currentScoreboard.remove();
        ACTIVE.put(newScoreboard.getPlayer(), newScoreboard);
        newScoreboard.show();
    }

    public void removeScoreboard(Player player){
        PlayerScoreboard currentScoreboard = ACTIVE.remove(player);
        if(currentScoreboard != null) currentScoreboard.remove();
        player.setScoreboard(Objects.requireNonNull(Bukkit.getScoreboardManager())
                .getMainScoreboard());
    }

    @Override
    public void run() {
        ACTIVE.values().forEach(PlayerScoreboard::render);
    }
}
