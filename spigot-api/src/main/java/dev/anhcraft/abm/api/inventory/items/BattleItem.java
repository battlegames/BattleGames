package dev.anhcraft.abm.api.inventory.items;

import dev.anhcraft.abm.api.misc.info.InfoHolder;
import dev.anhcraft.abm.api.misc.info.Informative;
import dev.anhcraft.craftkit.cb_common.kits.nbt.CompoundTag;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class BattleItem<M extends BattleItemModel> implements Informative {
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<M> model = Optional.empty();

    public Optional<M> getModel() {
        return model;
    }

    public void setModel(@NotNull M model) {
        Validate.notNull(model, "Model must be non-null");
        this.model = Optional.of(model);
    }

    public abstract void save(CompoundTag compound);
    public abstract void load(CompoundTag compound);

    public InfoHolder collectInfo(@Nullable String prefix) {
        if(model.isPresent()) {
            M m = model.get();
            InfoHolder h = new InfoHolder((prefix == null ? "" : prefix) +
                    m.getItemType().name().toLowerCase() + "_")
                    .link(m.collectInfo(prefix));
            inform(h);
            return h;
        }
        return null;
    }
}
