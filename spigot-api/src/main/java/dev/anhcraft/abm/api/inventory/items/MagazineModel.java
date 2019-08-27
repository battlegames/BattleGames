package dev.anhcraft.abm.api.inventory.items;

import dev.anhcraft.abm.api.APIProvider;
import dev.anhcraft.abm.api.misc.Skin;
import dev.anhcraft.abm.utils.EnumUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class MagazineModel extends BattleItemModel implements Attachable {
    private final Map<AmmoModel, Integer> ammunition = new HashMap<>();
    private Skin skin;

    public MagazineModel(@NotNull String id, @NotNull ConfigurationSection conf) {
        super(id, conf);

        String material = conf.getString("skin.material");
        skin = new Skin(material == null ? null : EnumUtil.getEnum(Material.values(), material), conf.getInt("skin.damage"));

        ConfigurationSection am = conf.getConfigurationSection("ammo");
        if(am != null){
            for(String a : am.getKeys(false)) APIProvider.get().getAmmoModel(a).ifPresent(ammo -> ammunition.put(ammo, am.getInt(a)));
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

    @Override
    public ItemType[] getHolderTypes() {
        return new ItemType[]{
                ItemType.GUN
        };
    }
}
