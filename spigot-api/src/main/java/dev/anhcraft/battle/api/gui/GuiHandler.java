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
package dev.anhcraft.battle.api.gui;

import dev.anhcraft.battle.utils.functions.Function;
import dev.anhcraft.jvmkit.utils.Condition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public abstract class GuiHandler {
    private interface BackendCaller {
        void call(SlotReport event, String... args);
    }

    private final Map<String, BackendCaller> slotListeners = new HashMap<>();
    private final GuiHandler instance = this;

    protected GuiHandler(){
        Method[] methods = getClass().getDeclaredMethods();
        for (Method method : methods){
            method.setAccessible(true);
            if(method.isAnnotationPresent(Function.class) && method.getParameterCount() >= 1 && SlotReport.class.isAssignableFrom(method.getParameterTypes()[0])){
                Function fn = method.getDeclaredAnnotation(Function.class);
                int paramLen = method.getParameterCount() - 1;
                slotListeners.put(fn.value(), (report, args) -> {
                    if(paramLen == 0) {
                        try {
                            method.invoke(instance, report);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    } else if(paramLen <= args.length) {
                        int len = Math.min(args.length, paramLen);
                        Object[] f = new Object[len + 1];
                        f[0] = report;
                        System.arraycopy(args, 0, f, 1, len);
                        try {
                            method.invoke(instance, f);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

    public boolean fireEvent(@Nullable String target, @NotNull SlotReport report, String... params) {
        Condition.argNotNull("report", report);
        BackendCaller backendCaller = slotListeners.get(target);
        if (backendCaller != null) {
            backendCaller.call(report, params == null ? new String[0] : params);
            return true;
        }
        return false;
    }
}