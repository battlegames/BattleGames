package dev.anhcraft.abm.gui.core;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BattlePagination {
    private List<String> headerLore;
    private List<String> footerLore;
    private int[] slots;
    private String handler;

    public BattlePagination(List<String> headerLore, List<String> footerLore, int[] slots, String handler) {
        this.headerLore = headerLore;
        this.footerLore = footerLore;
        this.slots = slots;
        this.handler = handler;
    }

    @NotNull
    public List<String> getHeaderLore() {
        return headerLore;
    }

    @NotNull
    public List<String> getFooterLore() {
        return footerLore;
    }

    @NotNull
    public int[] getSlots() {
        return slots;
    }

    @NotNull
    public String getHandler() {
        return handler;
    }
}
