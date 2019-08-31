package dev.anhcraft.abm.api.inventory.items;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public enum ItemType {
    AMMO(Ammo::new),
    GUN(Gun::new),
    MAGAZINE(Magazine::new),
    SCOPE(Scope::new);

    Supplier<BattleItem> c;

    ItemType(Supplier<BattleItem> c){
        this.c = c;
    }

    private String localizedName = name();

    @NotNull
    public String getLocalizedName() {
        return localizedName;
    }

    public void setLocalizedName(@NotNull String localizedName) {
        Validate.notNull(localizedName, "Localized name must be non-null");
        this.localizedName = localizedName;
    }

    @NotNull
    public BattleItem make(){
        return c.get();
    }
}
