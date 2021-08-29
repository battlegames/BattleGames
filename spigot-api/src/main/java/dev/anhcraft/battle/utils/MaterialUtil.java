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

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import dev.anhcraft.jvmkit.utils.EnumUtil;
import dev.anhcraft.jvmkit.utils.PresentPair;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MaterialUtil {
    private static final Table<String, Integer, String> LEGACY = HashBasedTable.create();
    private static final Map<String, PresentPair<String, Integer>> MODERN = new HashMap<>();

    /**
     * Registers legacy material.
     * @param legacy legacy material
     * @param data legacy data
     * @param modern modern material
     * @deprecated INTERNAL USES ONLY!
     */
    @Deprecated
    public static void registerLegacyMaterial(String legacy, int data, String modern){
        LEGACY.put(legacy, data, modern);
        LEGACY.put("LEGACY_" + legacy, data, modern);
        MODERN.put(modern, new PresentPair<>(legacy, data));
    }


    /**
     * Checks if the given material is null or is air.
     * @param material material
     * @return {@code true} if it is "null". Otherwise is {@code false}.
     */
    public static boolean isEmpty(@Nullable Material material) {
        return material == null || material == Material.AIR || material.toString().endsWith("_AIR");
    }

    @Nullable
    public static String modernize(@Nullable String material) {
        return modernize(material, 0);
    }

    @Nullable
    public static String modernize(@Nullable String material, int data) {
        if(material == null || material.isEmpty()) return null;
        material = material.toUpperCase();
        Material mt = (Material) EnumUtil.findEnum(Material.class, material);
        return mt != null ? material : LEGACY.get(material, data);
    }

    @Nullable
    public static LegacyMaterial antiquate(@Nullable String material) {
        return antiquate(material, 0);
    }

    @Nullable
    public static LegacyMaterial antiquate(@Nullable String material, int data) {
        if(material == null || material.isEmpty()) return null;
        material = material.toUpperCase();
        Material mt = (Material) EnumUtil.findEnum(Material.class, material);
        if (mt != null && mt.name().startsWith("LEGACY_")) {
            return new LegacyMaterial(mt, data);
        } else {
            PresentPair<String, Integer> pair = MODERN.get(material);
            if(pair != null) {
                // put this one first for faster searching in 1.13+
                mt = (Material) EnumUtil.findEnum(Material.class, "LEGACY_" + pair.getFirst());
                if (mt != null) return new LegacyMaterial(mt, pair.getSecond());
                // if legacy not found, maybe in 1.12 and earlier versions
                mt = (Material) EnumUtil.findEnum(Material.class, pair.getFirst());
                if (mt != null) return new LegacyMaterial(mt, pair.getSecond());
            }
            return null;
        }
    }

    @NotNull
    public static Optional<Material> parse(@Nullable String material) {
        return parse(material, 0);
    }

    @NotNull
    public static Optional<Material> parse(@Nullable String material, int data) {
        if(material == null || material.isEmpty()) return Optional.empty();
        material = material.toUpperCase();
        Material mt = (Material) EnumUtil.findEnum(Material.class, material);
        if(mt == null) {
            String found = LEGACY.get(material, data);
            if(found != null) {
                mt = (Material) EnumUtil.findEnum(Material.class, found);
            }
        }
        return Optional.ofNullable(mt);
    }
}
