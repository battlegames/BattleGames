package dev.anhcraft.abm.api.objects;

import dev.anhcraft.abif.ABIF;
import dev.anhcraft.abm.api.enums.ItemType;
import dev.anhcraft.abm.api.impl.BattleItemModel;
import dev.anhcraft.abm.api.impl.Informative;
import dev.anhcraft.abm.utils.MathUtil;
import org.apache.commons.lang.Validate;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Ammo implements BattleItemModel, Informative {
    private final List<Bullet> bullets = new ArrayList<>();
    private String id;
    private String name;
    private Skin skin;

    public Ammo(@NotNull String id, @NotNull ConfigurationSection conf) {
        Validate.notNull(id, "Id must be non-null");
        Validate.notNull(conf, "Conf must be non-null");

        this.id = id;
        name = conf.getString("name");
        if(name == null) throw new NullPointerException("Name must be specified");

        String material = conf.getString("skin.material");
        skin = new Skin(material == null ? null : Material.getMaterial(material.toUpperCase()), conf.getInt("skin.damage"));

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
                        bs.getDouble("particle.view_distance")));
            }
        }
    }

    @Override
    @NotNull
    public ItemType getItemType() {
        return ItemType.AMMO;
    }

    @NotNull
    @Override
    public String getId() {
        return id;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
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
    public void writeInfo(Map<String, String> map, ConfigurationSection localeConf) {
        map.put("ammo_name", name);
        map.put("ammo_bullet_count", Integer.toString(bullets.size()));
        map.put("ammo_total_bullet_damage", MathUtil.round(bullets.stream().mapToDouble(Bullet::getDamage).sum(), 3));
        map.put("ammo_total_bullet_knockback", MathUtil.round(bullets.stream().mapToDouble(Bullet::getKnockback).sum(), 3));
        map.put("ammo_avg_bullet_damage", MathUtil.round(bullets.stream().mapToDouble(Bullet::getDamage).average().orElse(0), 3));
        map.put("ammo_avg_bullet_knockback", MathUtil.round(bullets.stream().mapToDouble(Bullet::getKnockback).average().orElse(0), 3));
    }
}
