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

import dev.anhcraft.battle.api.storage.tags.DataTag;
import dev.anhcraft.battle.impl.Resettable;
import org.jetbrains.annotations.NotNull;

public abstract class Statistic<T> implements Resettable {
    private boolean advancementSupport;

    @NotNull
    public abstract String getId();

    @NotNull
    public abstract DataTag<T> getData();

    public abstract void setData(@NotNull T value);

    public boolean hasAdvancementSupport() {
        return advancementSupport;
    }

    public void setAdvancementSupport(boolean advancementSupport) {
        this.advancementSupport = advancementSupport;
    }
}
