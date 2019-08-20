package dev.anhcraft.abm.api.inventory.items;

import dev.anhcraft.abm.api.APIProvider;
import dev.anhcraft.abm.api.misc.info.InfoHolder;
import org.apache.commons.lang.Validate;
import org.bukkit.inventory.meta.tags.CustomItemTagContainer;
import org.bukkit.inventory.meta.tags.ItemTagType;
import org.jetbrains.annotations.NotNull;

public class Magazine extends BattleItem<MagazineModel> {
    @NotNull
    private Ammo ammo = new Ammo();
    private int ammoCount;

    public int getAmmoCount() {
        return ammoCount;
    }

    public void setAmmoCount(int ammoCount) {
        this.ammoCount = ammoCount;
    }

    @NotNull
    public Ammo getAmmo() {
        return ammo;
    }

    public void setAmmo(@NotNull Ammo ammo) {
        Validate.notNull(ammo, "AmmoModel must be non-null");
        this.ammo = ammo;
    }

    public void resetAmmo() {
        getModel().ifPresent(magazineModel -> ammo.getModel().ifPresent(ammoModel -> ammoCount = magazineModel.getAmmunition().getOrDefault(ammoModel, 0)));
    }

    @Override
    public void save(CustomItemTagContainer container) {
        getModel().ifPresent(magazineModel -> container.setCustomTag(ItemTag.MAGAZINE_ID, ItemTagType.STRING, magazineModel.getId()));
        container.setCustomTag(ItemTag.MAGAZINE_AMMO_COUNT, ItemTagType.INTEGER, ammoCount);
        CustomItemTagContainer c = container.getAdapterContext().newTagContainer();
        ammo.save(c);
        container.setCustomTag(ItemTag.MAGAZINE_AMMO, ItemTagType.TAG_CONTAINER, c);
    }

    @Override
    public void load(CustomItemTagContainer container) {
        APIProvider.get()
                .getMagazineModel(container.getCustomTag(ItemTag.MAGAZINE_ID, ItemTagType.STRING))
                .ifPresent(this::setModel);
        Integer a = container.getCustomTag(ItemTag.MAGAZINE_AMMO_COUNT, ItemTagType.INTEGER);
        if(a != null) ammoCount = a;
        CustomItemTagContainer am = container.getCustomTag(ItemTag.MAGAZINE_AMMO, ItemTagType.TAG_CONTAINER);
        if(am != null) ammo.load(am);
    }

    @Override
    public void inform(@NotNull InfoHolder holder) {
        getModel().ifPresent(magazineModel -> {
            magazineModel.inform(holder);
            ammo.getModel().ifPresent(ammoModel -> holder.inform("ammo_capacity", magazineModel.getAmmunition().getOrDefault(ammoModel, 0)));
        });
        holder.link(ammo.collectInfo(null)).inform("ammo_count", ammoCount);
    }
}