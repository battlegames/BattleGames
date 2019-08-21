package dev.anhcraft.abm.system.renderers.bossbar;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class PlayerBossBar {
    private Player player;
    private BossBar bar;
    private Consumer<PlayerBossBar> callback;

    public PlayerBossBar(Player player, String title, BarColor color, BarStyle style, Consumer<PlayerBossBar> callback) {
        this.player = player;
        this.callback = callback;

        bar = Bukkit.createBossBar(title, color, style);
    }

    public BossBar getBar() {
        return bar;
    }

    public Player getPlayer() {
        return player;
    }

    void render(){
        callback.accept(this);
    }

    void show(){
        bar.addPlayer(player);
        bar.setVisible(true);
    }

    void remove(){
        bar.removeAll();
    }
}
