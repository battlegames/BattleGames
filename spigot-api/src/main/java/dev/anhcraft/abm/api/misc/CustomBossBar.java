/*
 *
 *     Battle Minigame.
 *     Copyright (c) 2019 by anhcraft.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */
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
