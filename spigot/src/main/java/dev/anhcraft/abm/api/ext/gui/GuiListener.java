package dev.anhcraft.abm.api.ext.gui;

import dev.anhcraft.abm.api.objects.gui.GuiReport;

public abstract class GuiListener<T extends GuiReport> {
    private Class<T> clazz;

    protected GuiListener(Class<T> clazz) {
        this.clazz = clazz;
    }

    public abstract void call(T event);

    public Class<T> getClazz() {
        return clazz;
    }
}
