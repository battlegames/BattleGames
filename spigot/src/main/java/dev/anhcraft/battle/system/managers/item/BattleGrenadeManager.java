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

package dev.anhcraft.battle.system.managers.item;

import dev.anhcraft.battle.BattleComponent;
import dev.anhcraft.battle.BattlePlugin;
import dev.anhcraft.battle.api.inventory.item.Grenade;
import dev.anhcraft.battle.api.inventory.item.GrenadeModel;
import dev.anhcraft.battle.system.debugger.BattleDebugger;
import dev.anhcraft.battle.tasks.EntityTrackingTask;
import dev.anhcraft.battle.utils.BlockUtil;
import dev.anhcraft.battle.utils.MaterialUtil;
import dev.anhcraft.battle.utils.PreparedItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;

public class BattleGrenadeManager extends BattleComponent {
    public BattleGrenadeManager(BattlePlugin plugin) {
        super(plugin);
    }

    public boolean throwGrenade(Player player, Grenade grenade) {
        GrenadeModel gm = grenade.getModel();
        if (gm == null) return false;
        Location ploc = player.getEyeLocation();
        PreparedItem pi = plugin.itemManager.make(gm);
        if (pi == null) return false;
        ArmorStand as = player.getWorld().spawn(ploc, ArmorStand.class);
        as.setBoots(gm.getSkin().transform(pi).build());
        as.setInvulnerable(true);
        as.setGravity(true);
        as.setVelocity(ploc.getDirection().normalize().multiply(gm.getVelocityMultiplier()));
        as.setVisible(false);
        long dt = gm.getDelayTime();
        plugin.entityTrackingTask.track(as, new EntityTrackingTask.EntityTrackCallback() {
            @Override
            public void onCheck(EntityTrackingTask.EntityTracker tracker, EntityTrackingTask.EntityTrackCallback callback, Entity entity) {
                if (tracker.deltaMoveTime() >= 500 || (dt > 0 && tracker.deltaOriginTime() > dt * 50)) {
                    callback.untrack(entity);
                    Location eloc = entity.getLocation();
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        BattleDebugger.startTiming("grenade-track");
                        entity.remove();
                        if (gm.getExplosionPower() > 0) {
                            entity.getWorld().createExplosion(eloc.getX(), eloc.getY(), eloc.getZ(), (float) gm.getExplosionPower(), false, false);
                        }
                        int fbr = gm.getFireBlockRadius();
                        if (fbr > 0) {
                            for (Block b : BlockUtil.getNearbyBlocks(eloc, fbr, 1, fbr)) {
                                if (MaterialUtil.isEmpty(b.getType())) {
                                    b.setType(Material.FIRE);
                                }
                            }
                        }
                        double fmr = gm.getFireMobRadius();
                        int fmt = gm.getFireMobTicks();
                        if (fmr > 0 && fmt > 0) {
                            eloc.getWorld().getNearbyEntities(eloc, fmr, fmr, fmr).forEach(c -> {
                                if (c.getEntityId() != player.getEntityId()
                                        && c.getEntityId() != entity.getEntityId()
                                        && c instanceof LivingEntity) {
                                    entity.setFireTicks(fmt);
                                }
                            });
                        }
                        BattleDebugger.endTiming("grenade-track");
                    });
                    if (gm.getExplosionEffect() != null) {
                        plugin.playEffect(eloc, gm.getExplosionEffect());
                    }
                }
            }

            @Override
            public void onMove(EntityTrackingTask.EntityTracker tracker, EntityTrackingTask.EntityTrackCallback callback, Entity entity) {

            }
        });
        return true;
    }
}
