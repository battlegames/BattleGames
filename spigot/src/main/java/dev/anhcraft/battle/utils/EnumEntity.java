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
import dev.anhcraft.jvmkit.utils.EnumUtil;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public enum EnumEntity {
    DROPPED_ITEM("entity.minecraft.item", "entity.Item.name"),
    EXPERIENCE_ORB("entity.minecraft.experience_orb", "entity.XPOrb.name"),
    AREA_EFFECT_CLOUD("entity.minecraft.area_effect_cloud"),
    ELDER_GUARDIAN("entity.minecraft.elder_guardian", "entity.ElderGuardian.name"),
    WITHER_SKELETON("entity.minecraft.wither_skeleton", "entity.WitherSkeleton.name"),
    STRAY("entity.minecraft.stray", "entity.Stray.name"),
    EGG("entity.minecraft.egg"),
    LEASH_HITCH("entity.minecraft.leash_knot"),
    PAINTING("entity.minecraft.painting", "entity.Painting.name"),
    ARROW("entity.minecraft.arrow", "entity.Arrow.name"),
    SNOWBALL("entity.minecraft.snowball", "entity.Snowball.name"),
    FIREBALL("entity.minecraft.fireball", "entity.Fireball.name"),
    SMALL_FIREBALL("entity.minecraft.small_fireball", "entity.SmallFireball.name"),
    ENDER_PEARL("entity.minecraft.ender_pearl"),
    ENDER_SIGNAL("entity.minecraft.eye_of_ender"),
    SPLASH_POTION("entity.minecraft.potion", "entity.ThrownPotion.name"),
    THROWN_EXP_BOTTLE("entity.minecraft.experience_bottle"),
    ITEM_FRAME("entity.minecraft.item_frame"),
    WITHER_SKULL("entity.minecraft.wither_skull"),
    PRIMED_TNT("entity.minecraft.tnt", "entity.PrimedTnt.name"),
    FALLING_BLOCK("entity.minecraft.falling_block", "entity.FallingSand.name"),
    FIREWORK("entity.minecraft.firework_rocket"),
    HUSK("entity.minecraft.husk", "entity.Husk.name"),
    SPECTRAL_ARROW("entity.minecraft.spectral_arrow", "entity.Arrow.name"),
    SHULKER_BULLET("entity.minecraft.shulker_bullet"),
    DRAGON_FIREBALL("entity.minecraft.dragon_fireball", "entity.DragonFireball.name"),
    ZOMBIE_VILLAGER("entity.minecraft.zombie_villager", "entity.ZombieVillager.name"),
    SKELETON_HORSE("entity.minecraft.skeleton_horse", "entity.SkeletonHorse.name"),
    ZOMBIE_HORSE("entity.minecraft.zombie_horse", "entity.ZombieHorse.name"),
    ARMOR_STAND("entity.minecraft.armor_stand", "entity.ArmorStand.name"),
    DONKEY("entity.minecraft.donkey", "entity.Donkey.name"),
    MULE("entity.minecraft.mule", "entity.Mule.name"),
    EVOKER_FANGS("entity.minecraft.evoker_fangs"),
    EVOKER("entity.minecraft.evoker", "entity.EvocationIllager.name"),
    VEX("entity.minecraft.vex", "entity.Vex.name"),
    VINDICATOR("entity.minecraft.vindicator", "entity.VindicationIllager.name"),
    ILLUSIONER("entity.minecraft.illusioner", "entity.IllusionIllager.name"),
    MINECART_COMMAND("entity.minecraft.command_block_minecart"),
    BOAT("entity.minecraft.boat", "entity.Boat.name"),
    MINECART("entity.minecraft.minecart", "entity.Minecart.name"),
    MINECART_CHEST("entity.minecraft.chest_minecart", "entity.MinecartChest.name"),
    MINECART_FURNACE("entity.minecraft.furnace_minecart", "entity.Minecart.name"),
    MINECART_TNT("entity.minecraft.tnt_minecart", "entity.Minecart.name"),
    MINECART_HOPPER("entity.minecraft.hopper_minecart", "entity.MinecartHopper.name"),
    MINECART_MOB_SPAWNER("entity.minecraft.spawner_minecart", "entity.Minecart.name"),
    CREEPER("entity.minecraft.creeper", "entity.Creeper.name"),
    SKELETON("entity.minecraft.skeleton", "entity.Skeleton.name"),
    SPIDER("entity.minecraft.spider", "entity.Spider.name"),
    GIANT("entity.minecraft.giant", "entity.Giant.name"),
    ZOMBIE("entity.minecraft.zombie", "entity.Zombie.name"),
    SLIME("entity.minecraft.slime", "entity.Slime.name"),
    GHAST("entity.minecraft.ghast", "entity.Ghast.name"),
    PIG_ZOMBIE("entity.minecraft.zombie_pigman", "entity.PigZombie.name"),
    ENDERMAN("entity.minecraft.enderman", "entity.Enderman.name"),
    CAVE_SPIDER("entity.minecraft.cave_spider", "entity.CaveSpider.name"),
    SILVERFISH("entity.minecraft.silverfish", "entity.Silverfish.name"),
    BLAZE("entity.minecraft.blaze", "entity.Blaze.name"),
    MAGMA_CUBE("entity.minecraft.magma_cube", "entity.LavaSlime.name"),
    ENDER_DRAGON("entity.minecraft.ender_dragon", "entity.EnderDragon.name"),
    WITHER("entity.minecraft.wither", "entity.WitherBoss.name"),
    BAT("entity.minecraft.bat", "entity.Bat.name"),
    WITCH("entity.minecraft.witch", "entity.Witch.name"),
    ENDERMITE("entity.minecraft.endermite", "entity.Endermite.name"),
    GUARDIAN("entity.minecraft.guardian", "entity.Guardian.name"),
    SHULKER("entity.minecraft.shulker", "entity.Shulker.name"),
    PIG("entity.minecraft.pig", "entity.Pig.name"),
    SHEEP("entity.minecraft.sheep", "entity.Sheep.name"),
    COW("entity.minecraft.cow", "entity.Cow.name"),
    CHICKEN("entity.minecraft.chicken", "entity.Chicken.name"),
    SQUID("entity.minecraft.squid", "entity.Squid.name"),
    WOLF("entity.minecraft.wolf", "entity.Wolf.name"),
    MUSHROOM_COW("entity.minecraft.mooshroom", "entity.MushroomCow.name"),
    SNOWMAN("entity.minecraft.snow_golem", "entity.SnowMan.name"),
    OCELOT("entity.minecraft.ocelot", "entity.Ozelot.name"),
    IRON_GOLEM("entity.minecraft.iron_golem", "entity.VillagerGolem.name"),
    HORSE("entity.minecraft.horse", "entity.Horse.name"),
    RABBIT("entity.minecraft.rabbit", "entity.Rabbit.name"),
    POLAR_BEAR("entity.minecraft.polar_bear", "entity.PolarBear.name"),
    LLAMA("entity.minecraft.llama", "entity.Llama.name"),
    LLAMA_SPIT("entity.minecraft.llama_spit"),
    PARROT("entity.minecraft.parrot", "entity.Parrot"),
    VILLAGER("entity.minecraft.villager", "entity.Villager.name"),
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
    CAT("entity.minecraft.cat", "entity.Cat.name"),
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
    UNKNOWN("entity.notFound", "entity.generic.name"),
    LINGERING_POTION("entity.minecraft.potion", "entity.ThrownPotion.name"),
    WEATHER(null, null),
    COMPLEX_PART(null, null),
    TIPPED_ARROW("entity.minecraft.arrow", "entity.Arrow.name"),
    ZOMBIFIED_PIGLIN("entity.minecraft.zombified_piglin", null),
    HOGLIN("entity.minecraft.hoglin", null),
    PIGLIN("entity.minecraft.piglin", null),
    STRIDER("entity.minecraft.strider", null),
    ZOGLIN("entity.minecraft.zoglin", null);

    private static final Map<EntityType, EnumEntity> LOOKUP = new EnumMap<>(EntityType.class);
    private static final boolean LEGACY = NMSVersion.current().compare(NMSVersion.v1_13_R1) < 0;

    static {
        for (EntityType et : EntityType.values()) {
            EnumEntity ee = (EnumEntity) EnumUtil.findEnum(EnumEntity.class, et.name());
            if (ee != null) {
                LOOKUP.put(et, ee);
            }
        }
    }

    private final String path;
    private final String legacyPath;

    EnumEntity(@NotNull String path) {
        this(path, null);
    }
    EnumEntity(@Nullable String path, @Nullable String legacyPath) {
        this.path = path;
        this.legacyPath = legacyPath;
    }

    @Nullable
    public static EnumEntity of(@Nullable EntityType entityType) {
        return LOOKUP.get(entityType);
    }

    @NotNull
    public static String getLocalePath(@Nullable EntityType entityType) {
        EnumEntity ee = LOOKUP.get(entityType);
        if (ee == null) ee = EnumEntity.UNKNOWN;
        return ee.getLocalePath();
    }

    @Nullable
    public String getPath() {
        return path;
    }

    @Nullable
    public String getLegacyPath() {
        return legacyPath;
    }

    @NotNull
    public String getLocalePath() {
        if (LEGACY) {
            return legacyPath == null ? Objects.requireNonNull(EnumEntity.UNKNOWN.legacyPath) : legacyPath;
        } else {
            return path == null ? Objects.requireNonNull(EnumEntity.UNKNOWN.path) : path;
        }
    }
}
