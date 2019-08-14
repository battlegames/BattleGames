package dev.anhcraft.abm.api.enums;

import dev.anhcraft.abm.api.ext.BattleItem;
import dev.anhcraft.abm.api.objects.Ammo;
import dev.anhcraft.abm.api.objects.Gun;
import dev.anhcraft.abm.api.objects.Magazine;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public enum ItemType {
    AMMO(Ammo::new),
    GUN(Gun::new),
    MAGAZINE(Magazine::new);

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
