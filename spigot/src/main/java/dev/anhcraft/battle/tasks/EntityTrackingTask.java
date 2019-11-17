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

package dev.anhcraft.battle.tasks;

import dev.anhcraft.battle.BattleComponent;
import dev.anhcraft.battle.BattlePlugin;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EntityTrackingTask extends BattleComponent implements Runnable {
    private static EntityTrackingTask task;

    public EntityTrackingTask(BattlePlugin plugin){
        super(plugin);
        if(task != null) throw new UnsupportedOperationException();
        task = this;
    }

    public interface EntityTrackCallback {
        void onCheck(EntityTracker tracker, EntityTrackCallback callback, Entity entity);
        void onMove(EntityTracker tracker, EntityTrackCallback callback, Entity entity);

        default void untrack(Entity entity){
            EntityTracker x = task.MAP.get(entity);
            if(x != null) {
                x.callbacks.remove(this);
                if(x.callbacks.isEmpty()) {
                    task.MAP.remove(entity);
                }
            }
        }
    }

    public static class EntityTracker{
        private Entity entity;
        private Location lastLoc;
        private final List<EntityTrackCallback> callbacks = new LinkedList<>();
        private long originTime = System.currentTimeMillis();
        private long lastMoveTime = System.currentTimeMillis();

        public EntityTracker(Entity entity) {
            this.entity = entity;
            lastLoc = entity.getLocation();
        }

        @NotNull
        public Location getLastLocation(){
            return lastLoc;
        }

        @NotNull
        public Entity getEntity() {
            return entity;
        }

        public long getLastMoveTime() {
            return lastMoveTime;
        }

        public long deltaMoveTime(){
            return System.currentTimeMillis() - lastMoveTime;
        }

        public long getOriginTime() {
            return originTime;
        }

        public long deltaOriginTime(){
            return System.currentTimeMillis() - originTime;
        }
    }

    private final Map<Entity, EntityTracker> MAP = new ConcurrentHashMap<>();

    public void track(Entity entity, EntityTrackCallback callback){
        MAP.computeIfAbsent(entity, entity1 -> new EntityTracker(entity)).callbacks.add(callback);
    }

    @Override
    public void run() {
        for (Map.Entry<Entity, EntityTracker> e : MAP.entrySet()) {
            if (e.getKey().isDead()) {
                MAP.remove(e.getKey());
            }
            else {
                Location loc = e.getKey().getLocation();
                EntityTracker track = e.getValue();
                boolean b = false;
                if (loc.distanceSquared(track.lastLoc) >= plugin.GENERAL_CONF.getEntityTrackMinDistance()) {
                    track.lastMoveTime = System.currentTimeMillis();
                    track.lastLoc = loc;
                    b = true;
                }
                for (EntityTrackCallback c : track.callbacks) {
                    c.onCheck(track, c, e.getKey());
                    if (b) c.onMove(track, c, e.getKey());
                }
            }
        }
    }
}
