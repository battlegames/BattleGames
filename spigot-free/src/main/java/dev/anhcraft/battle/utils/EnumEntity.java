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

import dev.anhcraft.jvmkit.utils.EnumUtil;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

public enum EnumEntity {
    DROPPED_ITEM("entity.minecraft.item"),
    EXPERIENCE_ORB("entity.minecraft.experience_orb"),
    AREA_EFFECT_CLOUD("entity.minecraft.area_effect_cloud"),
    ELDER_GUARDIAN("entity.minecraft.elder_guardian"),
    WITHER_SKELETON("entity.minecraft.wither_skeleton"),
    STRAY("entity.minecraft.stray"),
    EGG("entity.minecraft.egg"),
    LEASH_HITCH("entity.minecraft.leash_knot"),
    PAINTING("entity.minecraft.painting"),
    ARROW("entity.minecraft.arrow"),
    SNOWBALL("entity.minecraft.snowball"),
    FIREBALL("entity.minecraft.fireball"),
    SMALL_FIREBALL("entity.minecraft.small_fireball"),
    ENDER_PEARL("entity.minecraft.ender_pearl"),
    ENDER_SIGNAL("entity.minecraft.eye_of_ender"),
    SPLASH_POTION("entity.minecraft.potion"),
    THROWN_EXP_BOTTLE("entity.minecraft.experience_bottle"),
    ITEM_FRAME("entity.minecraft.item_frame"),
    WITHER_SKULL("entity.minecraft.wither_skull"),
    PRIMED_TNT("entity.minecraft.tnt"),
    FALLING_BLOCK("entity.minecraft.falling_block"),
    FIREWORK("entity.minecraft.firework_rocket"),
    HUSK("entity.minecraft.husk"),
    SPECTRAL_ARROW("entity.minecraft.spectral_arrow"),
    SHULKER_BULLET("entity.minecraft.shulker_bullet"),
    DRAGON_FIREBALL("entity.minecraft.dragon_fireball"),
    ZOMBIE_VILLAGER("entity.minecraft.zombie_villager"),
    SKELETON_HORSE("entity.minecraft.skeleton_horse"),
    ZOMBIE_HORSE("entity.minecraft.zombie_horse"),
    ARMOR_STAND("entity.minecraft.armor_stand"),
    DONKEY("entity.minecraft.donkey"),
    MULE("entity.minecraft.mule"),
    EVOKER_FANGS("entity.minecraft.evoker_fangs"),
    EVOKER("entity.minecraft.evoker"),
    VEX("entity.minecraft.vex"),
    VINDICATOR("entity.minecraft.vindicator"),
    ILLUSIONER("entity.minecraft.illusioner"),
    MINECART_COMMAND("entity.minecraft.command_block_minecart"),
    BOAT("entity.minecraft.boat"),
    MINECART("entity.minecraft.minecart"),
    MINECART_CHEST("entity.minecraft.chest_minecart"),
    MINECART_FURNACE("entity.minecraft.furnace_minecart"),
    MINECART_TNT("entity.minecraft.tnt_minecart"),
    MINECART_HOPPER("entity.minecraft.hopper_minecart"),
    MINECART_MOB_SPAWNER("entity.minecraft.spawner_minecart"),
    CREEPER("entity.minecraft.creeper"),
    SKELETON("entity.minecraft.skeleton"),
    SPIDER("entity.minecraft.spider"),
    GIANT("entity.minecraft.giant"),
    ZOMBIE("entity.minecraft.zombie"),
    SLIME("entity.minecraft.slime"),
    GHAST("entity.minecraft.ghast"),
    PIG_ZOMBIE("entity.minecraft.zombie_pigman"),
    ENDERMAN("entity.minecraft.enderman"),
    CAVE_SPIDER("entity.minecraft.cave_spider"),
    SILVERFISH("entity.minecraft.silverfish"),
    BLAZE("entity.minecraft.blaze"),
    MAGMA_CUBE("entity.minecraft.magma_cube"),
    ENDER_DRAGON("entity.minecraft.ender_dragon"),
    WITHER("entity.minecraft.wither"),
    BAT("entity.minecraft.bat"),
    WITCH("entity.minecraft.witch"),
    ENDERMITE("entity.minecraft.endermite"),
    GUARDIAN("entity.minecraft.guardian"),
    SHULKER("entity.minecraft.shulker"),
    PIG("entity.minecraft.pig"),
    SHEEP("entity.minecraft.sheep"),
    COW("entity.minecraft.cow"),
    CHICKEN("entity.minecraft.chicken"),
    SQUID("entity.minecraft.squid"),
    WOLF("entity.minecraft.wolf"),
    MUSHROOM_COW("entity.minecraft.mooshroom"),
    SNOWMAN("entity.minecraft.snow_golem"),
    OCELOT("entity.minecraft.ocelot"),
    IRON_GOLEM("entity.minecraft.iron_golem"),
    HORSE("entity.minecraft.horse"),
    RABBIT("entity.minecraft.rabbit"),
    POLAR_BEAR("entity.minecraft.polar_bear"),
    LLAMA("entity.minecraft.llama"),
    LLAMA_SPIT("entity.minecraft.llama_spit"),
    PARROT("entity.minecraft.parrot"),
    VILLAGER("entity.minecraft.villager"),
    ENDER_CRYSTAL("entity.minecraft.end_crystal"),
    TURTLE("entity.minecraft.turtle"),
    PHANTOM("entity.minecraft.phantom"),
    TRIDENT("entity.minecraft.trident"),
    COD("entity.minecraft.cod"),
    SALMON("entity.minecraft.salmon"),
    PUFFERFISH("entity.minecraft.pufferfish"),
    TROPICAL_FISH("entity.minecraft.tropical_fish"),
    DROWNED("entity.minecraft.drowned"),
    DOLPHIN("entity.minecraft.dolphin"),
    CAT("entity.minecraft.cat"),
    PANDA("entity.minecraft.panda"),
    PILLAGER("entity.minecraft.pillager"),
    RAVAGER("entity.minecraft.ravager"),
    TRADER_LLAMA("entity.minecraft.trader_llama"),
    WANDERING_TRADER("entity.minecraft.wandering_trader"),
    FOX("entity.minecraft.fox"),
    BEE("entity.minecraft.bee"),
    FISHING_HOOK("entity.minecraft.fishing_bobber"),
    LIGHTNING("entity.minecraft.lightning_bolt"),
    PLAYER("entity.minecraft.player"),
    UNKNOWN("entity.notFound");

    private static final Map<EntityType, EnumEntity> lookup = new EnumMap<>(EntityType.class);

    static {
        for (EntityType et : EntityType.values()) {
            EnumEntity ee = (EnumEntity) EnumUtil.findEnum(EnumEntity.class, et.name());
            if(ee != null) {
                lookup.put(et, ee);
            }
        }
    }

    @NotNull
    public static String getLocalePath(EntityType entityType) {
        EnumEntity ee = lookup.get(entityType);
        return ee == null ? ("entity.minecraft." + entityType.name().toLowerCase()) : ee.path;
    }

    private final String path;

    EnumEntity(String path) {
        this.path = path;
    }
}
