package dev.anhcraft.abm.api.inventory.items;

import dev.anhcraft.abm.api.APIProvider;
import dev.anhcraft.abm.api.misc.info.InfoHolder;
import dev.anhcraft.abm.api.misc.Skin;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class GunModel extends WeaponModel {
    private boolean dualWielding;
    private Skin primarySkin;
    private Skin secondarySkin;
    private double weight;
    private MagazineModel defaultMagazine;
    private int magazineMaxCapacity;

    public GunModel(@NotNull String id, @NotNull ConfigurationSection conf) {
        super(id, conf);
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
            defaultMagazine = APIProvider.get().getMagazineModel(defaultMag)
                    .orElseThrow((Supplier<Throwable>) () -> new NullPointerException("MagazineModel did not exist"));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Override
    public @NotNull ItemType getItemType() {
        return ItemType.GUN;
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
    public MagazineModel getDefaultMagazine() {
        return defaultMagazine;
    }

    public int getMagazineMaxCapacity() {
        return magazineMaxCapacity;
    }

    @Override
    public void inform(@NotNull InfoHolder holder){
        super.inform(holder);
        holder.inform("weight", weight)
                .inform("dual_wielding", dualWielding)
                .inform("max_magazine_capacity", getMagazineMaxCapacity());
        holder.link(defaultMagazine.collectInfo("default_"));
    }
}
