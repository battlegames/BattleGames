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

package dev.anhcraft.battle.api.stats;

import dev.anhcraft.battle.api.BattleApi;
import dev.anhcraft.battle.api.storage.tags.DataTag;
import dev.anhcraft.battle.api.storage.tags.IntTag;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class IntCounter extends Statistic<Integer> {
    private final AtomicInteger backend;

    public IntCounter(){
        backend = new AtomicInteger();
    }

    public IntCounter(int value){
        backend = new AtomicInteger(value);
    }

    @Override
    public @NotNull DataTag<Integer> getData() {
        return new IntTag(backend.get());
    }

    @Override
    public void setData(@NotNull Integer value) {
        backend.set(value);
    }

    @Override
    public void reset() {
        backend.set(0);
    }

    public int get() {
        return backend.get();
    }

    public int increase(@Nullable Player who) {
        return increase(who, 1);
    }

    public int increase(@Nullable Player who, int delta) {
        if(delta == 0) return backend.get();
        int x = backend.addAndGet(delta);
        if(hasAdvancementSupport() && who != null) {
            BattleApi.getInstance().getAdvancementManager().report(who, getId(), x);
        }
        return x;
    }
}
