package dev.anhcraft.abm.api.misc;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

public class CustomBossBar {
    private boolean primarySlot;
    private String title;
    private BarColor color;
    private BarStyle style;

    public CustomBossBar(boolean primarySlot, String title, BarColor color, BarStyle style) {
        this.primarySlot = primarySlot;
        this.title = title;
        this.color = color;
        this.style = style;
    }

    public boolean isPrimarySlot() {
        return primarySlot;
    }

    public void setPrimarySlot(boolean primarySlot) {
        this.primarySlot = primarySlot;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BarColor getColor() {
        return color;
    }

    public void setColor(BarColor color) {
        this.color = color;
    }

    public BarStyle getStyle() {
        return style;
    }

    public void setStyle(BarStyle style) {
        this.style = style;
    }
}
