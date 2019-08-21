package dev.anhcraft.abm.system.renderers.bossbar;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BossbarRenderer implements Runnable {
    private final Map<Player, PlayerBossBar> ACTIVE_1 = new ConcurrentHashMap<>();
    private final Map<Player, PlayerBossBar> ACTIVE_2 = new ConcurrentHashMap<>();

    public void setPrimaryBar(PlayerBossBar bar){
        PlayerBossBar old = ACTIVE_1.get(bar.getPlayer());
        if(old != null) old.remove();
        ACTIVE_1.put(bar.getPlayer(), bar);
        bar.show();
    }

    public void setSecondaryBar(PlayerBossBar bar){
        PlayerBossBar old = ACTIVE_2.get(bar.getPlayer());
        if(old != null) old.remove();
        ACTIVE_2.put(bar.getPlayer(), bar);
        bar.show();
    }

    public void removePrimaryBar(PlayerBossBar bar){
        PlayerBossBar old = ACTIVE_1.remove(bar.getPlayer());
        if(old != null) old.remove();
    }

    public void removeSecondaryBar(PlayerBossBar bar){
        PlayerBossBar old = ACTIVE_1.remove(bar.getPlayer());
        if(old != null) old.remove();
    }

    @Override
    public void run() {
        ACTIVE_1.values().forEach(PlayerBossBar::render);
        ACTIVE_2.values().forEach(PlayerBossBar::render);
    }
}
