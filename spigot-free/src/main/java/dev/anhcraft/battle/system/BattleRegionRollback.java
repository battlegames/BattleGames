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
import dev.anhcraft.craftkit.cb_common.nbt.CompoundTag;
import dev.anhcraft.craftkit.cb_common.nbt.IntTag;
import dev.anhcraft.craftkit.cb_common.nbt.StringTag;
import dev.anhcraft.craftkit.utils.BlockUtil;
import dev.anhcraft.jvmkit.utils.FileUtil;
import dev.anhcraft.jvmkit.utils.ObjectUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class BattleRegionRollback extends BattleComponent {
    private final File cachedRegionFolder;

    public BattleRegionRollback(BattlePlugin plugin) {
        super(plugin);

        cachedRegionFolder = new File(plugin.getDataFolder(), "cachedRegions");
        cachedRegionFolder.mkdir();
    }

    public boolean backupRegion(@NotNull Location first, @NotNull Location second) {
        int minX = Math.min(first.getBlockX(), second.getBlockX());
        int maxX = Math.max(first.getBlockX(), second.getBlockX());
        int minY = Math.min(first.getBlockY(), second.getBlockY());
        int maxY = Math.max(first.getBlockY(), second.getBlockY());
        int minZ = Math.min(first.getBlockZ(), second.getBlockZ());
        int maxZ = Math.max(first.getBlockZ(), second.getBlockZ());
        int hash1 = first.hashCode();
        int hash2 = second.hashCode();
        World world = Objects.requireNonNull(first.getWorld());
        plugin.getLogger().info("Making region backup at world " + world.getName());
        plugin.getLogger().info("- Hash 1: " + hash1);
        plugin.getLogger().info("- Hash 2: " + hash2);
        CompoundTag root = new CompoundTag();
        CompoundTag blocks = new CompoundTag();
        long i = 0;
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if(!block.isEmpty()) {
                        CompoundTag b = new CompoundTag();
                        b.put("x", x - minX);
                        b.put("y", y - minY);
                        b.put("z", z - minZ);
                        b.put("data", block.getBlockData().getAsString());
                        CompoundTag te = CompoundTag.of(block);
                        if(te.size() > 0) {
                            te.remove("x");
                            te.remove("y");
                            te.remove("z");
                            b.put("tileEntity", te);
                        }
                        blocks.put(String.valueOf(i), b);
                        i++;
                    }
                }
            }
        }
        root.put("width", maxX - minX + 1);
        root.put("depth", maxZ - minZ + 1);
        root.put("height", maxY - minY + 1);
        plugin.getLogger().info("Total: " + blocks.size() + " blocks");
        CompoundTag brd = new CompoundTag();
        brd.put("hash1", hash1);
        brd.put("hash2", hash2);
        brd.put("version", plugin.getDescription().getVersion());
        root.put("battleRegionData", brd);
        root.put("blocks", blocks);
        File dir = new File(cachedRegionFolder, hash1 + File.separator + hash2);
        dir.mkdirs();
        File f = new File(dir, world.getName() + ".struct");
        try {
            if(!f.createNewFile()) {
                FileUtil.clean(f);
            }
        } catch (IOException e) {
            return false;
        }
        root.save(f);
        return true;
    }

    public boolean rollbackRegion(@NotNull Location first, @NotNull Location second) {
        BattleDebugger.startTiming("rollback-battle-region");
        double minX = Math.min(first.getX(), second.getX());
        double minY = Math.min(first.getY(), second.getY());
        double minZ = Math.min(first.getZ(), second.getZ());
        Location loc = new Location(first.getWorld(), minX, minY, minZ);
        int hash1 = first.hashCode();
        int hash2 = second.hashCode();
        World world = Objects.requireNonNull(first.getWorld());
        File f = new File(cachedRegionFolder, hash1 + File.separator + hash2 + File.separator + world.getName() + ".struct");
        if(f.exists()) {
            CompoundTag tag = new CompoundTag();
            tag.load(f);
            if(tag.size() > 0) {
                plugin.getLogger().info("[Rollback/BattleRegion] Resetting region at world " + world.getName());
                plugin.getLogger().info("- Hash 1: " + hash1);
                plugin.getLogger().info("- Hash 2: " + hash2);
                CompoundTag blocks = tag.get("blocks", CompoundTag.class);
                if (blocks != null) {
                    for(String s : blocks.listNames()){
                        CompoundTag b = blocks.get(s, CompoundTag.class);
                        if (b != null && b.has("data")) {
                            Location pos = loc.clone().add(
                                    ObjectUtil.optional(b.getValue("x", IntTag.class), 0),
                                    ObjectUtil.optional(b.getValue("y", IntTag.class), 0),
                                    ObjectUtil.optional(b.getValue("z", IntTag.class), 0)
                            );
                            BlockData bd = Bukkit.createBlockData(Objects.requireNonNull(b.getValue("data", StringTag.class)));
                            BlockUtil.setBlockFast(pos.getBlock(), bd, false, true);
                            BlockState te = pos.getBlock().getState();
                            if(te.isPlaced()) {
                                CompoundTag tileEntity = b.get("tileEntity", CompoundTag.class);
                                if(tileEntity != null) {
                                    tileEntity.put("x", pos.getBlockX());
                                    tileEntity.put("y", pos.getBlockY());
                                    tileEntity.put("z", pos.getBlockZ());
                                    tileEntity.save(pos.getBlock(), true);
                                }
                            }
                        }
                    }
                    BattleDebugger.endTiming("rollback-battle-region");
                    return true;
                }
            }
        }
        BattleDebugger.endTiming("rollback-battle-region");
        return false;
    }
}
