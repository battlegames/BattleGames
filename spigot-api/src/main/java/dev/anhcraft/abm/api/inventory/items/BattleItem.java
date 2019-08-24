package dev.anhcraft.abm.api.inventory.items;

import dev.anhcraft.abm.api.misc.info.InfoHolder;
import dev.anhcraft.abm.api.misc.info.Informative;
import dev.anhcraft.craftkit.cb_common.kits.nbt.CompoundTag;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class BattleItem<M extends BattleItemModel> implements Informative {
    private M model;

    public Optional<M> getModel() {
        return Optional.ofNullable(model);
    }

    public void setModel(@NotNull M model) {
        Validate.notNull(model, "Model must be non-null");
        this.model = model;
    }

    public abstract void save(CompoundTag compound);
    public abstract void load(CompoundTag compound);

    public InfoHolder collectInfo(@Nullable String prefix) {
        Validate.notNull(model, "Model must be assigned");
        InfoHolder h = new InfoHolder((prefix == null ? "" : prefix) +
                model.getItemType().name().toLowerCase() + "_")
                .link(model.collectInfo(prefix));
        inform(h);
        return h;
    }
}
