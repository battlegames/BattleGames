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

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BossbarRenderer implements Runnable {
    private final Map<Player, PlayerBossBar> ACTIVE_1 = new ConcurrentHashMap<>();
    private final Map<Player, PlayerBossBar> ACTIVE_2 = new ConcurrentHashMap<>();

    public void setPrimaryBar(PlayerBossBar bar) {
        PlayerBossBar old = ACTIVE_1.get(bar.getPlayer());
        if (old != null) old.remove();
        ACTIVE_1.put(bar.getPlayer(), bar);
    }

    public void setSecondaryBar(PlayerBossBar bar) {
        PlayerBossBar old = ACTIVE_2.get(bar.getPlayer());
        if (old != null) old.remove();
        ACTIVE_2.put(bar.getPlayer(), bar);
    }

    public void removePrimaryBar(Player player) {
        PlayerBossBar old = ACTIVE_1.remove(player);
        if (old != null) old.remove();
    }

    public void removeSecondaryBar(Player player) {
        PlayerBossBar old = ACTIVE_2.remove(player);
        if (old != null) old.remove();
    }

    @Override
    public void run() {
        ACTIVE_1.values().forEach(PlayerBossBar::render);
        ACTIVE_2.values().forEach(PlayerBossBar::render);
    }
}
