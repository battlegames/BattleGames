package dev.anhcraft.abm.gui.core;

import dev.anhcraft.abm.api.impl.Resettable;

import java.util.Arrays;

public class InternalGui implements Resettable {
    private final boolean[] slots = new boolean[36];

    public boolean isCancelled(int i){
        return slots[i];
    }

    public void setCancelled(int i, boolean x){
        slots[i] = x;
    }

    @Override
    public void reset() {
        Arrays.fill(slots, false);
    }
}
