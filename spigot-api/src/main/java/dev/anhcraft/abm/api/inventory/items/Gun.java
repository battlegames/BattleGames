package dev.anhcraft.abm.api.inventory.items;

import dev.anhcraft.abm.api.APIProvider;
import dev.anhcraft.abm.api.misc.info.InfoHolder;
import dev.anhcraft.craftkit.cb_common.kits.nbt.CompoundTag;
import dev.anhcraft.craftkit.cb_common.kits.nbt.StringTag;
import org.apache.commons.lang.Validate;
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
    public void save(CompoundTag compound) {
        getModel().ifPresent(gunModel -> compound.put(ItemTag.GUN_ID, gunModel.getId()));
        CompoundTag c = new CompoundTag();
        magazine.save(c);
        compound.put(ItemTag.GUN_MAGAZINE, c);
    }

    @Override
    public void load(CompoundTag compound) {
        APIProvider.get().getGunModel(compound.getValue(ItemTag.GUN_ID, StringTag.class)).ifPresent(this::setModel);
        CompoundTag mag = compound.get(ItemTag.GUN_MAGAZINE, CompoundTag.class);
        if(mag != null) (magazine = new Magazine()).load(mag);
    }

    @Override
    public void inform(@NotNull InfoHolder holder) {
        getModel().ifPresent(gunModel -> gunModel.inform(holder));
        holder.link(magazine.collectInfo(null));
    }
}
