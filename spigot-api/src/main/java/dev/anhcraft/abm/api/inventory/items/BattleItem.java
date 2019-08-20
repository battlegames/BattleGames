package dev.anhcraft.abm.api.inventory.items;

import dev.anhcraft.abm.api.misc.info.Informative;
import dev.anhcraft.abm.api.misc.info.InfoHolder;
import org.apache.commons.lang.Validate;
import org.bukkit.inventory.meta.tags.CustomItemTagContainer;
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

    public abstract void save(CustomItemTagContainer container);
    public abstract void load(CustomItemTagContainer container);

    public InfoHolder collectInfo(@Nullable String prefix) {
        Validate.notNull(model, "Model must be assigned");
        InfoHolder h = new InfoHolder((prefix == null ? "" : prefix) +
                model.getItemType().name().toLowerCase() + "_")
                .link(model.collectInfo(prefix));
        inform(h);
        return h;
    }
}
