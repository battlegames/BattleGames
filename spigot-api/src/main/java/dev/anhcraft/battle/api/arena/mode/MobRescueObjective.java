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

package dev.anhcraft.battle.api.arena.mode;

import dev.anhcraft.battle.utils.ConfigurableObject;
import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.annotation.Explanation;
import dev.anhcraft.confighelper.annotation.Key;
import dev.anhcraft.confighelper.annotation.Schema;

@SuppressWarnings("FieldMayBeFinal")
@Schema
public class MobRescueObjective extends ConfigurableObject {
    public static final ConfigSchema<MobRescueObjective> SCHEMA = ConfigSchema.of(MobRescueObjective.class);

    @Key("amount.min")
    @Explanation("Minimum amount of entities")
    private int minAmount = 1;

    @Key("amount.max")
    @Explanation("Maximum amount of entities")
    private int maxAmount = 1;

    @Key("reward_coins")
    @Explanation("Some coins to reward the player")
    private double rewardCoins;

    public int getMinAmount() {
        return minAmount;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public double getRewardCoins() {
        return rewardCoins;
    }
}
