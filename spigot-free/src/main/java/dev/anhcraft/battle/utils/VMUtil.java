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

import dev.anhcraft.inst.VM;
import dev.anhcraft.inst.values.*;
import org.jetbrains.annotations.NotNull;

public class VMUtil {
    public static final String WINDOW_DATA_PREFIX = "_window_";
    public static final String VIEW_DATA_PREFIX = "_view_";

    public static void setVariable(VM vm, String k, Object v){
        if(v instanceof String) {
            vm.setVariable(k, new StringVal() {
                @NotNull
                @Override
                public String get() {
                    return (String) v;
                }
            });
        } if(v instanceof Boolean) {
            vm.setVariable(k, new BoolVal() {
                @NotNull
                @Override
                public Boolean get() {
                    return (Boolean) v;
                }
            });
        } else if(v instanceof Byte) {
            vm.setVariable(k, new IntVal() {
                @NotNull
                @Override
                public Integer get() {
                    return ((Byte) v).intValue();
                }
            });
        } else if(v instanceof Short) {
            vm.setVariable(k, new IntVal() {
                @NotNull
                @Override
                public Integer get() {
                    return ((Short) v).intValue();
                }
            });
        } else if(v instanceof Integer) {
            vm.setVariable(k, new IntVal() {
                @NotNull
                @Override
                public Integer get() {
                    return (Integer) v;
                }
            });
        } else if(v instanceof Long) {
            vm.setVariable(k, new LongVal() {
                @NotNull
                @Override
                public Long get() {
                    return (Long) v;
                }
            });
        } else if(v instanceof Float) {
            vm.setVariable(k, new DoubleVal() {
                @NotNull
                @Override
                public Double get() {
                    return ((Float) v).doubleValue();
                }
            });
        } else if(v instanceof Double) {
            vm.setVariable(k, new DoubleVal() {
                @NotNull
                @Override
                public Double get() {
                    return (Double) v;
                }
            });
        }
    }

    public static int getInt(NumberVal<?> val){
        if (val instanceof IntVal) {
            return ((IntVal) val).get();
        } else if (val instanceof LongVal) {
            return (int) ((LongVal) val).get().longValue();
        } else if (val instanceof DoubleVal) {
            return (int) ((DoubleVal) val).get().doubleValue();
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public static long getLong(NumberVal<?> val){
        if (val instanceof IntVal) {
            return ((IntVal) val).get();
        } else if (val instanceof LongVal) {
            return ((LongVal) val).get();
        } else if (val instanceof DoubleVal) {
            return (long) ((DoubleVal) val).get().doubleValue();
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public static double getDouble(NumberVal<?> val){
        if (val instanceof IntVal) {
            return ((IntVal) val).get();
        } else if (val instanceof LongVal) {
            return ((LongVal) val).get();
        } else if (val instanceof DoubleVal) {
            return ((DoubleVal) val).get();
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
