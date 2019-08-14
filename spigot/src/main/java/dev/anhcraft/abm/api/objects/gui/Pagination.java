package dev.anhcraft.abm.api.objects.gui;

import dev.anhcraft.jvmkit.utils.Condition;
import org.jetbrains.annotations.NotNull;

public class Pagination {
    private int[] slots;
    private String handler;

    public Pagination(@NotNull int[] slots, @NotNull String handler) {
        Condition.argNotNull("slots", slots);
        Condition.argNotNull("handler", handler);
        this.slots = slots;
        this.handler = handler;
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
