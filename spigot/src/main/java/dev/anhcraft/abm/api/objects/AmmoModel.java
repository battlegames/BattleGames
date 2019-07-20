package dev.anhcraft.abm.api.objects;

import dev.anhcraft.abif.ABIF;
import dev.anhcraft.abm.api.enums.ItemType;
import dev.anhcraft.abm.api.ext.BattleItemModel;
import dev.anhcraft.abm.utils.info.InfoHolder;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AmmoModel extends BattleItemModel {
    private final List<Bullet> bullets = new ArrayList<>();
    private Skin skin;

    public AmmoModel(@NotNull String id, @NotNull ConfigurationSection conf) {
        super(id, conf);

        String material = conf.getString("skin.material");
        skin = new Skin(material == null ? null : Material.getMaterial(material.toUpperCase()),
                conf.getInt("skin.damage"));

        ConfigurationSection bss = conf.getConfigurationSection("bullets");
        if(bss != null){
            for(String bsk : bss.getKeys(false)){
                ConfigurationSection bs = bss.getConfigurationSection(bsk);
                if(bs == null) continue;
                String ptn = bs.getString("particle.type");
                Particle pt = ptn == null ? Particle.END_ROD : Particle.valueOf(ptn.toUpperCase());
                Object data = null;
                if(pt.getDataType() == Particle.DustOptions.class){
                    data = new Particle.DustOptions(
                            Objects.requireNonNull(bs.getColor("particle.data.dust.color", Color.RED)),
                            (float) bs.getDouble("particle.data.dust.size")
                    );
                } else if(pt.getDataType() == ItemStack.class){
                    ConfigurationSection its = bs.getConfigurationSection("particle.data.item");
                    if(its != null) data = ABIF.load(its);
                }
                bullets.add(new Bullet(bs.getDouble("damage"),
                        bs.getDouble("knockback"), pt,
                        bs.getInt("particle.count", 1),
                        bs.getDouble("particle.offset_x"),
                        bs.getDouble("particle.offset_y"),
                        bs.getDouble("particle.offset_z"),
                        bs.getDouble("particle.speed"), data,
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
