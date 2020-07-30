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
import dev.anhcraft.battle.utils.ConfigurableObject;
import dev.anhcraft.battle.utils.info.InfoHolder;
import dev.anhcraft.confighelper.ConfigHelper;
import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.annotation.*;
import dev.anhcraft.confighelper.exception.InvalidValueException;
import dev.anhcraft.jvmkit.utils.Condition;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldMayBeFinal")
@Schema
public class Perk extends ConfigurableObject implements Informative {
    public static final ConfigSchema<Perk> SCHEMA = ConfigSchema.of(Perk.class);

    private final String id;

    @Key("name")
    @Explanation("This perk's name")
    @IgnoreValue(ifNull = true)
    private String name;

    @Key("executions.give_effects")
    @Explanation("Potion effects to be applied on the player")
    @IgnoreValue(ifNull = true, ifEmptyList = true)
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

    @Key("executions.set_health")
    @Explanation("Set the player's health")
    private int newHealth;

    @Key("executions.set_food_level")
    @Explanation("Set the player's food level")
    private int newFoodLevel;

    public Perk(@NotNull String id) {
        Condition.argNotNull("id", id);
        this.id = id;
        name = id;
    }

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

    public void give(@NotNull Player player){
        Condition.argNotNull("player", player);
        for(BattlePotionEffect pe : potionEffects){
            pe.give(player);
        }
        if(newHealth > 0){
            player.setHealth(Math.min(newHealth, player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
        }
        if(newFoodLevel > 0){
            player.setFoodLevel(newFoodLevel);
        }
    }

    @Override
    protected @Nullable Object conf2schema(@Nullable Object value, ConfigSchema.Entry entry) {
        if(value != null && entry.getKey().equals("executions.give_effects")){
            ConfigurationSection cs = (ConfigurationSection) value;
            List<BattlePotionEffect> effects = new ArrayList<>();
            for(String s : cs.getKeys(false)){
                try {
                    effects.add(ConfigHelper.readConfig(cs.getConfigurationSection(s), BattlePotionEffect.SCHEMA));
                } catch (InvalidValueException e) {
                    e.printStackTrace();
                }
            }
            return effects;
        }
        return value;
    }

    @Override
    protected @Nullable Object schema2conf(@Nullable Object value, ConfigSchema.Entry entry) {
        if(value != null && entry.getKey().equals("executions.give_effects")){
            ConfigurationSection parent = new YamlConfiguration();
            int i = 0;
            for(BattlePotionEffect effect : (List<BattlePotionEffect>) value){
                YamlConfiguration c = new YamlConfiguration();
                ConfigHelper.writeConfig(c, BattlePotionEffect.SCHEMA, effect);
                parent.set(String.valueOf(i++), c);
            }
            return parent;
        }
        return value;
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
