package dev.anhcraft.abm.api.objects.gui;

import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

public class PlayerGui {
    private BattleGui topGui;
    private BattleGui bottomGui;
    private Inventory topInv;

    @Nullable
    public BattleGui getTopGui() {
        return topGui;
    }

    public void setTopGui(@Nullable BattleGui topGui) {
        this.topGui = topGui;
    }

    @Nullable
    public BattleGui getBottomGui() {
        return bottomGui;
    }

    public void setBottomGui(@Nullable BattleGui bottomGui) {
        this.bottomGui = bottomGui;
    }

    @Nullable
    public Inventory getTopInv() {
        return topInv;
    }

    public void setTopInv(@Nullable Inventory topInv) {
        this.topInv = topInv;
    }
}
