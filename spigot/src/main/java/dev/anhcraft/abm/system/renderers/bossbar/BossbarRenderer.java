package dev.anhcraft.abm.system.renderers.bossbar;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BossbarRenderer implements Runnable {
    private final Map<Player, PlayerBossbar> ACTIVE_1 = new ConcurrentHashMap<>();
    private final Map<Player, PlayerBossbar> ACTIVE_2 = new ConcurrentHashMap<>();

    public void setPrimaryBar(PlayerBossbar bar){
        PlayerBossbar old = ACTIVE_1.get(bar.getPlayer());
        if(old != null) old.remove();
        ACTIVE_1.put(bar.getPlayer(), bar);
        bar.show();
    }

    public void setSecondaryBar(PlayerBossbar bar){
        PlayerBossbar old = ACTIVE_2.get(bar.getPlayer());
        if(old != null) old.remove();
        ACTIVE_2.put(bar.getPlayer(), bar);
        bar.show();
    }

    public void removePrimaryBar(PlayerBossbar bar){
        PlayerBossbar old = ACTIVE_1.remove(bar.getPlayer());
        if(old != null) old.remove();
    }

    public void removeSecondaryBar(PlayerBossbar bar){
        PlayerBossbar old = ACTIVE_1.remove(bar.getPlayer());
        if(old != null) old.remove();
    }

    @Override
    public void run() {
        ACTIVE_1.values().forEach(PlayerBossbar::render);
        ACTIVE_2.values().forEach(PlayerBossbar::render);
    }
}
