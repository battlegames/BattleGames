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
package dev.anhcraft.battle.system.renderers.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ScoreboardRenderer implements Runnable {
    private final Map<Player, PlayerScoreboard> ACTIVE = new ConcurrentHashMap<>();

    @Nullable
    public PlayerScoreboard getScoreboard(Player player){
        return ACTIVE.get(player);
    }

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
