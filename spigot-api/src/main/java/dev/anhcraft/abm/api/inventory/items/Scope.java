package dev.anhcraft.abm.api.inventory.items;

import dev.anhcraft.abm.api.APIProvider;
import dev.anhcraft.abm.api.misc.info.InfoHolder;
import dev.anhcraft.craftkit.cb_common.kits.nbt.CompoundTag;
import dev.anhcraft.craftkit.cb_common.kits.nbt.IntTag;
import dev.anhcraft.craftkit.cb_common.kits.nbt.StringTag;
import org.jetbrains.annotations.NotNull;

public class Scope extends BattleItem<ScopeModel> {
    private int nextZoomLevel = -1;

    public int getNextZoomLevel() {
        return nextZoomLevel;
    }

    public void setNextZoomLevel(int nextZoomLevel) {
        this.nextZoomLevel = nextZoomLevel;
    }

    public int nextZoomLevel() {
        getModel().ifPresent(scopeModel -> {
            if(++nextZoomLevel == scopeModel.getZoomLevels().size()) nextZoomLevel = -1;
        });
        return nextZoomLevel;
    }

    @Override
    public void save(CompoundTag compound) {
        getModel().ifPresent(m -> compound.put(ItemTag.SCOPE_ID, m.getId()));
        compound.put(ItemTag.SCOPE_NEXT_ZOOM_LEVEL, nextZoomLevel);
    }

    @Override
    public void load(CompoundTag compound) {
        APIProvider.get().getScopeModel(compound.getValue(ItemTag.SCOPE_ID, StringTag.class)).ifPresent(this::setModel);
        Integer nextZoomLv = compound.getValue(ItemTag.SCOPE_NEXT_ZOOM_LEVEL, IntTag.class);
        if(nextZoomLv != null) nextZoomLevel = nextZoomLv;
    }

    @Override
    public void inform(@NotNull InfoHolder holder) {
        getModel().ifPresent(m -> m.inform(holder));
    }
}
