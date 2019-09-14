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

public abstract class DataTag<T> {
    public static final int INT = 1;
    public static final int BOOL = 2;
    public static final int DOUBLE = 3;
    public static final int STRING = 4;
    public static final int LONG = 5;
    public static final int FLOAT = 6;
    public static final int LIST = 7;

    private T value;

    public DataTag(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public abstract int getId();
}
