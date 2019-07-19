package dev.anhcraft.abm.api.ext;

import dev.anhcraft.abm.api.impl.BattleItemModel;
import org.apache.commons.lang.Validate;
import org.bukkit.inventory.meta.tags.CustomItemTagContainer;
import org.jetbrains.annotations.NotNull;

public abstract class BattleItem<M extends BattleItemModel> {
    private M model;

    public M getModel() {
        return model;
    }

    public void setModel(@NotNull M model) {
        Validate.notNull(model, "Model must be non-null");
        this.model = model;
    }

    public abstract void save(CustomItemTagContainer container);
    public abstract void load(CustomItemTagContainer container);
}
