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

package dev.anhcraft.abm.api.misc;

import dev.anhcraft.jvmkit.utils.ReflectionUtil;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public enum BattlePotionEffectType {
    SPEED,
    SLOW,
    FAST_DIGGING,
    SLOW_DIGGING,
    INCREASE_DAMAGE,
    HEAL,
    HARM,
    JUMP,
    CONFUSION,
    REGENERATION,
    DAMAGE_RESISTANCE,
    FIRE_RESISTANCE,
    WATER_BREATHING,
    INVISIBILITY,
    BLINDNESS,
    NIGHT_VISION,
    HUNGER,
    WEAKNESS,
    POISON,
    WITHER,
    HEALTH_BOOST,
    ABSORPTION,
    SATURATION,
    GLOWING,
    LEVITATION,
    LUCK,
    UNLUCK;

    private PotionEffectType potionEffectType;

    BattlePotionEffectType(){
        potionEffectType = (PotionEffectType) ReflectionUtil.getStaticField(PotionEffectType.class, name());
    }

    @NotNull
    public PotionEffectType asBukkit(){
        return potionEffectType;
    }
}
