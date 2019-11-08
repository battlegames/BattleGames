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

import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.annotation.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Schema
public class BattleScoreboard {
    public static final ConfigSchema<BattleScoreboard> SCHEMA = ConfigSchema.of(BattleScoreboard.class);

    @Key("enabled")
    @Explanation("Should we enable the scoreboard?")
    private boolean enabled = true;

    @Key("title")
    @Explanation({
            "The title of the scoreboard",
            "You can use placeholders here"
    })
    @Validation(notNull = true)
    private String title;

    @Key("content")
    @Explanation({
            "The content of the scoreboard",
            "You can use placeholders here"
    })
    @IgnoreValue(ifNull = true)
    private List<String> content = new ArrayList<>();

    @Key("fixed_length")
    @Explanation({
            "The (fixed) length of each line",
            "Set to 0 in order to resize automatically",
            "Maximum length of a line is:",
            "+ 128 characters (1.13+)",
            "+ 32 characters (1.12)"
    })
    private int fixedLength;

    public boolean isEnabled() {
        return enabled;
    }

    @NotNull
    public String getTitle() {
        return title;
    }

    @NotNull
    public List<String> getContent() {
        return content;
    }

    public int getFixedLength() {
        return fixedLength;
    }
}
