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

package dev.anhcraft.battle.system;

import dev.anhcraft.battle.BattleComponent;
import dev.anhcraft.battle.BattlePlugin;
import dev.anhcraft.battle.system.debugger.BattleDebugger;
import dev.anhcraft.jvmkit.utils.FileUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class BattleWorldRollback extends BattleComponent {
    private final Set<String> cachedWorlds = new HashSet<>();
    private final File cachedWorldFolder;

    public BattleWorldRollback(BattlePlugin plugin) {
        super(plugin);

        cachedWorldFolder = new File(plugin.getDataFolder(), "cache" + File.separatorChar + "worlds");
        cachedWorldFolder.mkdirs();
    }

    @NotNull
    public File getCachedWorldFolder(World world) {
        return new File(cachedWorldFolder, world.getName());
    }

    public boolean backupWorld(World world) {
        if (cachedWorlds.contains(world.getName())) return true;
        cachedWorlds.add(world.getName());
        plugin.getLogger().info("Making a backup for world " + world.getName() + "...");
        File dest = getCachedWorldFolder(world);
        if (dest.exists()) {
            FileUtil.clean(dest);
        } else {
            dest.mkdir();
        }
        return FileUtil.copy(world.getWorldFolder(), dest);
    }

    public boolean rollbackWorld(World world) {
        plugin.getLogger().info("[Rollback/BattleWorld] Reloading world: " + world.getName());
        BattleDebugger.startTiming("rollback-battle-world");
        File workingDir = world.getWorldFolder();
        File cacheDir = getCachedWorldFolder(world);
        if (!cacheDir.exists()) {
            BattleDebugger.endTiming("rollback-battle-world");
            return false;
        }
        world.getPlayers().forEach(c -> c.kickPlayer("The world is going to be reloaded"));
        if (!Bukkit.unloadWorld(world, false)) {
            BattleDebugger.endTiming("rollback-battle-world");
            return false;
        }
        FileUtil.clean(workingDir);
        if (!FileUtil.copy(cacheDir, workingDir)) {
            BattleDebugger.endTiming("rollback-battle-world");
            return false;
        }
        new WorldCreator(world.getName()).createWorld();
        BattleDebugger.endTiming("rollback-battle-world");
        return true;
    }
}
