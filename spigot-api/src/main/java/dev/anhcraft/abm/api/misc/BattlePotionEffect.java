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

import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.annotation.*;
import dev.anhcraft.jvmkit.utils.Condition;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Schema
public class BattlePotionEffect extends ConfigurableObject {
    public static final ConfigSchema<BattlePotionEffect> SCHEMA = ConfigSchema.of(BattlePotionEffect.class);

    @Key("type")
    @Explanation("The type of the effect")
    @Validation(notNull = true)
    @PrettyEnum
    private BattlePotionEffectType type;

    @Key("duration")
    @Explanation("How long does the effect remain")
    private int duration;

    @Key("amplifier")
    @Explanation("Boost the effect to a higher level")
    private int amplifier;

    @Key("ambient")
    @Explanation("Makes potion effect produce more, translucent, particles")
    private boolean ambient;

    @Key("particles")
    @Explanation("Shows particle effects or not")
    private boolean particles;

    @Key("color")
    @Explanation("Set the color for the particles")
    @PrettyEnum
    private BattleColor color;

    @NotNull
    public BattlePotionEffectType getType() {
        return type;
    }

    public int getDuration() {
        return duration;
    }

    public int getAmplifier() {
        return amplifier;
    }

    public boolean isAmbient() {
        return ambient;
    }

    public boolean hasParticles() {
        return particles;
    }

    @Nullable
    public BattleColor getColor() {
        return color;
    }

    @NotNull
    public PotionEffect build(){
        return new PotionEffect(type.asBukkit(), duration, amplifier, ambient, particles, color.asBukkit());
    }

    public void give(@NotNull Player player){
        Condition.argNotNull("player", player);
        player.addPotionEffect(build(), true);
    }
}
