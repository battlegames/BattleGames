package dev.anhcraft.abm.api.impl;

import dev.anhcraft.abm.api.enums.ItemType;
import org.jetbrains.annotations.NotNull;

public interface BattleItemModel {
    @NotNull
    ItemType getItemType();

    @NotNull
    String getId();

    @NotNull
    String getName();
}
