package dev.anhcraft.abm.system.cleaners;

import dev.anhcraft.abm.api.objects.Arena;
import dev.anhcraft.abm.system.cleaners.works.BlockRestoration;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class GameCleaner {
    private final ExecutorService POOL = Executors.newFixedThreadPool(3);
    private final BlockRestoration[] WORKS = new BlockRestoration[]{
            new BlockRestoration()
    };

    public void doClean(Arena arena, Consumer<Arena> onFinished){
        POOL.submit(() -> {
            Arrays.stream(WORKS).forEach(x -> x.accept(arena));
            onFinished.accept(arena);
        });
    }
}
