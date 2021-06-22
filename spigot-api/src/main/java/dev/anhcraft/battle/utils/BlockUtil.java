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

import dev.anhcraft.config.bukkit.NMSVersion;
import dev.anhcraft.jvmkit.utils.Condition;
import dev.anhcraft.jvmkit.utils.ReflectionUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockUtil {
    private static Class<?> IBlockAccessClass;
    private static Class<?> IBlockDataClass;
    private static Class<?> voxelShapeClass;
    private static Class<?> blockPositionClass;
    private static Class<?> packetClass;
    private static Class<?> blockBreakAnimationPacketClass;
    private static Class<?> playerConnectionClass;
    private static Class<?> entityPlayerClass;
    private static Class<?> CRAFT_BLOCK_CLASS;
    private static Class<?> CRAFT_WORLD_CLASS;
    private static Class<?> CRAFT_PLAYER_CLASS;

    static {
        try {
            if (NMSVersion.current().compare(NMSVersion.v1_17_R1) >= 0) {
                IBlockAccessClass = Class.forName("net.minecraft.world.level.IBlockAccess");
                IBlockDataClass = Class.forName("net.minecraft.world.level.block.state.IBlockData");
                voxelShapeClass = Class.forName("net.minecraft.world.phys.shapes.VoxelShape");
                blockPositionClass = Class.forName("net.minecraft.core.BlockPosition");
                packetClass = Class.forName("net.minecraft.network.protocol.Packet");
                blockBreakAnimationPacketClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutBlockBreakAnimation");
                entityPlayerClass = Class.forName("net.minecraft.server.level.EntityPlayer");
                playerConnectionClass = Class.forName("net.minecraft.server.network.PlayerConnection");
            } else {
                IBlockAccessClass = Class.forName("net.minecraft.server." + NMSVersion.current().name() + ".IBlockAccess");
                IBlockDataClass = Class.forName("net.minecraft.server." + NMSVersion.current().name() + ".IBlockData");
                voxelShapeClass = Class.forName("net.minecraft.server." + NMSVersion.current().name() + ".VoxelShape");
                blockPositionClass = Class.forName("net.minecraft.server." + NMSVersion.current().name() + ".BlockPosition");
                packetClass = Class.forName("net.minecraft.server." + NMSVersion.current().name() + ".Packet");
                blockBreakAnimationPacketClass = Class.forName("net.minecraft.server." + NMSVersion.current().name() + ".PacketPlayOutBlockBreakAnimation");
                entityPlayerClass = Class.forName("net.minecraft.server." + NMSVersion.current().name() + ".EntityPlayer");
                playerConnectionClass = Class.forName("net.minecraft.server." + NMSVersion.current().name() + ".PlayerConnection");
            }
            CRAFT_BLOCK_CLASS = Class.forName("org.bukkit.craftbukkit." + NMSVersion.current().name() + ".block.CraftBlock");
            CRAFT_WORLD_CLASS = Class.forName("org.bukkit.craftbukkit." + NMSVersion.current().name() + ".CraftWorld");
            CRAFT_WORLD_CLASS = Class.forName("org.bukkit.craftbukkit." + NMSVersion.current().name() + ".entity.CraftPlayer");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets all blocks in specific distance from given central location.
     * @param loc the central location
     * @param rx the maximum distance on the x axis
     * @param ry the maximum distance on the y axis
     * @param rz the maximum distance on the z axis
     * @return list of blocks
     */
    @NotNull
    public static List<Block> getNearbyBlocks(@NotNull Location loc, int rx, int ry, int rz){
        Condition.argNotNull("loc", loc);

        List<Block> blocks = new ArrayList<>();
        double cx = loc.getX();
        double cy = loc.getY();
        double cz = loc.getZ();

        for (int x = -rx; x <= rx; x++){
            for (int y = -ry; y <= ry; y++) {
                for (int z = -rz; z <= rz; z++) {
                    loc.setX(cx + x);
                    loc.setY(cy + y);
                    loc.setZ(cz + z);
                    blocks.add(loc.getBlock());
                }
            }
        }
        return blocks;
    }

    public static BoundingBox getBoundingBox(@NotNull Block bukkitBlock){
        Object craftBlock = CRAFT_BLOCK_CLASS.cast(bukkitBlock);
        Object pos = ReflectionUtil.invokeDeclaredMethod(CRAFT_BLOCK_CLASS, craftBlock, "getPosition");
        Object blockData = ReflectionUtil.invokeDeclaredMethod(CRAFT_BLOCK_CLASS, craftBlock, "getNMS");
        Object craftWorld = CRAFT_WORLD_CLASS.cast(bukkitBlock.getWorld());
        Object world = ReflectionUtil.invokeDeclaredMethod(CRAFT_WORLD_CLASS, craftWorld, "getHandle");
        Object voxelShape = ReflectionUtil.invokeDeclaredMethod(IBlockDataClass, blockData,
                NMSVersion.current() == NMSVersion.v1_13_R1 ? "g" : "getShape",
                new Class<?>[]{IBlockAccessClass, blockPositionClass},
                new Object[]{world, pos}
        );
        boolean empty = (boolean) ReflectionUtil.invokeDeclaredMethod(voxelShapeClass, voxelShape, NMSVersion.current() == NMSVersion.v1_13_R1 ? "b" : "isEmpty");
        if(empty) {
           return new BoundingBox();
        } else {
            Object aabb = ReflectionUtil.invokeDeclaredMethod(voxelShapeClass, voxelShape, NMSVersion.current() == NMSVersion.v1_13_R1 ? "a" : "getBoundingBox");
            double[] v = Arrays.stream(aabb.getClass().getDeclaredFields()).mapToDouble(f -> {
                try {
                    return (double) f.get(aabb);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                return 0;
            }).toArray();
            return new BoundingBox(v[0], v[1], v[2], v[3], v[4], v[5]);
        }
    }

    public static void createBreakAnimation(int id, Block bukkitBlock, int stage, List<Player> viewers) {
        Object craftBlock = CRAFT_BLOCK_CLASS.cast(bukkitBlock);
        Object pos = ReflectionUtil.invokeDeclaredMethod(CRAFT_BLOCK_CLASS, craftBlock, "getPosition");
        Object packet = ReflectionUtil.invokeDeclaredConstructor(blockBreakAnimationPacketClass,
                new Class<?>[]{int.class, blockPositionClass, int.class},
                new Object[]{id, pos, stage}
        );

        for(Player bukkitPlayer : viewers){
            Object craftPlayer = CRAFT_PLAYER_CLASS.cast(bukkitPlayer);
            Object entityPlayer = ReflectionUtil.invokeDeclaredMethod(CRAFT_PLAYER_CLASS, craftPlayer, "getHandle");
            Object conn = ReflectionUtil.invokeDeclaredMethod(entityPlayerClass, entityPlayer, NMSVersion.current() == NMSVersion.v1_17_R1 ? "b" : "playerConnection");
            ReflectionUtil.invokeDeclaredMethod(playerConnectionClass, conn, "sendPacket",
                    new Class<?>[]{packetClass},
                    new Object[]{packet}
            );
        }
    }
}
