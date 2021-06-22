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

import dev.anhcraft.jvmkit.utils.Condition;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This is the clone of ABIF (https://github.com/anhcraft/ABIF/) which brings multi-version support.
 */
public class ABIF {
    static class Key{
        final static String MATERIAL = "material";
        final static String AMOUNT = "amount";
        final static String DAMAGE = "damage";
        final static String NAME = "name";
        final static String LORE = "lore";
        final static String ENCHANT = "enchant";
        final static String FLAG = "flag";
        final static String UNBREAKABLE = "unbreakable";
        final static String MODIFIERS = "modifiers";
        final static String ATTRIBUTE = "attr";
        final static String META_TYPE = "meta.type";
        final static String META_POTION_TYPE = "meta.potion.type";
        final static String META_POTION_EXTENDED = "meta.potion.extended";
        final static String META_POTION_UPGRADED = "meta.potion.upgraded";
        final static String META_LEATHER_COLOR_R = "meta.leather.color_r";
        final static String META_LEATHER_COLOR_G = "meta.leather.color_g";
        final static String META_LEATHER_COLOR_B = "meta.leather.color_b";
        final static String META_SPAWN_EGG_ENTITY = "meta.spawn_egg.entity";
        final static String META_SKULL_OWNER = "meta.skull.owner";
        final static String META_BOOK_AUTHOR = "meta.book.author";
        final static String META_BOOK_TITLE = "meta.book.title";
        final static String META_BOOK_GENERATION = "meta.book.generation";
        final static String META_BOOK_PAGES = "meta.book.pages";
    }

    private static final Map<String, Enchantment> ENCHANT_MAP = new HashMap<>();
    private static final Map<Enchantment, String> REVERSED_ENCHANT_MAP = new HashMap<>();
    private static final List<String> AVAILABLE_VANILLA_ENCHANTS = Arrays
            .stream(Enchantment.values())
            .map(Enchantment::getName)
            .collect(Collectors.toList());

    static {
        registerEnchant("protection", "PROTECTION_ENVIRONMENTAL");
        registerEnchant("fire protection", "PROTECTION_FIRE");
        registerEnchant("feather falling", "PROTECTION_FALL");
        registerEnchant("blast protection", "PROTECTION_EXPLOSIONS");
        registerEnchant("projectile protection", "PROTECTION_PROJECTILE");
        registerEnchant("respiration", "OXYGEN");
        registerEnchant("aqua affinity", "WATER_WORKER");
        registerEnchant("thorns", "THORNS");
        registerEnchant("depth strider", "DEPTH_STRIDER");
        registerEnchant("sharpness", "DAMAGE_ALL");
        registerEnchant("smite", "DAMAGE_UNDEAD");
        registerEnchant("bane of arthropods", "DAMAGE_ARTHROPODS");
        registerEnchant("knockback", "KNOCKBACK");
        registerEnchant("fire aspect", "FIRE_ASPECT");
        registerEnchant("looting", "LOOT_BONUS_MOBS");
        registerEnchant("efficiency", "DIG_SPEED");
        registerEnchant("silk touch", "SILK_TOUCH");
        registerEnchant("unbreaking", "DURABILITY");
        registerEnchant("fortune", "LOOT_BONUS_BLOCKS");
        registerEnchant("power", "ARROW_DAMAGE");
        registerEnchant("punch", "ARROW_KNOCKBACK");
        registerEnchant("flame", "ARROW_FIRE");
        registerEnchant("infinity", "ARROW_INFINITE");
        registerEnchant("luck of the sea", "LUCK");
        registerEnchant("lure", "LURE");
        registerEnchant("mending", "MENDING");
        registerEnchant("frost walker", "FROST_WALKER");
        registerEnchant("curse of binding", "BINDING_CURSE");
        registerEnchant("curse of vanishing", "VANISHING_CURSE");
        registerEnchant("sweeping edge", "SWEEPING_EDGE");
        registerEnchant("loyalty", "LOYALTY");
        registerEnchant("impaling", "IMPALING");
        registerEnchant("riptide", "RIPTIDE");
        registerEnchant("channeling", "CHANNELING");
        registerEnchant("multishot", "MULTISHOT");
        registerEnchant("quick charge", "QUICK_CHARGE");
        registerEnchant("piercing", "PIERCING");
        registerEnchant("soul speed", "SOUL_SPEED");
    }

    private static void registerEnchant(String encName, String enumName){
        if(AVAILABLE_VANILLA_ENCHANTS.contains(enumName)) {
            Enchantment enc = Enchantment.getByName(enumName);
            ENCHANT_MAP.put(enumName.toLowerCase(), enc);
            registerEnchant(encName, enc);
        }
    }

    /**
     * Registers the given enchantment, so it can be handled by ABIF.
     * @param encName the name of the enchantment (can contain spaces between words)
     * @param enchantment an instance of {@link Enchantment}
     */
    public static void registerEnchant(@NotNull String encName, @NotNull Enchantment enchantment){
        Condition.argNotNull("encName", encName);
        Condition.argNotNull("enchantment", enchantment);
        ENCHANT_MAP.put(encName.toLowerCase(), enchantment);
        ENCHANT_MAP.put(encName.replace(" ", "").toLowerCase(), enchantment);
        REVERSED_ENCHANT_MAP.put(enchantment, encName);
    }

    @Nullable
    public static Enchantment getEnchant(@NotNull String encName){
        Condition.argNotNull("encName", encName);
        return ENCHANT_MAP.get(encName.toLowerCase());
    }
}
