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
import dev.anhcraft.config.annotations.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@SuppressWarnings("FieldMayBeFinal")
@Configurable
public class AmmoModel extends SingleSkinItem implements Attachable {
    @Setting
    @Description("Define bullets in this ammunition")
    @Validation(notNull = true, silent = true)
    @Example({
            "bullets:",
            "  '1':",
            "    damage: 21",
            "    knockback: 0.1",
            "    penetration_power: 10",
            "    particle:",
            "      type: end_rod"
    })
    private Map<String, Ammo.Bullet> bullets = new HashMap<>();

    @Setting
    @Path("reload_delay")
    @Description({
            "The time needed to put a single ammo into magazines.",
            "The reloading time of a magazine (or a gun with magazine attached) is",
            "<b>&lt;reload_delay&gt; * &lt;remaining ammo&gt; (ticks)</b>",
            "or <b>&lt;reload_delay&gt; * &lt;remaining ammo&gt; / 20 (seconds)</b>"
    })
    private long reloadDelay = 1;

    private double sumBulletDamage;
    private double avgBulletDamage;
    private double sumBulletKnockback;
    private double avgBulletKnockback;
    private double sumBulletPenetration;
    private double avgBulletPenetration;

    public AmmoModel(@NotNull String id) {
        super(id);
    }

    @Override
    public @NotNull ItemType getItemType() {
        return ItemType.AMMO;
    }

    @NotNull
    public Collection<Ammo.Bullet> getBullets() {
        return bullets.values();
    }

    public long getReloadDelay() {
        return reloadDelay;
    }

    @Override
    public void inform(@NotNull InfoHolder holder) {
        super.inform(holder);
        holder.inform("bullet_count", bullets.size())
                .inform("total_bullet_damage", sumBulletDamage)
                .inform("total_bullet_knockback", sumBulletKnockback)
                .inform("total_bullet_penetration", sumBulletPenetration)
                .inform("avg_bullet_damage", avgBulletDamage)
                .inform("avg_bullet_knockback", avgBulletKnockback)
                .inform("avg_bullet_penetration", avgBulletPenetration)
                .inform("reload_delay", reloadDelay);
    }

    @Override
    public ItemType[] getHolderTypes() {
        return new ItemType[]{
                ItemType.MAGAZINE
        };
    }

    @PostHandler
    private void handle(){
        for (Ammo.Bullet b : bullets.values()) {
            sumBulletDamage += b.getDamage();
            sumBulletKnockback += b.getKnockback();
            sumBulletPenetration += b.getPenetrationPower();
            avgBulletDamage += b.getDamage() / bullets.size();
            avgBulletKnockback += b.getKnockback() / bullets.size();
            avgBulletPenetration += b.getPenetrationPower() / (double) bullets.size();
        }
    }
}
