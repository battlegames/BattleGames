package dev.anhcraft.abm.api.objects.gui;

import dev.anhcraft.jvmkit.utils.Condition;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class GuiSlot {
    private ConfigurationSection itemConf;
    private Collection<String> eventHandlers;
    private boolean isPaginationSlot;

    public GuiSlot(@Nullable ConfigurationSection itemConf, @NotNull Collection<String> eventHandlers, boolean isPaginationSlot) {
        Condition.argNotNull("eventHandlers", eventHandlers);
        this.isPaginationSlot = isPaginationSlot;
        this.itemConf = itemConf;
        this.eventHandlers = eventHandlers;
    }

    @Nullable
    public ConfigurationSection getItemConf() {
        return itemConf;
    }

    @NotNull
    public Collection<String> getEventHandlers() {
        return eventHandlers;
    }

    public boolean isPaginationSlot() {
        return isPaginationSlot;
    }
}
