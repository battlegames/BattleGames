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
import dev.anhcraft.battle.utils.info.InfoHolder;
import dev.anhcraft.config.annotations.*;
import dev.anhcraft.craftkit.abif.PreparedItem;
import dev.anhcraft.jvmkit.utils.Condition;
import dev.anhcraft.jvmkit.utils.ObjectUtil;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("FieldMayBeFinal")
@Configurable
public class Booster implements Informative {
    private final String id;

    @Setting
    @Description("The icon that used to symbolize the booster")
    @Validation(notNull = true)
    private PreparedItem icon;

    @Setting
    @Path("expiry_time")
    @Description("The expiry time")
    private int expiryTime;

    @Setting
    @Path("money.multiplier")
    @Description("The multiplier for the total money")
    private double moneyMultiplier = 1;

    @Setting
    @Path("money.limit")
    @Description({
            "The maximum money a player can get",
            "Set to 0 to disable this option"
    })
    private double moneyLimit;

    @Setting
    @Path("exp.multiplier")
    @Description("The multiplier for the total experience points")
    private double expMultiplier = 1;

    @Setting
    @Path("exp.limit")
    @Description({
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
