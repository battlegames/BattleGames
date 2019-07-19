package dev.anhcraft.abm.api.objects;

import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.ext.WeaponItem;
import dev.anhcraft.abm.api.impl.Informative;
import dev.anhcraft.abm.system.ItemTag;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.tags.CustomItemTagContainer;
import org.bukkit.inventory.meta.tags.ItemTagType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class GunItem extends WeaponItem<Gun> implements Informative {
    @NotNull
    private MagazineItem magazine = new MagazineItem();

    @NotNull
    public MagazineItem getMagazine() {
        return magazine;
    }

    public void setMagazine(@NotNull MagazineItem magazine) {
        Validate.notNull(magazine, "Magazine must be non-null");
        this.magazine = magazine;
    }

    @Override
    public void save(CustomItemTagContainer container) {
        container.setCustomTag(ItemTag.GUN_ID, ItemTagType.STRING, getModel().getId());
        CustomItemTagContainer c = container.getAdapterContext().newTagContainer();
        magazine.save(c);
        container.setCustomTag(ItemTag.GUN_MAGAZINE, ItemTagType.TAG_CONTAINER, c);
    }

    @Override
    public void load(CustomItemTagContainer container) {
        BattlePlugin.getAPI()
                .getGun(container.getCustomTag(ItemTag.GUN_ID, ItemTagType.STRING))
                .ifPresent(this::setModel);
        CustomItemTagContainer mag = container.getCustomTag(ItemTag.GUN_MAGAZINE, ItemTagType.TAG_CONTAINER);
        if(mag != null) (magazine = new MagazineItem()).load(mag);
    }

    @Override
    public void writeInfo(Map<String, String> map, ConfigurationSection localeConf) {
        if(getModel() != null) getModel().writeInfo(map, localeConf);
        magazine.writeInfo(map, localeConf);
    }
}
