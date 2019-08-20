package dev.anhcraft.abm.system.managers;

import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.BattleComponent;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

public class TaskManager extends BattleComponent {
    private static final BukkitScheduler SCHEDULER = Bukkit.getScheduler();

    public TaskManager(BattlePlugin plugin) {
        super(plugin);
    }

    public int newTask(@NotNull Runnable task) {
        return SCHEDULER.runTask(plugin, task).getTaskId();
    }

    public int newAsyncTask(@NotNull Runnable task) {
        return SCHEDULER.runTaskAsynchronously(plugin, task).getTaskId();
    }

    public int newDelayedTask(@NotNull Runnable task, long delay) {
        return SCHEDULER.runTaskLater(plugin, task, delay).getTaskId();
    }

    public int newDelayedAsyncTask(@NotNull Runnable task, long delay) {
        return SCHEDULER.runTaskLaterAsynchronously(plugin, task, delay).getTaskId();
    }

    public int newTimerTask(@NotNull Runnable task, long delay, long interval) {
        return SCHEDULER.runTaskTimer(plugin, task, delay, interval).getTaskId();
    }

    public int newAsyncTimerTask(@NotNull Runnable task, long delay, long interval) {
        return SCHEDULER.runTaskTimerAsynchronously(plugin, task, delay, interval).getTaskId();
    }

    public void cancelTask(int id) {
        SCHEDULER.cancelTask(id);
    }

    public boolean isRunning(int id) {
        return SCHEDULER.isCurrentlyRunning(id);
    }
}
