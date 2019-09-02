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
package dev.anhcraft.abm.system.cleaners;

import dev.anhcraft.abm.api.game.Arena;
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

    public void destroy(){
        POOL.shutdown();
    }
}
