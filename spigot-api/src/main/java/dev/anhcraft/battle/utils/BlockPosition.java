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

package dev.anhcraft.battle.utils;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.Objects;

public class BlockPosition {
    @NotNull
    public static BlockPosition of(@NotNull Location location) {
        Preconditions.checkNotNull(location);
        return new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getWorld());
    }

    @NotNull
    public static BlockPosition of(@NotNull Block block) {
        Preconditions.checkNotNull(block);
        return new BlockPosition(block.getX(), block.getY(), block.getZ(), block.getWorld());
    }

    @NotNull
    public static BlockPosition of(@NotNull String str) {
        Preconditions.checkNotNull(str);
        String[] a = str.split(" ");
        return new BlockPosition(Integer.parseInt(a[1]), Integer.parseInt(a[2]), Integer.parseInt(a[3]), a[0].equals("null") ? null : Bukkit.getWorld(a[0]));
    }

    private final int x;
    private final int y;
    private final int z;
    private final WeakReference<World> world;

    public BlockPosition(int x, int y, int z, @Nullable World world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = new WeakReference<>(world);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    @Nullable
    public World getWorld() {
        return world.get();
    }

    @NotNull
    public Block getBlock(){
        return Objects.requireNonNull(world.get()).getBlockAt(x, y, z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockPosition that = (BlockPosition) o;
        return x == that.x &&
                y == that.y &&
                z == that.z &&
                Objects.equals(world.get(), that.world.get());
    }

    @Override
    public int hashCode() {
        World w = world.get();
        int hash = 3;
        hash = 19 * hash + (w == null ? 0 : w.hashCode());
        hash = 19 * hash + (int) (Double.doubleToLongBits(x) ^ (Double.doubleToLongBits(x) >>> 32));
        hash = 19 * hash + (int) (Double.doubleToLongBits(y) ^ (Double.doubleToLongBits(y) >>> 32));
        hash = 19 * hash + (int) (Double.doubleToLongBits(z) ^ (Double.doubleToLongBits(z) >>> 32));
        return hash;
    }

    @NotNull
    public String toString() {
        World w = world.get();
        return (w == null ? "null" : w.getName()) + " " + x + " " + y + " " + z;
    }
}
