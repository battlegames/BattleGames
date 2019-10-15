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

import dev.anhcraft.confighelper.ConfigHelper;
import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.annotation.Explanation;
import dev.anhcraft.confighelper.annotation.IgnoreValue;
import dev.anhcraft.confighelper.annotation.Key;
import dev.anhcraft.confighelper.annotation.Schema;
import dev.anhcraft.confighelper.exception.InvalidValueException;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Schema
public class BattleFirework extends ConfigurableObject {
    public static ConfigSchema<BattleFirework> SCHEMA = ConfigSchema.of(BattleFirework.class);

    @Key("effects")
    @Explanation("The firework's effects")
    @IgnoreValue(ifNull = true)
    private List<BattleFireworkEffect> effects = new ArrayList<>();

    @Key("power")
    @Explanation("The firework's power")
    private int power;

    @NotNull
    public List<BattleFireworkEffect> getEffects() {
        return effects;
    }

    public int getPower() {
        return power;
    }

    public void spawn(@NotNull Location location){
        Firework fw = location.getWorld().spawn(location, Firework.class);
        FireworkMeta fwm = fw.getFireworkMeta();
        fwm.setPower(power);
        for (BattleFireworkEffect bfe : effects){
            fwm.addEffect(bfe.getFireworkEffect());
        }
        fw.setFireworkMeta(fwm);
    }

    @Override
    protected @Nullable Object conf2schema(@Nullable Object value, ConfigSchema.Entry entry) {
        if(value != null && entry.getKey().equals("effects")){
            ConfigurationSection cs = (ConfigurationSection) value;
            List<BattleFireworkEffect> list = new ArrayList<>();
            for(String s : cs.getKeys(false)){
                try {
                    list.add(ConfigHelper.readConfig(cs.getConfigurationSection(s), BattleFireworkEffect.SCHEMA));
                } catch (InvalidValueException e) {
                    e.printStackTrace();
                }
            }
            return list;
        }
        return value;
    }

    @Override
    protected @Nullable Object schema2conf(@Nullable Object value, ConfigSchema.Entry entry) {
        if(value != null && entry.getKey().equals("effects")){
            ConfigurationSection parent = new YamlConfiguration();
            int i = 0;
            for(BattleFireworkEffect b : (List<BattleFireworkEffect>) value){
                YamlConfiguration c = new YamlConfiguration();
                ConfigHelper.writeConfig(c, BattleFireworkEffect.SCHEMA, b);
                parent.set(String.valueOf(i++), c);
            }
            return parent;
        }
        return value;
    }
}
