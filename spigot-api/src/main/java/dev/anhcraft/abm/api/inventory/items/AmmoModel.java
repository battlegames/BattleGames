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

import dev.anhcraft.abm.api.misc.ParticleEffect;
import dev.anhcraft.abm.api.misc.Skin;
import dev.anhcraft.abm.api.misc.info.InfoHolder;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AmmoModel extends BattleItemModel implements Attachable {
    private List<Ammo.Bullet> bullets = new ArrayList<>();
    private Skin skin;

    public AmmoModel(@NotNull String id, @NotNull ConfigurationSection conf) {
        super(id, conf);

        skin = new Skin(conf.getConfigurationSection("skin"));

        ConfigurationSection bss = conf.getConfigurationSection("bullets");
        if(bss != null){
            for(String bsk : bss.getKeys(false)){
                ConfigurationSection bs = bss.getConfigurationSection(bsk);
                if(bs == null) continue;
                ConfigurationSection particle = bs.getConfigurationSection("particle");
                bullets.add(new Ammo.Bullet(
                        bs.getDouble("damage"),
                        bs.getDouble("knockback"),
                        particle == null ? null : new ParticleEffect(particle))
                );
            }
        }
        bullets = Collections.unmodifiableList(bullets);
    }

    @Override
    public @NotNull ItemType getItemType() {
        return ItemType.AMMO;
    }

    @NotNull
    public List<Ammo.Bullet> getBullets() {
        return bullets;
    }

    @NotNull
    public Skin getSkin() {
        return skin;
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
}
