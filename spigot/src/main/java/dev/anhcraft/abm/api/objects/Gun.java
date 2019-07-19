package dev.anhcraft.abm.api.objects;

import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.enums.ItemType;
import dev.anhcraft.abm.api.impl.Informative;
import dev.anhcraft.abm.api.impl.Weapon;
import dev.anhcraft.abm.utils.MathUtil;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Supplier;

public class Gun implements Weapon, Informative {
    private String id;
    private String name;
    private boolean dualWielding;
    private Skin primarySkin;
    private Skin secondarySkin;
    private double weight;
    private Magazine defaultMagazine;
    private int magazineMaxCapacity;

    public Gun(@NotNull String id, @NotNull ConfigurationSection conf) {
        Validate.notNull(id, "Id must be non-null");
        Validate.notNull(conf, "Conf must be non-null");

        this.id = id;
        name = conf.getString("name");
        if(name == null) throw new NullPointerException("Name must be specified");

        dualWielding = conf.getBoolean("dual_wielding");

        String primaryMaterial = conf.getString("skin.primary.material");
        primarySkin = new Skin(primaryMaterial == null ? null : Material.getMaterial(primaryMaterial.toUpperCase()), conf.getInt("skin.primary.damage"));
        String secondaryMaterial = conf.getString("skin.secondary.material");
        secondarySkin = new Skin(secondaryMaterial == null ? null : Material.getMaterial(secondaryMaterial.toUpperCase()), conf.getInt("skin.secondary.damage"));

        weight = conf.getDouble("weight");
        magazineMaxCapacity = conf.getInt("magazine.max_capacity");

        String defaultMag = conf.getString("magazine.default");
        if(defaultMag == null) throw new NullPointerException("Default magazine must be specified");
        try {
            defaultMagazine = BattlePlugin.getAPI().getMagazine(defaultMag)
                    .orElseThrow((Supplier<Throwable>) () -> new NullPointerException("Magazine did not exist"));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Override
    @NotNull
    public ItemType getItemType() {
        return ItemType.GUN;
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

    public boolean isDualWielding() {
        return dualWielding;
    }

    @NotNull
    public Skin getPrimarySkin() {
        return primarySkin;
    }

    @NotNull
    public Skin getSecondarySkin() {
        return secondarySkin;
    }

    public double getWeight() {
        return weight;
    }

    @NotNull
    public Magazine getDefaultMagazine() {
        return defaultMagazine;
    }

    public int getMagazineMaxCapacity() {
        return magazineMaxCapacity;
    }

    @Override
    public void writeInfo(Map<String, String> map, ConfigurationSection localeConf) {
        map.put("gun_name", name);
        map.put("gun_weight", MathUtil.round(weight, 3));
        map.put("gun_max_magazine_capacity", Integer.toString(getMagazineMaxCapacity()));
        map.put("gun_default_magazine_name", defaultMagazine.getName());
    }
}
