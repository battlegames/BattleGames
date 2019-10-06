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

import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.annotation.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

@Schema
public class CustomBossBar {
    public static final ConfigSchema<CustomBossBar> SCHEMA = ConfigSchema.of(CustomBossBar.class);

    @Key("primary")
    @Explanation("Make the bar appeared on the primary slot")
    private boolean primarySlot = true;

    @Key("title")
    @Explanation("Set the title")
    @Validation(notNull = true)
    private String title;

    @Key("color")
    @Explanation("Set the color")
    @PrettyEnum
    @IgnoreValue(ifNull = true)
    private BarColor color = BarColor.RED;

    @Key("style")
    @Explanation("Set the style")
    @PrettyEnum
    @IgnoreValue(ifNull = true)
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
