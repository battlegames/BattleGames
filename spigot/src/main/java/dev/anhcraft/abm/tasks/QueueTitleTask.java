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
package dev.anhcraft.abm.tasks;

import dev.anhcraft.abm.system.QueueTitle;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Queue;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class QueueTitleTask implements Runnable {
    private final Map<Player, Queue<QueueTitle>> QUEUE = new WeakHashMap<>();

    public void put(Player p, QueueTitle title){
        Queue<QueueTitle> x = QUEUE.get(p);
        if(x == null) {
            x = new ConcurrentLinkedQueue<>();
            QUEUE.put(p, x);
        }
        x.add(title);
    }

    public void remove(Player p){
        QUEUE.remove(p);
    }

    @Override
    public void run() {
        QUEUE.forEach((player, queueTitles) -> {
            QueueTitle title = queueTitles.poll();
            if (title != null) player.sendTitle(title.getTitle(), title.getSubtitle(), 10, 70, 20);
        });
    }
}
