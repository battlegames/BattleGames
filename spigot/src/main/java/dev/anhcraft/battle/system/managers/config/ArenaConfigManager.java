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

package dev.anhcraft.battle.system.managers.config;

import dev.anhcraft.battle.api.Rollback;
import dev.anhcraft.battle.api.arena.Arena;
import dev.anhcraft.battle.utils.ConfigHelper;
import dev.anhcraft.battle.utils.ConfigUpdater;
import dev.anhcraft.craftkit.cb_common.BoundingBox;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class ArenaConfigManager extends ConfigManager {
    public final Map<String, Arena> ARENA_MAP = new HashMap<>();
    private final ConfigUpdater configUpdater;

    public ArenaConfigManager() {
        super("Arena", "arenas.yml");
        configUpdater = new ConfigUpdater(plugin.getLogger());
        configUpdater.getPathRelocating().add(
                new ConfigUpdater.PathRelocating()
                        .oldPath("attr")
                        .newPath("game_options")
                        .type(ConfigurationSection.class)
        );
        configUpdater.getPathRelocating().add(
                new ConfigUpdater.PathRelocating()
                        .oldPath("mode_options")
                        .newPath("game_options")
                        .type(ConfigurationSection.class)
        );
    }

    @Override
    public void onLoad() {
        Set<String> keys = getSettings().getKeys(false);
        plugin.getLogger().info("[" + loggerName + "] Total arenas found: " + keys.size());
        plugin.limit(loggerName, keys, 8).forEach(s -> {
            Arena arena = new Arena(s);
            plugin.getLogger().info("[" + loggerName + "] Loading arena " + s + "...");
            ConfigurationSection cs = getSettings().getConfigurationSection(s);
            configUpdater.update(Objects.requireNonNull(cs));
            ConfigHelper.load(Arena.class, cs, arena);
            ARENA_MAP.put(s, arena);
            if (arena.getRollback() == null) {
                plugin.getLogger().warning("[" + loggerName + "] For safety reasons, you should specify rollback for arena #" + arena.getId());
            } else {
                if (arena.getRollback().getProvider() == Rollback.Provider.SLIME_WORLD && !plugin.hasSlimeWorldManagerSupport()) {
                    arena.getRollback().setProvider(Rollback.Provider.BATTLE_WORLD);
                }
                if (arena.getRollback().getProvider() == Rollback.Provider.BATTLE_WORLD) {
                    for (Iterator<String> it = arena.getRollback().getWorlds().iterator(); it.hasNext(); ) {
                        String w = it.next();
                        World wd = plugin.getServer().getWorld(w);
                        if (wd == null) {
                            plugin.getLogger().warning("[" + loggerName + "/BattleWorldValidator] World not found: " + w);
                            it.remove();
                        } else if (plugin.SWMIntegration != null && plugin.SWMIntegration.isReadOnly(w) != -1) {
                            it.remove();
                        } else {
                            plugin.battleWorldRollback.backupWorld(wd);
                        }
                    }
                }
                if (arena.getRollback().getProvider() == Rollback.Provider.BATTLE_REGION) {
                    Location l1 = arena.getRollback().getCorner1();
                    Location l2 = arena.getRollback().getCorner2();
                    if (l1 == null || l2 == null) {
                        plugin.getLogger().warning("[" + loggerName + "/BattleRegionValidator] Location is null! (Arena #" + arena.getId() + ")");
                    } else if (l1.getWorld() == null || l2.getWorld() == null) {
                        plugin.getLogger().warning("[" + loggerName + "/BattleRegionValidator] World is absent! (Arena #" + arena.getId() + ")");
                    } else if (!l1.getWorld().equals(l2.getWorld())) {
                        plugin.getLogger().warning("[" + loggerName + "/BattleRegionValidator] Both locations must be in the same world! (Arena #" + arena.getId() + ")");
                    } else {
                        List<BoundingBox> list = arena.getRollback().getCachedRegionPartitions();
                        plugin.battleRegionRollback.handleDivision(l1, l2, list);
                        plugin.getLogger().info("[" + loggerName + "/BattleRegion] Arena #" + arena.getId() + ": " + list.size() + " partitions");
                        for (BoundingBox box : list) {
                            Location a = box.getMin().toLocation(l1.getWorld());
                            Location b = box.getMax().toLocation(l1.getWorld());
                            plugin.battleRegionRollback.backupRegion(a, b);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onClean() {
        ARENA_MAP.clear();
    }
}
