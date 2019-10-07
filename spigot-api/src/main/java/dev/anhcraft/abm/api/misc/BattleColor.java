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

import dev.anhcraft.jvmkit.utils.ReflectionUtil;
import org.jetbrains.annotations.NotNull;

public enum BattleColor {
    WHITE,
    SILVER,
    GRAY,
    BLACK,
    RED,
    MAROON,
    YELLOW,
    OLIVE,
    LIME,
    GREEN,
    AQUA,
    TEAL,
    BLUE,
    NAVY,
    FUCHSIA,
    PURPLE,
    ORANGE;

    private org.bukkit.Color color;

    BattleColor(){
        color = (org.bukkit.Color) ReflectionUtil.getStaticField(org.bukkit.Color.class, name());
    }

    @NotNull
    public org.bukkit.Color asBukkitColor(){
        return color;
    }
}
