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

package dev.anhcraft.battle.api.arena.mode.options;

import dev.anhcraft.battle.utils.ConfigurableObject;
import dev.anhcraft.battle.utils.LocationUtil;
import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.annotation.Explanation;
import dev.anhcraft.confighelper.annotation.Key;
import dev.anhcraft.confighelper.annotation.Schema;
import dev.anhcraft.confighelper.annotation.Validation;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Schema
public class FlagOptions extends ConfigurableObject {
    public static final ConfigSchema<FlagOptions> SCHEMA = ConfigSchema.of(FlagOptions.class);

    @Key("location")
    @Explanation("The location of the flag")
    @Validation(notNull = true)
    private String location;

    @Key("display_name.valid")
    @Explanation("The name to be displayed when the flag state is valid")
    @Validation(notNull = true)
    private String validDisplayName;

    @Key("display_name.invalid")
    @Explanation("The name to be displayed when the flag state is invalid")
    @Validation(notNull = true)
    private String invalidDisplayName;

    @Key("display_name.neutral")
    @Explanation("The name to be displayed when the flag state is neutral")
    @Validation(notNull = true)
    private String neutralDisplayName;

    @Key("max_health")
    @Explanation("The maximum health points of the flag")
    @Validation(notNull = true)
    private int maxHealth = 10;

    @Key("start_capture_sound")
    @Explanation("The sound on starting the capture")
    private String startCaptureSound;

    @Key("stop_capture_sound")
    @Explanation("The sound on stopping the capture")
    private String stopCaptureSound;

    @NotNull
    public Location getLocation() {
        return LocationUtil.fromString(location);
    }

    @NotNull
    public String getValidDisplayName() {
        return validDisplayName;
    }

    @NotNull
    public String getInvalidDisplayName() {
        return invalidDisplayName;
    }

    @NotNull
    public String getNeutralDisplayName() {
        return neutralDisplayName;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    @Nullable
    public String getStartCaptureSound() {
        return startCaptureSound;
    }

    @Nullable
    public String getStopCaptureSound() {
        return stopCaptureSound;
    }
}
