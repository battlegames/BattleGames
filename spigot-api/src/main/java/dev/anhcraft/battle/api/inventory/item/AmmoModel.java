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
package dev.anhcraft.battle.api.inventory.item;

import dev.anhcraft.battle.utils.info.InfoHolder;
import dev.anhcraft.confighelper.ConfigHelper;
import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.annotation.Explanation;
import dev.anhcraft.confighelper.annotation.IgnoreValue;
import dev.anhcraft.confighelper.annotation.Key;
import dev.anhcraft.confighelper.annotation.Schema;
import dev.anhcraft.confighelper.exception.InvalidValueException;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Schema
public class AmmoModel extends SingleSkinItem implements Attachable {
    public static final ConfigSchema<AmmoModel> SCHEMA = ConfigSchema.of(AmmoModel.class);

    @Key("bullets")
    @Explanation("Define bullets in this ammunition")
    @IgnoreValue(ifNull = true)
    private List<Ammo.Bullet> bullets = new ArrayList<>();

    private double sumBulletDamage;
    private double avgBulletDamage;
    private double sumBulletKnockback;
    private double avgBulletKnockback;

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
        .inform("total_bullet_damage", sumBulletDamage)
        .inform("total_bullet_knockback", sumBulletKnockback)
        .inform("avg_bullet_damage", avgBulletDamage)
        .inform("avg_bullet_knockback", avgBulletKnockback);
    }

    @Override
    public ItemType[] getHolderTypes() {
        return new ItemType[]{
                ItemType.MAGAZINE
        };
    }

    @Override
    protected @Nullable Object conf2schema(@Nullable Object value, ConfigSchema.Entry entry) {
        if(value != null && entry.getKey().equals("bullets")){
            ConfigurationSection cs = (ConfigurationSection) value;
            List<Ammo.Bullet> bullets = new ArrayList<>();
            Set<String> keys = cs.getKeys(false);
            for(String s : keys){
                try {
                    Ammo.Bullet b = ConfigHelper.readConfig(cs.getConfigurationSection(s), Ammo.Bullet.SCHEMA);
                    bullets.add(b);
                    sumBulletDamage += b.getDamage();
                    sumBulletKnockback += b.getKnockback();
                    avgBulletDamage += b.getDamage() / keys.size();
                    avgBulletKnockback += b.getKnockback() / keys.size();
                } catch (InvalidValueException e) {
                    e.printStackTrace();
                }
            }
            return bullets;
        }
        return value;
    }

    @Override
    protected @Nullable Object schema2conf(@Nullable Object value, ConfigSchema.Entry entry) {
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
