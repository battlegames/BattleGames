package dev.anhcraft.abm.api.gui;

import java.util.HashMap;
import java.util.Map;

public abstract class GuiHandler {
    private final Map<String, GuiListener<? extends GuiReport>> eventListeners = new HashMap<>();

    public Map<String, GuiListener<? extends GuiReport>> getEventListeners() {
        return eventListeners;
    }
}
