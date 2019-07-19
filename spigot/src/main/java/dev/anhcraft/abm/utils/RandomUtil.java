package dev.anhcraft.abm.utils;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUtil {
    @Nullable
    public static <E> E pickRandom(@Nullable List<E> list){
        if(list == null) return null;
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }
}
