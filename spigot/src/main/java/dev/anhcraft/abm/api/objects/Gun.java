package dev.anhcraft.abm.api.objects;

import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.ext.Weapon;
import dev.anhcraft.abm.system.ItemTag;
import dev.anhcraft.abm.utils.info.InfoHolder;
import org.apache.commons.lang.Validate;
import org.bukkit.inventory.meta.tags.CustomItemTagContainer;
import org.bukkit.inventory.meta.tags.ItemTagType;
import org.jetbrains.annotations.NotNull;

public class Gun extends Weapon<GunModel> {
    @NotNull
    private Magazine magazine = new Magazine();

    @NotNull
    public Magazine getMagazine() {
        return magazine;
    }

    public void setMagazine(@NotNull Magazine magazine) {
        Validate.notNull(magazine, "Magazine must be non-null");
        this.magazine = magazine;
    }

    @Override
    public void save(CustomItemTagContainer container) {
        getModel().ifPresent(gunModel ->
                container.setCustomTag(ItemTag.GUN_ID, ItemTagType.STRING, gunModel.getId()));
        CustomItemTagContainer c = container.getAdapterContext().newTagContainer();
        magazine.save(c);
        container.setCustomTag(ItemTag.GUN_MAGAZINE, ItemTagType.TAG_CONTAINER, c);
    }

    @Override
    public void load(CustomItemTagContainer container) {
        BattlePlugin.getAPI()
                .getGunModel(container.getCustomTag(ItemTag.GUN_ID, ItemTagType.STRING))
                .ifPresent(this::setModel);
        CustomItemTagContainer mag = container.getCustomTag(ItemTag.GUN_MAGAZINE, ItemTagType.TAG_CONTAINER);
        if(mag != null) (magazine = new Magazine()).load(mag);
    }

    @Override
    public void inform(@NotNull InfoHolder holder) {
        getModel().ifPresent(gunModel -> gunModel.inform(holder));
        holder.link(magazine.collectInfo(null));
    }
}
