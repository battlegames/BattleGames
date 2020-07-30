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

import dev.anhcraft.battle.impl.Informative;
import dev.anhcraft.battle.utils.ConfigurableObject;
import dev.anhcraft.battle.utils.info.InfoHolder;
import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.annotation.Explanation;
import dev.anhcraft.confighelper.annotation.Key;
import dev.anhcraft.confighelper.annotation.Schema;
import dev.anhcraft.confighelper.annotation.Validation;
import dev.anhcraft.craftkit.abif.PreparedItem;
import dev.anhcraft.jvmkit.utils.Condition;
import dev.anhcraft.jvmkit.utils.ObjectUtil;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("FieldMayBeFinal")
@Schema
public class Booster extends ConfigurableObject implements Informative {
    public static final ConfigSchema<Booster> SCHEMA = ConfigSchema.of(Booster.class);

    private final String id;

    @Key("icon")
    @Explanation("The icon that used to symbolize the booster")
    @Validation(notNull = true)
    private PreparedItem icon;

    @Key("expiry_time")
    @Explanation("The expiry time")
    private int expiryTime;

    @Key("money.multiplier")
    @Explanation("The multiplier for the total money")
    private double moneyMultiplier = 1;

    @Key("money.limit")
    @Explanation({
            "The maximum money a player can get",
            "Set to 0 to disable this option"
    })
    private double moneyLimit;

    @Key("exp.multiplier")
    @Explanation("The multiplier for the total experience points")
    private double expMultiplier = 1;

    @Key("exp.limit")
    @Explanation({
            "The maximum exp a player can get",
            "Set to 0 to disable this option"
    })
    private int expLimit;

    public Booster(@NotNull String id) {
        Condition.argNotNull("id", id);
        this.id = id;
    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public PreparedItem getIcon() {
        return icon;
    }

    public int getExpiryTime() {
        return expiryTime;
    }

    public double getMoneyMultiplier() {
        return moneyMultiplier;
    }

    public double getMoneyLimit() {
        return moneyLimit;
    }

    public double getExpMultiplier() {
        return expMultiplier;
    }

    public int getExpLimit() {
        return expLimit;
    }

    @Override
    public void inform(@NotNull InfoHolder holder) {
        holder.inform("id", id)
                .inform("name", ObjectUtil.optional(icon.name(), id))
                .inform("expiry_time", expiryTime)
                .inform("formatted_expiry_time", BattleApi.getInstance().formatShortFormTime(expiryTime))
                .inform("money_multiplier", moneyMultiplier)
                .inform("money_limit", moneyLimit)
                .inform("exp_multiplier", expMultiplier)
                .inform("exp_limit", expLimit);
    }
}
