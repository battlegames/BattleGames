package dev.anhcraft.abm.api.objects;

import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.ext.BattleItem;
import dev.anhcraft.abm.api.impl.Informative;
import dev.anhcraft.abm.system.ItemTag;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.tags.CustomItemTagContainer;
import org.bukkit.inventory.meta.tags.ItemTagType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class MagazineItem extends BattleItem<Magazine> implements Informative {
    @NotNull
    private AmmoItem ammo = new AmmoItem();
    private int ammoCount;

    public int getAmmoCount() {
        return ammoCount;
    }

    public void setAmmoCount(int ammoCount) {
        this.ammoCount = ammoCount;
    }

    @NotNull
    public AmmoItem getAmmo() {
        return ammo;
    }

    public void setAmmo(@NotNull AmmoItem ammo) {
        Validate.notNull(ammo, "Ammo must be non-null");
        this.ammo = ammo;
    }

    public void resetAmmo() {
        ammoCount = getModel().getAmmunition().get(ammo.getModel());
    }

    @Override
    public void save(CustomItemTagContainer container) {
        container.setCustomTag(ItemTag.MAGAZINE_ID, ItemTagType.STRING, getModel().getId());
        container.setCustomTag(ItemTag.MAGAZINE_AMMO_COUNT, ItemTagType.INTEGER, ammoCount);
        CustomItemTagContainer c = container.getAdapterContext().newTagContainer();
        ammo.save(c);
        container.setCustomTag(ItemTag.MAGAZINE_AMMO, ItemTagType.TAG_CONTAINER, c);
    }

    @Override
    public void load(CustomItemTagContainer container) {
        BattlePlugin.getAPI()
                .getMagazine(container.getCustomTag(ItemTag.MAGAZINE_ID, ItemTagType.STRING))
                .ifPresent(this::setModel);
        Integer a = container.getCustomTag(ItemTag.MAGAZINE_AMMO_COUNT, ItemTagType.INTEGER);
        if(a != null) ammoCount = a;
        CustomItemTagContainer am = container.getCustomTag(ItemTag.MAGAZINE_AMMO, ItemTagType.TAG_CONTAINER);
        if(am != null) ammo.load(am);
    }

    @Override
    public void writeInfo(Map<String, String> map, ConfigurationSection localeConf) {
        Magazine m = getModel();
        map.put("magazine_name", m == null ? localeConf.getString("none_magazine") : m.getName());
        map.put("magazine_ammo_count", m == null ? "0" : Integer.toString(ammoCount));
        map.put("magazine_ammo_capacity", ammo.getModel() == null ? "0" : Integer.toString(getModel().getAmmunition()
                .get(ammo.getModel())));
        if(m != null) m.writeInfo(map, localeConf);
        ammo.writeInfo(map, localeConf);
    }
}
