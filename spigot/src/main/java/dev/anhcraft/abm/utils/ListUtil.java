package dev.anhcraft.abm.utils;

import java.util.List;
import java.util.function.Function;

public class ListUtil {
    public static <T> void update(List<T> list, Function<T, T> f){
        for (int i = 0; i < list.size(); i++){
            list.set(i, f.apply(list.get(i)));
        }
    }
}
