package dev.anhcraft.abm.system.workers;

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
