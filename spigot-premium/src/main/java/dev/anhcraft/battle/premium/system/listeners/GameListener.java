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

package dev.anhcraft.battle.premium.system.listeners;

import dev.anhcraft.battle.api.arena.game.GamePhase;
import dev.anhcraft.battle.api.events.game.GamePhaseChangeEvent;
import dev.anhcraft.battle.premium.PremiumModule;
import dev.anhcraft.battle.premium.system.ArenaSettings;
import dev.anhcraft.battle.premium.system.PositionPair;
import dev.anhcraft.craftkit.utils.BlockUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class GameListener implements Listener {
    @EventHandler
    public void gamePhaseChange(GamePhaseChangeEvent event){
        if(event.getOldPhase() == GamePhase.WAITING && event.getNewPhase() == GamePhase.PLAYING) {
            ArenaSettings as = PremiumModule.getInstance().getArenaSettings(event.getGame().getArena().getId());
            if (as != null) {
                for (PositionPair pair : as.getEmptyRegions()) {
                    Location first = pair.getCorner1();
                    Location second = pair.getCorner2();
                    if (first == null || second == null) continue;
                    int minX = Math.min(first.getBlockX(), second.getBlockX());
                    int maxX = Math.max(first.getBlockX(), second.getBlockX());
                    int minY = Math.min(first.getBlockY(), second.getBlockY());
                    int maxY = Math.max(first.getBlockY(), second.getBlockY());
                    int minZ = Math.min(first.getBlockZ(), second.getBlockZ());
                    int maxZ = Math.max(first.getBlockZ(), second.getBlockZ());
                    for (int x = minX; x <= maxX; x++) {
                        for (int y = minY; y <= maxY; y++) {
                            for (int z = minZ; z <= maxZ; z++) {
                                BlockUtil.setBlockFast(first.getWorld().getBlockAt(x, y, z), Material.AIR, false, true);
                            }
                        }
                    }
                }
            }
        }
    }
}
