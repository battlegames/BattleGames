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

import dev.anhcraft.confighelper.annotation.*;
import org.bukkit.FireworkEffect;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Schema
public class BattleFireworkEffect {
    @Key("type")
    @Explanation("The firework type")
    @PrettyEnum
    @IgnoreValue(ifNull = true)
    private FireworkEffect.Type type = FireworkEffect.Type.BALL;

    @Key("flicker")
    @Explanation("Make this firework flicker")
    private boolean flicker;

    @Key("trail")
    @Explanation("Make this firework has a trail")
    private boolean trail;

    @Key("primary_colors")
    @Explanation("All primary colors")
    @PrettyEnum
    @IgnoreValue(ifNull = true)
    private List<Color> primaryColors = new ArrayList<>();

    @Key("fade_colors")
    @Explanation("All fade colors")
    @PrettyEnum
    @IgnoreValue(ifNull = true)
    private List<Color> fadeColors = new ArrayList<>();

    private FireworkEffect cached;

    @NotNull
    public FireworkEffect.Type getType() {
        return type;
    }

    public boolean isFlicker() {
        return flicker;
    }

    public boolean isTrail() {
        return trail;
    }

    @NotNull
    public List<Color> getPrimaryColors() {
        return primaryColors;
    }

    @NotNull
    public List<Color> getFadeColors() {
        return fadeColors;
    }

    @NotNull
    public FireworkEffect getFireworkEffect() {
        if(cached == null) {
            FireworkEffect.Builder b = FireworkEffect.builder().with(type).flicker(flicker).trail(trail);
            for (Color c : primaryColors) {
                b.withColor(c.asBukkitColor());
            }
            for (Color c : fadeColors) {
                b.withFade(c.asBukkitColor());
            }
            cached = b.build();
        }
        return cached;
    }
}
