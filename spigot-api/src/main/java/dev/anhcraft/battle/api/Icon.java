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

public enum Icon {
    MONEY_BAG("\u3400"),
    LETTER("\u3401"),
    ONLINE("\u3402"),
    USER("\u3403"),
    MEDAL("\u3404"),
    GOLD_CUP("\u3405"),
    HEADSHOT("\u3406"),
    GUN("\u3407"),
    EXIT("\u3408"),
    CHART("\u3409"),
    ACCEPT("\u340a"),
    DENY("\u340b"),
    WARN("\u340c"),
    INFO("\u340d");

    private final String character;

    Icon(String character) {
        this.character = character;
    }

    public String getChar() {
        return this.character;
    }
}
