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
package dev.anhcraft.battle.system.cleaners;

import dev.anhcraft.battle.BattleComponent;
import dev.anhcraft.battle.BattlePlugin;
import dev.anhcraft.battle.api.arena.Arena;
import dev.anhcraft.battle.system.cleaners.works.RollbackWork;
import dev.anhcraft.battle.system.cleaners.works.Work;

import java.util.function.Consumer;

public class GameCleaner extends BattleComponent {
    private final Work[] works = new Work[]{
            new RollbackWork()
    };

    public GameCleaner(BattlePlugin plugin) {
        super(plugin);
    }

    public void newSession(Arena arena, Consumer<Arena> onFinished) {
        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            plugin.getLogger().info(String.format("Cleaning arena %s (%s works remaining)", arena.getId(), works.length));

            WorkSession workSession = new WorkSession(works.length, plugin.getLogger());
            for (Work work : works) {
                work.handle(plugin, workSession, arena);
            }
            workSession.await();
            plugin.getLogger().info("Finished works! Arena " + arena.getId() + " is now ready.");
            onFinished.accept(arena);
        }, 60);
    }
}
