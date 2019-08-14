package dev.anhcraft.abm.system.renderers.bossbar;

import dev.anhcraft.abm.utils.PlaceholderUtils;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class PlayerBossbar {
    private Player player;
    private BossBar bar;
    private String title;
    private BarFlag[] flags;
    private Consumer<PlayerBossbar> callback;

    public PlayerBossbar(Player player, String title, double value, BarColor color, BarStyle style, Consumer<PlayerBossbar> callback, BarFlag... flags) {
        this.player = player;
        this.title = title;
        this.callback = callback;
        this.flags = flags;

        bar = Bukkit.createBossBar(PlaceholderUtils.formatPAPI(player, title), color, style, flags);
        bar.setProgress(value);
    }

    public Player getPlayer() {
        return player;
    }

    public void setTitle(String title) {
        bar.setTitle(PlaceholderUtils.formatPAPI(player, title));
    }

    public void setColor(BarColor color) {
        bar.setColor(color);
    }

    public void setStyle(BarStyle style) {
        bar.setStyle(style);
    }

    public void setFlags(BarFlag[] flags) {
        for (BarFlag f : this.flags) bar.removeFlag(f);
        for (BarFlag f : flags) bar.addFlag(f);
        this.flags = flags;
    }

    public void setValue(double value) {
        if(bar != null) bar.setProgress(value);
    }

    void setBar(BossBar bar) {
        this.bar = bar;
    }

    void render(){
        bar.setTitle(PlaceholderUtils.formatPAPI(player, title));
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
