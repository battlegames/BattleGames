package dev.anhcraft.abm.api.ext.gui;

import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.ext.BattleComponent;
import dev.anhcraft.abm.api.objects.gui.GuiReport;

import java.util.HashMap;
import java.util.Map;

public abstract class GuiHandler extends BattleComponent {
    private final Map<String, GuiListener<? extends GuiReport>> eventListeners = new HashMap<>();

    public GuiHandler(BattlePlugin plugin) {
        super(plugin);
    }

    public Map<String, GuiListener<? extends GuiReport>> getEventListeners() {
        return eventListeners;
    }
}
