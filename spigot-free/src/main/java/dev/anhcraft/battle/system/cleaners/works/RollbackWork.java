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

package dev.anhcraft.battle.system.cleaners.works;

import dev.anhcraft.battle.BattlePlugin;
import dev.anhcraft.battle.api.arena.Arena;
import dev.anhcraft.battle.api.misc.Rollback;
import dev.anhcraft.battle.system.cleaners.WorkSession;
import dev.anhcraft.craftkit.cb_common.BoundingBox;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CountDownLatch;

public class RollbackWork implements Work {
    @Override
    public String id() {
        return "rollback";
    }

    @Override
    public void handle(@NotNull BattlePlugin plugin, WorkSession session, @NotNull Arena arena) {
        if(arena.getRollback() != null) {
            Rollback rollback = arena.getRollback();
            if (rollback.getProvider() == Rollback.Provider.SLIME_WORLD && plugin.hasSlimeWorldManagerSupport()) {
                for (String w : rollback.getWorlds()) {
                    CountDownLatch countDownLatch = new CountDownLatch(1);
                    switch (plugin.SWMIntegration.isReadOnly(w)) {
                        case -1: {
                            plugin.getLogger().warning("[Rollback/SWM] World not found: " + w);
                            continue;
                        }
                        case 0: {
                            plugin.getLogger().warning("[Rollback/SWM] World is not in read-only mode: " + w);
                            continue;
                        }
                        case 1: {
                            plugin.getLogger().info("[Rollback/SWM] Reloading world: " + w);
                            plugin.SWMIntegration.reloadWorld(countDownLatch, w);
                        }
                    }
                    try {
                        countDownLatch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else if (rollback.getProvider() == Rollback.Provider.BATTLE_WORLD) {
                CountDownLatch countDownLatch = new CountDownLatch(1);
                for (Iterator<String> it = arena.getRollback().getWorlds().iterator(); it.hasNext(); ) {
                    String w = it.next();
                    World wd = plugin.getServer().getWorld(w);
                    if(wd == null){
                        plugin.getLogger().warning("World not found: "+w);
                        it.remove();
                    } else {
                        plugin.extension.getTaskHelper().newTask(() -> {
                            if (plugin.battleWorldRollback.rollbackWorld(wd)) {
                                plugin.getLogger().info("[Rollback/BattleWorld] World reloaded successfully!");
                            } else {
                                plugin.getLogger().warning("[Rollback/BattleWorld] Failed to reload! (Please check the world)");
                            }
                            countDownLatch.countDown();
                        });
                    }
                }
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else if (rollback.getProvider() == Rollback.Provider.BATTLE_REGION) {
                Location l1 = rollback.getCorner1();
                Location l2 = rollback.getCorner2();
                if (l1 != null && l2 != null) {
                    List<BoundingBox> crp = rollback.getCachedRegionPartitions();
                    CountDownLatch countDownLatch = new CountDownLatch(1);
                    plugin.extension.getTaskHelper().newTask(() -> {
                        plugin.getLogger().info("[Rollback/BattleRegion] Total partitions: " + crp.size());
                        for (BoundingBox box : crp) {
                            Location a = box.getMin().toLocation(Objects.requireNonNull(l1.getWorld()));
                            Location b = box.getMax().toLocation(l1.getWorld());
                            if (!plugin.battleRegionRollback.rollbackRegion(a, b)) {
                                plugin.getLogger().warning("[Rollback/BattleRegion] Failed to reset!");
                            }
                        }
                        if(rollback.shouldClearEntities()) {
                            rollback.getWorlds().stream()
                                    .map(Bukkit::getWorld)
                                    .filter(Objects::nonNull)
                                    .map(World::getEntities)
                                    .flatMap(Collection::stream)
                                    .forEach(Entity::remove);
                        }
                        countDownLatch.countDown();
                    });
                    try {
                        countDownLatch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        session.done(this);
    }
}
