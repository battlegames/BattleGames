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

package dev.anhcraft.battle.api.market;

import dev.anhcraft.config.annotations.*;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("FieldMayBeFinal")
@Configurable
public class PackageDetails {
    @Setting
    @Path("item_header")
    @Description("A nice header for the Item part")
    @Validation(notNull = true)
    private String itemHeader;

    @Setting
    @Path("item_format.vanilla")
    @Description({
            "The format of each Vanilla item to show in the details",
            "Placeholders: &lt;name&gt;, &lt;amount&gt;"
    })
    @Validation(notNull = true)
    private String vanillaItemFormat;

    @Setting
    @Path("item_format.battle")
    @Description({
            "The format of each Battle item to show in the details",
            "Placeholders: &lt;name&gt;"
    })
    @Validation(notNull = true)
    private String battleItemFormat;

    @Setting
    @Path("exp_header")
    @Description("A nice header for the Experience points part")
    @Validation(notNull = true)
    private String expHeader;

    @Setting
    @Path("exp_format.vanilla")
    @Description({
            "A line to show how many Vanilla exp points will be given",
            "Placeholders: &lt;amount&gt;"
    })
    @Validation(notNull = true)
    private String vanillaExpFormat;

    @Setting
    @Path("exp_format.battle")
    @Description({
            "A line to show how many Battle exp points will be given",
            "Placeholders: &lt;amount&gt;"
    })
    @Validation(notNull = true)
    private String battleExpFormat;

    @Setting
    @Path("perk_header")
    @Description("A nice header for the Perks part")
    @Validation(notNull = true)
    private String perkHeader;

    @Setting
    @Path("perk_format")
    @Description({
            "The format for each perk to show in the details",
            "You can use all informative placeholders for Perk here.",
            "&lt;id&gt;, &lt;name&gt;, &lt;effects&gt;, &lt;new_health&gt;, &lt;new_food_level&gt;"
    })
    @Validation(notNull = true)
    private String perkFormat;

    @Setting
    @Path("booster_header")
    @Description("A nice header for the Boosters part")
    @Validation(notNull = true)
    private String boosterHeader;

    @Setting
    @Path("booster_format")
    @Description({
            "The format for each booster to show in the details",
            "You can use all informative placeholders for Booster here.",
            "&lt;id&gt;, &lt;name&gt;, &lt;expiry_time&gt;, &lt;formatted_expiry_time&gt;, &lt;money_limit&gt;",
            "&lt;money_multiplier&gt;, &lt;exp_limit&gt;, &lt;exp_multiplier&gt;"
    })
    @Validation(notNull = true)
    private String boosterFormat;

    @Setting
    @Path("separated_part_by_new_line")
    @Description("Should we separate parts with new lines?")
    @Validation(notNull = true)
    private boolean separatedPartByNewLine;

    @NotNull
    public String getItemHeader() {
        return itemHeader;
    }

    @NotNull
    public String getVanillaItemFormat() {
        return vanillaItemFormat;
    }

    @NotNull
    public String getBattleItemFormat() {
        return battleItemFormat;
    }

    @NotNull
    public String getExpHeader() {
        return expHeader;
    }

    @NotNull
    public String getVanillaExpFormat() {
        return vanillaExpFormat;
    }

    @NotNull
    public String getBattleExpFormat() {
        return battleExpFormat;
    }

    @NotNull
    public String getPerkHeader() {
        return perkHeader;
    }

    @NotNull
    public String getPerkFormat() {
        return perkFormat;
    }

    @NotNull
    public String getBoosterHeader() {
        return boosterHeader;
    }

    @NotNull
    public String getBoosterFormat() {
        return boosterFormat;
    }

    public boolean shouldSeparatedPartByNewLine() {
        return separatedPartByNewLine;
    }
}
