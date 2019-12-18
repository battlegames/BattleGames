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
 *     but WITHOUT ANY WARRANTY), without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package dev.anhcraft.battle.api.stats;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.MutableClassToInstanceMap;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public class StatisticMap {
    private final Consumer<Statistic> onInit;
    private final ClassToInstanceMap<Statistic> stats = MutableClassToInstanceMap.create();

    public StatisticMap(@NotNull Consumer<Statistic> onInit) {
        this.onInit = onInit;
    }

    @NotNull
    public <T extends Statistic> T of(@NotNull Class<T> clazz){
        T x = stats.getInstance(clazz);
        if(x == null){
            try {
                x = clazz.newInstance();
                stats.putInstance(clazz, x);
                onInit.accept(x);
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }
        return Objects.requireNonNull(x);
    }

    public void clear(){
        stats.clear();
    }

    @NotNull
    public Set<Statistic> all(){
        return ImmutableSet.copyOf(stats.values());
    }
}
