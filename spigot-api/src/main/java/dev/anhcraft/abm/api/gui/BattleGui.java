package dev.anhcraft.abm.api.gui;

import dev.anhcraft.jvmkit.helpers.PaginationHelper;
import org.jetbrains.annotations.Nullable;

public class BattleGui {
    private Gui gui;
    private PlayerGui playerGui;
    private BattleGuiSlot[] slots;
    private PaginationHelper<BattleGuiSlot> pagination;

    public BattleGui(Gui gui, PlayerGui playerGui, BattleGuiSlot[] slots) {
        this.gui = gui;
        this.playerGui = playerGui;
        this.slots = slots;
    }

    public Gui getGui() {
        return gui;
    }

    public BattleGuiSlot[] getSlots() {
        return slots;
    }

    @Nullable
    public PaginationHelper<BattleGuiSlot> getPagination() {
        return pagination;
    }

    public PlayerGui getPlayerGui() {
        return playerGui;
    }

    public void setPagination(@Nullable PaginationHelper<BattleGuiSlot> pagination) {
        this.pagination = pagination;
    }

    public void updatePagination(){
        pagination.each(battleGuiSlot -> slots[battleGuiSlot.getIndex()] = battleGuiSlot);
    }
}
