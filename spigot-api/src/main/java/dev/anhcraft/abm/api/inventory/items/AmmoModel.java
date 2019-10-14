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
package dev.anhcraft.abm.api.inventory.items;

import dev.anhcraft.abm.api.misc.info.InfoHolder;
import dev.anhcraft.confighelper.ConfigHelper;
import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.annotation.Explanation;
import dev.anhcraft.confighelper.annotation.IgnoreValue;
import dev.anhcraft.confighelper.annotation.Key;
import dev.anhcraft.confighelper.annotation.Schema;
import dev.anhcraft.confighelper.exception.InvalidValueException;
import dev.anhcraft.confighelper.impl.TwoWayMiddleware;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Schema
public class AmmoModel extends SingleSkinItem implements Attachable, TwoWayMiddleware {
    public static final ConfigSchema<AmmoModel> SCHEMA = ConfigSchema.of(AmmoModel.class);

    @Key("bullets")
    @Explanation("Define bullets in this ammunition")
    @IgnoreValue(ifNull = true)
    private List<Ammo.Bullet> bullets = new ArrayList<>();

    public AmmoModel(@NotNull String id) {
        super(id);
    }

    @Override
    public @NotNull ItemType getItemType() {
        return ItemType.AMMO;
    }

    @NotNull
    public List<Ammo.Bullet> getBullets() {
        return bullets;
    }

    @Override
    public void inform(@NotNull InfoHolder holder){
        super.inform(holder);
        holder.inform("bullet_count", bullets.size())
        .inform("total_bullet_damage", bullets.stream().mapToDouble(Ammo.Bullet::getDamage).sum())
        .inform("total_bullet_knockback", bullets.stream().mapToDouble(Ammo.Bullet::getKnockback).sum())
        .inform("avg_bullet_damage", bullets.stream().mapToDouble(Ammo.Bullet::getDamage).average().orElse(0))
        .inform("avg_bullet_knockback", bullets.stream().mapToDouble(Ammo.Bullet::getKnockback).average().orElse(0));
    }

    @Override
    public ItemType[] getHolderTypes() {
        return new ItemType[]{
                ItemType.MAGAZINE
        };
    }

    @Override
    protected @Nullable Object readConfig(ConfigSchema.Entry entry, @Nullable Object value) {
        if(value != null && entry.getKey().equals("bullets")){
            ConfigurationSection cs = (ConfigurationSection) value;
            List<Ammo.Bullet> bullets = new ArrayList<>();
            for(String s : cs.getKeys(false)){
                try {
                    bullets.add(ConfigHelper.readConfig(cs.getConfigurationSection(s), Ammo.Bullet.SCHEMA));
                } catch (InvalidValueException e) {
                    e.printStackTrace();
                }
            }
            return bullets;
        }
        return value;
    }

    @Override
    protected @Nullable Object writeConfig(ConfigSchema.Entry entry, @Nullable Object value) {
        if(value != null && entry.getKey().equals("bullets")){
            ConfigurationSection parent = new YamlConfiguration();
            int i = 0;
            for(Ammo.Bullet b : (List<Ammo.Bullet>) value){
                YamlConfiguration c = new YamlConfiguration();
                ConfigHelper.writeConfig(c, Ammo.Bullet.SCHEMA, b);
                parent.set(String.valueOf(i++), c);
            }
            return parent;
        }
        return value;
    }
}
