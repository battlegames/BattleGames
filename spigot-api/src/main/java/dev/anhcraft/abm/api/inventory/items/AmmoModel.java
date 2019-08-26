package dev.anhcraft.abm.api.inventory.items;

import dev.anhcraft.abm.api.entity.Bullet;
import dev.anhcraft.abm.api.misc.Skin;
import dev.anhcraft.abm.api.misc.info.InfoHolder;
import dev.anhcraft.abm.utils.EnumUtil;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AmmoModel extends BattleItemModel {
    private final List<Bullet> bullets = new ArrayList<>();
    private Skin skin;

    public AmmoModel(@NotNull String id, @NotNull ConfigurationSection conf) {
        super(id, conf);

        String material = conf.getString("skin.material");
        skin = new Skin(material == null ? null : EnumUtil.getEnum(Material.values(), material),
                conf.getInt("skin.damage"));

        ConfigurationSection bss = conf.getConfigurationSection("bullets");
        if(bss != null){
            for(String bsk : bss.getKeys(false)){
                ConfigurationSection bs = bss.getConfigurationSection(bsk);
                if(bs == null) continue;
                String ptn = bs.getString("particle.type");
                Particle pt = ptn == null ? Particle.END_ROD : EnumUtil.getEnum(Particle.values(), ptn);
                bullets.add(new Bullet(bs.getDouble("damage"),
                        bs.getDouble("knockback"), pt,
                        bs.getInt("particle.count", 1),
                        bs.getDouble("particle.offset_x"),
                        bs.getDouble("particle.offset_y"),
                        bs.getDouble("particle.offset_z"),
                        bs.getDouble("particle.speed"),
                        bs.getDouble("particle.view_distance", 50)));
            }
        }
    }

    @Override
    public @NotNull ItemType getItemType() {
        return ItemType.AMMO;
    }

    @NotNull
    public List<Bullet> getBullets() {
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
        .inform("total_bullet_damage", bullets.stream().mapToDouble(Bullet::getDamage).sum())
        .inform("total_bullet_knockback", bullets.stream().mapToDouble(Bullet::getKnockback).sum())
        .inform("avg_bullet_damage", bullets.stream().mapToDouble(Bullet::getDamage).average().orElse(0))
        .inform("avg_bullet_knockback", bullets.stream().mapToDouble(Bullet::getKnockback).average().orElse(0));
    }
}
