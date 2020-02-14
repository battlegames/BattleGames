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

package dev.anhcraft.battle.api.stats.natives;

import dev.anhcraft.battle.api.stats.LongCounter;
import dev.anhcraft.battle.api.stats.NativeStats;
import org.jetbrains.annotations.NotNull;

public class ExpStat extends LongCounter {
    @Override
    public @NotNull String getId() {
        return NativeStats.EXP;
    }

    /*
        ExpStat never support advancement as exp are a kind of reward.
        Means they may cause overflow.
     */
    @Override
    public boolean hasAdvancementSupport() {
        return false;
    }
}
