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

package dev.anhcraft.battle.api;

import dev.anhcraft.battle.api.effect.potion.BattlePotionEffect;
import dev.anhcraft.battle.impl.Informative;
import dev.anhcraft.battle.utils.info.InfoHolder;
import dev.anhcraft.config.annotations.*;
import dev.anhcraft.jvmkit.utils.Condition;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldMayBeFinal")
@Configurable
public class Perk implements Informative {
    @Setting
    @Virtual
    private String id;

    @Setting
    @Description("This perk's name")
    @Validation(notNull = true)
    private String name;

    @Setting
    @Path("executions.give_effects")
    @Description("Potion effects to be applied on the player")
    @Validation(notNull = true, silent = true)
    @Example({
            "executions:",
            "  give_effects:",
            "    '1':",
            "      type: speed",
            "      amplifier: 1",
            "      duration: 99999",
            "      particles: false"
    })
    private List<BattlePotionEffect> potionEffects = new ArrayList<>();

    @Setting
    @Path("executions.set_health")
    @Description("Set the player's health")
    private int newHealth;

    @Setting
    @Path("executions.set_food_level")
    @Description("Set the player's food level")
    private int newFoodLevel;

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public List<BattlePotionEffect> getPotionEffects() {
        return potionEffects;
    }

    public int getNewHealth() {
        return newHealth;
    }

    public int getNewFoodLevel() {
        return newFoodLevel;
    }

    public void give(@NotNull Player player) {
        Condition.argNotNull("player", player);
        for (BattlePotionEffect pe : potionEffects) {
            pe.give(player);
        }
        if (newHealth > 0) {
            player.setHealth(Math.min(newHealth, player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
        }
        if (newFoodLevel > 0) {
            player.setFoodLevel(newFoodLevel);
        }
    }

    @Override
    public void inform(@NotNull InfoHolder holder) {
        holder.inform("id", id)
                .inform("name", name)
                .inform("effects", potionEffects.size())
                .inform("new_health", newHealth)
                .inform("new_food_level", newFoodLevel);
    }
}
