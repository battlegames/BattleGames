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
package dev.anhcraft.battle.api;

import dev.anhcraft.config.annotations.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

@SuppressWarnings("FieldMayBeFinal")
@Configurable
public class BattleBar {
    @Setting
    @Path("primary")
    @Description("Make the bar appeared on the primary slot")
    private boolean primarySlot = true;

    @Setting
    @Description("Set the title")
    @Validation(notNull = true)
    private String title;

    @Setting
    @Description("Set the color")
    @Validation(notNull = true, silent = true)
    private BarColor color = BarColor.RED;

    @Setting
    @Description("Set the style")
    @Validation(notNull = true, silent = true)
    private BarStyle style = BarStyle.SOLID;

    public boolean isPrimarySlot() {
        return primarySlot;
    }

    public String getTitle() {
        return title;
    }

    public BarColor getColor() {
        return color;
    }

    public BarStyle getStyle() {
        return style;
    }
}
