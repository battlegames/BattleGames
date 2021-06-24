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
import dev.anhcraft.jvmkit.utils.ReflectionUtil;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.function.Consumer;

public class EntityUtil {
    private static Class<?> ENTITY_CLASS;
    private static Class<?> CRAFT_ENTITY_CLASS;

    static {
        try {
            if (NMSVersion.current().compare(NMSVersion.v1_17_R1) >= 0) {
                ENTITY_CLASS = Class.forName("net.minecraft.world.entity.Entity");
            } else {
                ENTITY_CLASS = Class.forName("net.minecraft.server." + NMSVersion.current().name() + ".Entity");
            }
            CRAFT_ENTITY_CLASS = Class.forName("org.bukkit.craftbukkit." + NMSVersion.current().name() + ".entity.CraftEntity");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void teleport(@NotNull Entity entity, @Nullable Location location) {
        teleport(entity, location, ok -> {
        });
    }

    public static void teleport(@NotNull Entity entity, @Nullable Location location, @NotNull Consumer<Boolean> callback) {
        if (location == null) return;
        if (location.getWorld() == null) {
            Bukkit.getLogger().warning(String.format("`%s` is missing param `world`. Recheck your config!", LocationUtil.toString(location)));
            location.setWorld(entity.getWorld());
        }
        PaperLib.teleportAsync(entity, location).thenAccept(callback);
    }

    public static BoundingBox getBoundingBox(@NotNull Entity bukkitEntity){
        Object craftEntity = CRAFT_ENTITY_CLASS.cast(bukkitEntity);
        Object entity = ReflectionUtil.invokeDeclaredMethod(CRAFT_ENTITY_CLASS, craftEntity, "getHandle");
        Object aabb = ReflectionUtil.invokeDeclaredMethod(ENTITY_CLASS, entity, "getBoundingBox");
        double[] v = Arrays.stream(aabb.getClass().getDeclaredFields()).filter(f -> !Modifier.isStatic(f.getModifiers())).mapToDouble(f -> {
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
