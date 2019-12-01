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
package dev.anhcraft.battle.api.gui;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import dev.anhcraft.battle.api.gui.struct.Component;
import dev.anhcraft.battle.api.misc.BattleSound;
import dev.anhcraft.battle.api.misc.ConfigurableObject;
import dev.anhcraft.battle.api.misc.info.InfoHolder;
import dev.anhcraft.battle.api.misc.info.Informative;
import dev.anhcraft.confighelper.ConfigHelper;
import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.annotation.IgnoreValue;
import dev.anhcraft.confighelper.annotation.Key;
import dev.anhcraft.confighelper.annotation.Schema;
import dev.anhcraft.confighelper.annotation.Validation;
import dev.anhcraft.confighelper.exception.InvalidValueException;
import dev.anhcraft.jvmkit.utils.Condition;
import dev.anhcraft.jvmkit.utils.MathUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Schema
public class Gui extends ConfigurableObject implements Informative {
    public static final ConfigSchema<Gui> SCHEMA = ConfigSchema.of(Gui.class);

    private final Map<Integer, Component> S2C = new HashMap<>();
    private final Multimap<String, Component> P2C = HashMultimap.create();
    private String id;
    private int size;

    @Key("title")
    @Validation(notNull = true)
    private String title;

    @Key("components")
    @IgnoreValue(ifNull = true)
    private List<Component> components = new ArrayList<>();

    @Key("sound")
    private BattleSound sound;

    public Gui(@NotNull String id) {
        Condition.argNotNull("id", id);
        this.id = id;
    }

    @NotNull
    public String getId() {
        return id;
    }

    public int getSize() {
        return size;
    }

    @NotNull
    public String getTitle() {
        return title;
    }

    @NotNull
    public List<Component> getComponents() {
        return components;
    }

    @Nullable
    public BattleSound getSound() {
        return sound;
    }

    @Nullable
    public Component getComponentAt(int pos) {
        return S2C.get(pos);
    }

    @NotNull
    public Collection<Component> getComponentOf(@NotNull String pagination) {
        return P2C.get(pagination);
    }

    @NotNull
    public Collection<String> getAllPagination() {
        return P2C.keys();
    }

    @Override
    protected @Nullable Object conf2schema(@Nullable Object value, ConfigSchema.Entry entry) {
        if(value != null) {
            switch (entry.getKey()) {
                case "components": {
                    ConfigurationSection cs = (ConfigurationSection) value;
                    List<Component> components = new ArrayList<>();
                    int highestSlot = 0;
                    for(String s : cs.getKeys(false)){
                        try {
                            Component c = ConfigHelper.readConfig(cs.getConfigurationSection(s), Component.SCHEMA, new Component(s));
                            for(Integer i : c.getSlots()){
                                Component prev = S2C.put(i, c);
                                // if this slot exists in previous component, we will remove it
                                if(prev != null){
                                    prev.getSlots().remove(i);
                                }
                            }
                            if(c.getPagination() != null){
                                if(!P2C.put(c.getPagination(), c)){
                                    Bukkit.getLogger().warning("Pagination should not be duplicated! `"+c.getPagination()+"` in component: "+c.getId());
                                }
                            }
                            int hs = Collections.max(c.getSlots());
                            if(hs > highestSlot) highestSlot = hs;
                            components.add(c);
                        } catch (InvalidValueException e) {
                            e.printStackTrace();
                        }
                    }
                    size = (int) MathUtil.nextMultiple(highestSlot, 9);
                    if(size > 54){
                        Bukkit.getLogger().warning("The inventory size is out of bound: "+size);
                        size = 54;
                    }
                    return components;
                }
                case "sound": {
                    return new BattleSound((String) value);
                }
            }
        }
        return value;
    }

    @Override
    protected @Nullable Object schema2conf(@Nullable Object value, ConfigSchema.Entry entry) {
        if(value != null) {
            switch (entry.getKey()) {
                case "components": {
                    ConfigurationSection parent = new YamlConfiguration();
                    int i = 0;
                    for(Component cpn : (List<Component>) value){
                        YamlConfiguration c = new YamlConfiguration();
                        ConfigHelper.writeConfig(c, Component.SCHEMA, cpn);
                        parent.set(String.valueOf(i++), c);
                    }
                    return parent;
                }
                case "sound": {
                    return value.toString();
                }
            }
        }
        return value;
    }

    @Override
    public void inform(@NotNull InfoHolder holder) {
        holder.inform("id", id).inform("size", size);
    }
}
