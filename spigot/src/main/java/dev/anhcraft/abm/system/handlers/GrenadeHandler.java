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

package dev.anhcraft.abm.system.handlers;

import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.inventory.items.Grenade;
import dev.anhcraft.abm.api.inventory.items.GrenadeModel;
import dev.anhcraft.abm.tasks.EntityTrackingTask;
import dev.anhcraft.craftkit.kits.abif.PreparedItem;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GrenadeHandler extends Handler {
    public GrenadeHandler(BattlePlugin plugin) {
        super(plugin);
    }

    private ItemStack createGrenade(Grenade grenade, GrenadeModel gm){
        PreparedItem pi = plugin.itemManager.make(grenade);
        if(pi == null) return null;
        else {
            pi = gm.getSkin().transform(pi);
            return plugin.itemManager.write(pi.build(), grenade);
        }
    }

    public void selectGrenade(Player player, GrenadeModel g) {
        Grenade grenade = new Grenade();
        grenade.setModel(g);
        player.getInventory().setItem(g.getInventorySlot(), createGrenade(grenade, g));
    }

    public boolean throwGrenade(Player player, Grenade grenade){
        GrenadeModel gm = grenade.getModel();
        if(gm == null) return false;
        Location ploc = player.getLocation();
        PreparedItem pi = plugin.itemManager.make(gm);
        if(pi == null) return false;
        Item item = player.getWorld().dropItem(ploc, gm.getSkin().transform(pi).build());
        item.setInvulnerable(true);
        item.setGravity(true);
        item.setVelocity(ploc.getDirection().normalize().multiply(gm.getVelocityMultiplier()));
        item.setPickupDelay(Integer.MAX_VALUE);
        long dt = gm.getDelayTime();
        plugin.entityTracker.track(item, new EntityTrackingTask.EntityTrackCallback() {
            @Override
            public void onCheck(EntityTrackingTask.EntityTracker tracker, EntityTrackingTask.EntityTrackCallback callback, Entity entity) {
                if(tracker.deltaMoveTime() >= 500 || (dt > 0 && tracker.deltaOriginTime() > dt * 50)){
                    callback.untrack(entity);
                    Location eloc = entity.getLocation();
                    plugin.taskHelper.newTask(() -> {
                        entity.remove();
                        if(gm.getExplosionPower() > 0)
                            entity.getWorld().createExplosion(eloc.getX(), eloc.getY(), eloc.getZ(), (float) gm.getExplosionPower(), false, false);
                    });
                    if(gm.getExplosionEffect() != null)
                        gm.getExplosionEffect().spawn(eloc);
                }
            }

            @Override
            public void onMove(EntityTrackingTask.EntityTracker tracker, EntityTrackingTask.EntityTrackCallback callback, Entity entity) {

            }
        });
        return true;
    }
}
