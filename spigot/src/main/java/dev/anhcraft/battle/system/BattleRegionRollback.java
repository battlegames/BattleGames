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

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTFile;
import de.tr7zw.changeme.nbtapi.NBTTileEntity;
import dev.anhcraft.battle.BattleComponent;
import dev.anhcraft.battle.BattlePlugin;
import dev.anhcraft.battle.system.debugger.BattleDebugger;
import dev.anhcraft.battle.utils.BoundingBox;
import dev.anhcraft.jvmkit.utils.FileUtil;
import dev.anhcraft.jvmkit.utils.ObjectUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.MultipleFacing;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class BattleRegionRollback extends BattleComponent {
    private final File cachedRegionFolder;

    public BattleRegionRollback(BattlePlugin plugin) {
        super(plugin);

        cachedRegionFolder = new File(plugin.getDataFolder(), "cache" + File.separatorChar + "regions");
        cachedRegionFolder.mkdirs();
    }

    public void handleDivision(@NotNull Location first, @NotNull Location second, @NotNull List<BoundingBox> list) {
        BoundingBox box = BoundingBox.of(first, second);
        if (box.getVolume() > plugin.generalConf.getRegionPartitionSize()) {
            World world = Objects.requireNonNull(first.getWorld());
            Location center = box.getCenter().toLocation(world);
            for (Location loc : box.getLocationCorners(world)) {
                handleDivision(center, loc, list);
            }
            //list.add(box); <-- don't add the original!!!
        } else {
            list.add(box);
        }
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
        NBTContainer root = new NBTContainer();
        NBTCompound blocks = root.addCompound("blocks");
        long i = 0;
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (!block.isEmpty()) {
                        NBTCompound b = blocks.addCompound(String.valueOf(i));
                        b.setInteger("x", x - minX);
                        b.setInteger("y", y - minY);
                        b.setInteger("z", z - minZ);
                        b.setString("data", block.getBlockData().getAsString());
                        NBTCompound te = b.addCompound("tileEntity");
                        te.mergeCompound(new NBTTileEntity(block.getState()));
                        te.removeKey("x");
                        te.removeKey("y");
                        te.removeKey("z");
                        i++;
                    }
                }
            }
        }
        root.setInteger("width", maxX - minX + 1);
        root.setInteger("depth", maxZ - minZ + 1);
        root.setInteger("height", maxY - minY + 1);
        NBTCompound brd = root.addCompound("battleRegionData");
        brd.setInteger("hash1", hash1);
        brd.setInteger("hash2", hash2);
        brd.setString("version", plugin.getDescription().getVersion());
        File dir = new File(cachedRegionFolder, hash1 + File.separator + hash2);
        dir.mkdirs();
        File f = new File(dir, world.getName() + ".struct");
        try {
            if (!f.createNewFile()) {
                FileUtil.clean(f);
            }
        } catch (IOException e) {
            return false;
        }
        try(FileOutputStream s = new FileOutputStream(f)) {
            root.writeCompound(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        if (f.exists()) {
            try {
                NBTFile tag = new NBTFile(f);
                NBTCompound blocks = tag.getCompound("blocks");
                if (blocks != null) {
                    for (String s : blocks.getKeys()) {
                        NBTCompound b = blocks.getCompound(s);
                        Location pos = loc.clone().add(
                                ObjectUtil.optional(b.getInteger("x"), 0),
                                ObjectUtil.optional(b.getInteger("y"), 0),
                                ObjectUtil.optional(b.getInteger("z"), 0)
                        );
                        BlockData bd = Bukkit.createBlockData(Objects.requireNonNull(b.getString("data")));
                        pos.getBlock().setBlockData(bd,
                                bd instanceof Directional
                                        || bd instanceof MultipleFacing
                                        || bd instanceof Bisected);
                        BlockState te = pos.getBlock().getState();
                        if (te.isPlaced()) {
                            NBTCompound tileEntity = b.getCompound("tileEntity");
                            if (tileEntity != null) {
                                tileEntity.setInteger("x", pos.getBlockX());
                                tileEntity.setInteger("y", pos.getBlockY());
                                tileEntity.setInteger("z", pos.getBlockZ());
                                new NBTTileEntity(pos.getBlock().getState()).mergeCompound(tileEntity);
                            }
                        }
                    }
                    BattleDebugger.endTiming("rollback-battle-region");
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        BattleDebugger.endTiming("rollback-battle-region");
        return false;
    }
}
