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

import java.util.Objects;

public class SignedInt {
    public static final SignedInt ZERO = new SignedInt();
    public static final SignedInt ONE = new SignedInt(1);
    public static final SignedInt TWO = new SignedInt(2);
    public static final SignedInt THREE = new SignedInt(3);
    public static final SignedInt FOUR = new SignedInt(4);
    public static final SignedInt FIVE = new SignedInt(5);
    public static final SignedInt SIX = new SignedInt(6);
    public static final SignedInt SEVEN = new SignedInt(7);
    public static final SignedInt EIGHT = new SignedInt(8);
    public static final SignedInt NINE = new SignedInt(9);
    public static final SignedInt TEN = new SignedInt(10);

    private int unsigned;
    private boolean negative;

    public SignedInt() {
    }

    public SignedInt(int i) {
        if (i < 0) {
            unsigned = -i;
            negative = true;
        } else {
            unsigned = i;
        }
    }

    private SignedInt(int unsigned, boolean negative) {
        this.unsigned = unsigned;
        this.negative = negative;
    }

    public int getUnsigned() {
        return unsigned;
    }

    public boolean isNegative() {
        return negative;
    }

    @NotNull
    public SignedInt add(SignedInt i) {
        if (negative != i.negative) {
            if (unsigned > i.unsigned) {
                return new SignedInt(unsigned - i.unsigned, negative);
            } else if (unsigned < i.unsigned) {
                return new SignedInt(i.unsigned - unsigned, i.negative);
            } else {
                return ZERO;
            }
        } else {
            return new SignedInt(unsigned + i.unsigned, negative);
        }
    }

    @NotNull
    public SignedInt negate() {
        return new SignedInt(unsigned, !negative);
    }

    @NotNull
    public SignedInt subtract(SignedInt i) {
        return add(i.negate());
    }

    @NotNull
    public SignedInt toNegative() {
        return negative ? this : negate();
    }

    @NotNull
    public SignedInt toPositive() {
        return negative ? negate() : this;
    }

    public int asInt() {
        return negative ? -unsigned : unsigned;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SignedInt signedInt = (SignedInt) o;
        return unsigned == signedInt.unsigned &&
                negative == signedInt.negative;
    }

    @Override
    public int hashCode() {
        return Objects.hash(unsigned, negative);
    }
}
