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
package dev.anhcraft.battle.system.renderers.bossbar;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class PlayerBossBar {
    private final Player player;
    private final BossBar bar;
    private final Consumer<PlayerBossBar> callback;
    private boolean isShow;

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

    void render() {
        callback.accept(this);
    }

    public void show() {
        if (isShow) return;
        isShow = true;
        bar.addPlayer(player);
        bar.setVisible(true);
    }

    void remove() {
        bar.removeAll();
    }
}
