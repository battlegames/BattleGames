package dev.anhcraft.abm.gui.core;

import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class PlayerGui {
    private final Map<Integer, Integer> slot2DataIndexes = new HashMap<>();
    private BattleGui gui;
    private BattleGui internalInventory;
    private Inventory inventory;
    private int page = 0;
    private boolean outOfData = false;

    public BattleGui getGui() {
        return gui;
    }

    public void setGui(BattleGui gui) {
        this.gui = gui;
    }

    @Nullable
    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(@Nullable Inventory inventory) {
        this.inventory = inventory;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public boolean isOutOfData() {
        return outOfData;
    }

    public void setOutOfData(boolean outOfData) {
        this.outOfData = outOfData;
    }

    public Map<Integer, Integer> getSlot2DataIndexes() {
        return slot2DataIndexes;
    }

    public BattleGui getInternalInventory() {
        return internalInventory;
    }

    public void setInternalInventory(BattleGui internalInventory) {
        this.internalInventory = internalInventory;
    }
}
