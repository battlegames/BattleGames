package dev.anhcraft.abm.api.enums;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

public enum ItemType {
    AMMO,
    GUN,
    MAGAZINE;

    private String localizedName = name();

    public String getLocalizedName() {
        return localizedName;
    }

    public void setLocalizedName(@NotNull String localizedName) {
        Validate.notNull(localizedName, "Localized name must be non-null");
        this.localizedName = localizedName;
    }
}
