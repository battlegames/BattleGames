package dev.anhcraft.abm.api.objects;

import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.enums.ItemType;
import dev.anhcraft.abm.api.ext.BattleItemModel;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class MagazineModel extends BattleItemModel {
    private final Map<AmmoModel, Integer> ammunition = new HashMap<>();
    private Skin skin;

    public MagazineModel(@NotNull String id, @NotNull ConfigurationSection conf) {
        super(id, conf);

        String material = conf.getString("skin.material");
        skin = new Skin(material == null ? null : Material.getMaterial(material.toUpperCase()), conf.getInt("skin.damage"));

        ConfigurationSection am = conf.getConfigurationSection("ammo");
        if(am != null){
            for(String a : am.getKeys(false)) BattlePlugin.getAPI().getAmmoModel(a).ifPresent(ammo -> ammunition.put(ammo, am.getInt(a)));
        }
    }

    @Override
    public @NotNull ItemType getItemType() {
        return ItemType.MAGAZINE;
    }

    @NotNull
    public Skin getSkin() {
        return skin;
    }

    @NotNull
    public Map<AmmoModel, Integer> getAmmunition() {
        return ammunition;
    }
}
