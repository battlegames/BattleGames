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

package dev.anhcraft.battle.api.arena.game.options;

import dev.anhcraft.config.annotations.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@SuppressWarnings("FieldMayBeFinal")
@Configurable
public class CaptureTheFlagOptions extends TeamDeathmatchOptions {
    @Setting
    @Description("All flags in the arena")
    @Validation(notNull = true, silent = true)
    @Example({
            "flags:",
            "  '1':",
            "    location: assault -345.41 43.00 -224.30 52.35 11.55",
            "    display_name:",
            "      valid: \"&a&l{__flag_team__} | &f&l{__flag_health__} &c&l❤\"",
            "      invalid: \"&7&l{__flag_team__} | &f&l{__flag_health__}/{__flag_max_health__} &c&l❤\"",
            "      neutral: \"&7Neutral\"",
            "    max_health: 10",
            "  '2':",
            "    location: assault -355.86 40.50 -285.70 176.70 90.00",
            "    display_name:",
            "      valid: \"&a&l{__flag_team__} | &f&l{__flag_health__} &c&l❤\"",
            "      invalid: \"&7&l{__flag_team__} | &f&l{__flag_health__}/{__flag_max_health__} &c&l❤\"",
            "      neutral: \"&7Neutral\"",
            "    max_health: 15"
    })
    private Map<String, FlagOptions> flags = new HashMap<>();

    @NotNull
    public Collection<FlagOptions> getFlags() {
        return flags.values();
    }
}
