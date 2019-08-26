package dev.anhcraft.abm.utils;

import dev.anhcraft.jvmkit.utils.Condition;
import org.bukkit.Bukkit;

public class EnumUtil {
    public static <E extends Enum> E getEnum(E[] list, String str){
        Condition.notEmpty(list);
        Condition.notNull(str);

        str = str.toUpperCase();
        for(E e : list){
            if(e.name().equals(str)) return e;
        }
        StackTraceElement stacktrace = Thread.currentThread().getStackTrace()[2];
        E def = list[list.length-1];
        Bukkit.getLogger().warning(String.format("%s#%s() | Enum `%s` not found! Using default: `%s`", stacktrace.getClassName(), stacktrace.getMethodName(), str, def.name()));
        return def;
    }
}
