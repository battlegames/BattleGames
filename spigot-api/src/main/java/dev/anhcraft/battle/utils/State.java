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

package dev.anhcraft.battle.utils;

import org.jetbrains.annotations.NotNull;

public enum State {
    ENABLED(2),
    DISABLED(1),
    ENABLE(4),
    DISABLE(3),
    TRUE(6),
    FALSE(5);

    private final int opposite;
    private final String localePath;

    State(int opposite) {
        this.opposite = opposite;
        localePath = "state." + name().toLowerCase();
    }

    @NotNull
    public State getOppositeState() {
        return values()[opposite];
    }

    @NotNull
    public State inCaseOf(boolean expression) {
        return expression ? this : getOppositeState();
    }

    @NotNull
    public String getLocalePath() {
        return localePath;
    }
}
