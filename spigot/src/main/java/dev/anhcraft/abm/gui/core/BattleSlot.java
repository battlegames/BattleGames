package dev.anhcraft.abm.gui.core;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

public class BattleSlot {
    private ConfigurationSection item;
    private String handler;

    public BattleSlot(@Nullable ConfigurationSection item, @Nullable String handler) {
        this.item = item;
        this.handler = handler;
    }

    @Nullable
    public String getHandler() {
        return handler;
    }

    @Nullable
    public ConfigurationSection getItem() {
        return item;
    }
}
