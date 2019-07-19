package dev.anhcraft.abm.api.objects;

import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.enums.ItemType;
import dev.anhcraft.abm.api.impl.BattleItemModel;
import dev.anhcraft.abm.api.impl.Informative;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Magazine implements BattleItemModel, Informative {
    private final Map<Ammo, Integer> ammunition = new HashMap<>();
    private String id;
    private String name;
    private Skin skin;

    public Magazine(@NotNull String id, @NotNull ConfigurationSection conf) {
        Validate.notNull(id, "Id must be non-null");
        Validate.notNull(conf, "Conf must be non-null");

        this.id = id;
        name = conf.getString("name");
        if(name == null) throw new NullPointerException("Name must be specified");

        String material = conf.getString("skin.material");
        skin = new Skin(material == null ? null : Material.getMaterial(material.toUpperCase()), conf.getInt("skin.damage"));

        ConfigurationSection am = conf.getConfigurationSection("ammo");
        if(am != null){
            for(String a : am.getKeys(false)) BattlePlugin.getAPI().getAmmo(a).ifPresent(ammo -> ammunition.put(ammo, am.getInt(a)));
        }
    }

    @Override
    @NotNull
    public ItemType getItemType() {
        return ItemType.MAGAZINE;
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
    public Skin getSkin() {
        return skin;
    }

    @NotNull
    public Map<Ammo, Integer> getAmmunition() {
        return ammunition;
    }

    @Override
    public void writeInfo(Map<String, String> map, ConfigurationSection localeConf) {
        map.put("magazine_name", name);
    }
}
