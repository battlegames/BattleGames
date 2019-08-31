package dev.anhcraft.abm.api.inventory.items;

import dev.anhcraft.abm.api.APIProvider;
import dev.anhcraft.abm.api.misc.info.InfoHolder;
import dev.anhcraft.craftkit.cb_common.kits.nbt.CompoundTag;
import dev.anhcraft.craftkit.cb_common.kits.nbt.StringTag;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Gun extends Weapon<GunModel> {
    @NotNull
    private Magazine magazine = new Magazine();

    @Nullable
    private Scope scope;

    @Nullable
    public Scope getScope() {
        return scope;
    }

    public void setScope(@Nullable Scope scope) {
        this.scope = scope;
    }

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

        CompoundTag mc = new CompoundTag();
        magazine.save(mc);
        compound.put(ItemTag.GUN_MAGAZINE, mc);

        if(scope != null) {
            CompoundTag sc = new CompoundTag();
            scope.save(sc);
            compound.put(ItemTag.GUN_SCOPE, sc);
        }
    }

    @Override
    public void load(CompoundTag compound) {
        APIProvider.get().getGunModel(compound.getValue(ItemTag.GUN_ID, StringTag.class)).ifPresent(this::setModel);
        CompoundTag mag = compound.get(ItemTag.GUN_MAGAZINE, CompoundTag.class);
        if(mag != null) magazine.load(mag);

        CompoundTag scp = compound.get(ItemTag.GUN_SCOPE, CompoundTag.class);
        if(scp != null) (scope = (scope == null) ? new Scope() : scope).load(scp);
    }

    @Override
    public void inform(@NotNull InfoHolder holder) {
        getModel().ifPresent(gunModel -> gunModel.inform(holder));
        holder.link(magazine.collectInfo(null));
        if(scope != null)
            holder.link(scope.collectInfo(null));
    }
}
