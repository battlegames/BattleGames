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

package dev.anhcraft.battle.api.misc;

import dev.anhcraft.battle.utils.ConfigurableObject;
import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.annotation.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Schema
public class Rollback extends ConfigurableObject {
    public static final ConfigSchema<Rollback> SCHEMA = ConfigSchema.of(Rollback.class);

    public enum Provider {
        SLIME_WORLD
    }

    @Key("enabled")
    @Explanation("Enabled/Disabled the rollback system")
    private boolean enabled;

    @Key("provider")
    @Explanation("The provider which handles the rollback")
    @PrettyEnum
    @Validation(notNull = true)
    private Provider provider;

    @Key("worlds")
    @Explanation("List of worlds need to be reset")
    @IgnoreValue(ifNull = true)
    private List<String> worlds = new ArrayList<>();

    public boolean isEnabled() {
        return enabled;
    }

    @NotNull
    public Provider getProvider() {
        return provider;
    }

    @NotNull
    public List<String> getWorlds() {
        return worlds;
    }
}
