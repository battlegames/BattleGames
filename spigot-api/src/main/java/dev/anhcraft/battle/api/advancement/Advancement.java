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

package dev.anhcraft.battle.api.advancement;

import dev.anhcraft.battle.impl.Informative;
import dev.anhcraft.battle.utils.ConfigurableObject;
import dev.anhcraft.battle.utils.info.InfoHolder;
import dev.anhcraft.battle.utils.info.State;
import dev.anhcraft.confighelper.ConfigHelper;
import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.annotation.Key;
import dev.anhcraft.confighelper.annotation.PrettyEnum;
import dev.anhcraft.confighelper.annotation.Schema;
import dev.anhcraft.confighelper.annotation.Validation;
import dev.anhcraft.confighelper.exception.InvalidValueException;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Schema
public class Advancement extends ConfigurableObject implements Informative {
    public static final ConfigSchema<Advancement> SCHEMA = ConfigSchema.of(Advancement.class);

    private String id;

    @Key("type")
    @Validation(notNull = true)
    private String type;

    @Key("name")
    @Validation(notNull = true)
    private String name;

    @Key("description")
    private List<String> description;

    @Key("icon")
    @Validation(notNull = true)
    @PrettyEnum
    private Material icon;

    @Key("inherit_progress")
    private boolean inheritProgress;

    @Key("progression")
    @Validation(notNull = true)
    private SortedSet<Progression> progression;

    private double maxAmount;

    public Advancement(@NotNull String id) {
        this.id = id;
    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public String getType() {
        return type;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @Nullable
    public List<String> getDescription() {
        return description;
    }

    @NotNull
    public Material getIcon() {
        return icon;
    }

    public boolean getInheritProgress() {
        return inheritProgress;
    }

    @NotNull
    public SortedSet<Progression> getProgression() {
        return progression;
    }

    public double getMaxAmount() {
        return maxAmount;
    }

    @Override
    public void inform(@NotNull InfoHolder holder) {
        holder.inform("id", id)
                .inform("type", type)
                .inform("name", name)
                .inform("description", description)
                .inform("inherit", State.ENABLED.inCaseOf(inheritProgress));
    }

    @Override
    protected @Nullable Object conf2schema(@Nullable Object value, ConfigSchema.Entry entry) {
        if(value != null && entry.getKey().equals("progression")){
            ConfigurationSection cs = (ConfigurationSection) value;
            SortedSet<Progression> progressions = new TreeSet<>(Comparator.naturalOrder());
            Set<String> keys = cs.getKeys(false);
            try {
                for (String s : keys) {
                    Progression p = ConfigHelper.readConfig(cs.getConfigurationSection(s), Progression.SCHEMA);
                    maxAmount = Math.max(maxAmount, p.getAmount());
                    progressions.add(p);
                }
            } catch (InvalidValueException e) {
                e.printStackTrace();
            }
            return progressions;
        }
        return value;
    }

    @Override
    protected @Nullable Object schema2conf(@Nullable Object value, ConfigSchema.Entry entry) {
        if(value != null && entry.getKey().equals("progression")){
            ConfigurationSection parent = new YamlConfiguration();
            int i = 0;
            for(Progression p : (Set<Progression>) value){
                YamlConfiguration c = new YamlConfiguration();
                ConfigHelper.writeConfig(c, Progression.SCHEMA, p);
                parent.set(String.valueOf(i++), c);
            }
            return parent;
        }
        return value;
    }
}
