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
import dev.anhcraft.confighelper.utils.EnumUtil;
import dev.anhcraft.jvmkit.utils.Condition;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@Schema
public class BattleEffect extends ConfigurableObject {
    public static final ConfigSchema<BattleEffect> SCHEMA = ConfigSchema.of(BattleEffect.class);

    @Key("particle")
    @Explanation("The particle that used to make up this effect")
    private BattleParticle particle;

    @Key("block_effect")
    @Explanation("Fake block effect")
    private FakeBlockEffect blockEffect;

    @Key("type")
    @Explanation("The effect's type")
    @Validation(notNull = true)
    @PrettyEnum
    private EffectType type;

    @Key("options")
    @Explanation({
            "Options for customizing this effect",
            "Example:",
            "<code>repeat_delay: 20",
            "repeat_times: 5</code>"
    })
    @IgnoreValue(ifNull = true)
    private Map<EffectOption, Object> options = new HashMap<>();

    @NotNull
    public Map<EffectOption, Object> getOptions() {
        return options;
    }

    @NotNull
    public EffectType getType() {
        return type;
    }

    @Nullable
    public BattleParticle getParticle() {
        return particle;
    }

    @Nullable
    public FakeBlockEffect getBlockEffect() {
        return blockEffect;
    }

    @Nullable
    protected Object conf2schema(@Nullable Object value, ConfigSchema.Entry entry){
        if(value != null && entry.getKey().equals("options")){
            Map<EffectOption, Object> options = new HashMap<>();
            ConfigurationSection cs = (ConfigurationSection) value;
            for(String k : cs.getKeys(false)){
                EffectOption eo = (EffectOption) EnumUtil.findEnum(EffectOption.class, k);
                if(eo == null) continue;
                options.put(eo, cs.get(k));
            }
            return options;
        }
        return value;
    }

    @Nullable
    protected Object schema2conf(@Nullable Object value, ConfigSchema.Entry entry){
        if(value != null && entry.getKey().equals("options")){
            Map<EffectOption, Object> options = (Map<EffectOption, Object>) value;
            ConfigurationSection cs = new YamlConfiguration();
            for(Map.Entry<EffectOption, Object> ent : options.entrySet()){
                cs.set(ent.getKey().name().toLowerCase(), ent.getValue());
            }
            return cs;
        }
        return value;
    }

    public void spawn(@NotNull Location location){
        Condition.argNotNull("location", location);
        if(particle != null){
            particle.spawn(location);
        }
        if(blockEffect != null){
            blockEffect.spawn(location);
        }
    }
}
