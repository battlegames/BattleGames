package dev.anhcraft.abm.api.inventory.items;

import dev.anhcraft.abm.api.APIProvider;
import dev.anhcraft.abm.api.misc.info.InfoHolder;
import dev.anhcraft.craftkit.cb_common.kits.nbt.CompoundTag;
import dev.anhcraft.craftkit.cb_common.kits.nbt.StringTag;
import org.jetbrains.annotations.NotNull;

public class Ammo extends BattleItem<AmmoModel> {
    @Override
    public void save(CompoundTag compound) {
        getModel().ifPresent(ammoModel -> compound.put(ItemTag.AMMO_ID, ammoModel.getId()));
    }

    @Override
    public void load(CompoundTag compound) {
        APIProvider.get().getAmmoModel(compound.getValue(ItemTag.AMMO_ID, StringTag.class)).ifPresent(this::setModel);
    }

    @Override
    public void inform(@NotNull InfoHolder holder) {
        getModel().ifPresent(ammoModel -> ammoModel.inform(holder));
    }
}
