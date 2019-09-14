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
package dev.anhcraft.abm.api.storage.tags;

import dev.anhcraft.jvmkit.utils.Condition;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class DataTag<T> {
    public static final int INT = 1;
    public static final int BOOL = 2;
    public static final int DOUBLE = 3;
    public static final int STRING = 4;
    public static final int LONG = 5;
    public static final int FLOAT = 6;
    public static final int LIST = 7;

    private T value;

    public DataTag(@NotNull T value) {
        Condition.argNotNull("value", value);
        this.value = value;
    }

    @NotNull
    public T getValue() {
        return value;
    }

    public abstract int getId();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataTag<?> dataTag = (DataTag<?>) o;
        return getId() == dataTag.getId() && value.equals(dataTag.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), value);
    }
}
