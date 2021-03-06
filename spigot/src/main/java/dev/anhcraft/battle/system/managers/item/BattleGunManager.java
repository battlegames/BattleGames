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
import dev.anhcraft.battle.api.arena.game.LocalGame;
import dev.anhcraft.battle.api.events.game.WeaponUseEvent;
import dev.anhcraft.battle.api.inventory.ItemSkin;
import dev.anhcraft.battle.api.inventory.item.*;
import dev.anhcraft.battle.api.reports.PlayerAttackReport;
import dev.anhcraft.battle.system.controllers.GameControllerImpl;
import dev.anhcraft.battle.system.debugger.BattleDebugger;
import dev.anhcraft.battle.utils.*;
import dev.anhcraft.config.bukkit.NMSVersion;
import dev.anhcraft.jvmkit.utils.Pair;
import dev.anhcraft.jvmkit.utils.RandomUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class BattleGunManager extends BattleComponent {
    private static final ItemStack PUMPKIN_HELMET = new ItemStack(NMSVersion.current().compare(NMSVersion.v1_13_R1) >= 0 ? Material.valueOf("CARVED_PUMPKIN") : Material.PUMPKIN, 1);

    public BattleGunManager(BattlePlugin plugin) {
        super(plugin);
    }

    public ItemStack createGun(Gun gun, boolean secondarySkin) {
        if (gun.getModel() == null) return null;
        ItemSkin skin = secondarySkin ? gun.getModel().getSecondarySkin() : gun.getModel().getPrimarySkin();
        PreparedItem pi = plugin.itemManager.make(gun);
        if (pi == null) return null;
        else {
            pi = skin.transform(pi);
            return plugin.itemManager.write(pi.build(), gun);
        }
    }

    public boolean selectGun(Player player, GunModel g) {
        Gun gun = new Gun();
        gun.setModel(g);
        Map.Entry<AmmoModel, Integer> ammo = g.getDefaultMagazine().getAmmunition().entrySet().iterator().next();
        Magazine magazine = gun.getMagazine();
        magazine.setModel(g.getDefaultMagazine());
        magazine.setAmmoCount(ammo.getValue());
        Ammo ammoItem = magazine.getAmmo();
        ammoItem.setModel(ammo.getKey());
        return selectGun(player, gun);
    }

    public boolean selectGun(Player player, Gun gun) {
        GunModel g = Objects.requireNonNull(gun.getModel());
        int slot = player.getInventory().firstEmpty();
        if (slot == -1) return false;
        player.getInventory().setItem(slot, createGun(gun, false));
        int held = player.getInventory().getHeldItemSlot();
        if (held == slot) {
            player.getInventory().setItemInOffHand(createGun(gun, true));
            SpeedUtil.setModifier(player, SpeedFactor.ITEM, -g.getWeight());
        }
        return true;
    }

    private boolean isHeadShot(Location q, BoundingBox box) {
        double d1 = box.getHeight();
        double d2 = box.getMaxY() - q.getY();
        return d2 < d1 / 4;
    }

    private void rmvZoom(Player player) {
        plugin.playerListener.FROZEN_PLAYERS.remove(player.getUniqueId());
        player.removePotionEffect(PotionEffectType.SLOW);
        player.getInventory().setHelmet(null);
        player.removeMetadata("zoom", plugin);
        SpeedUtil.setModifier(player, SpeedFactor.ZOOM, 0);
    }

    public boolean handleZoomOut(Player player) {
        if (player.hasMetadata("zoom")) {
            List<MetadataValue> x = player.getMetadata("zoom");
            for (MetadataValue v : x) {
                if (v.getOwningPlugin() == plugin && v.asInt() != -1) {
                    rmvZoom(player);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean handleZoomIn(LocalGame localGame, Player player, Gun gunItem) {
        GameControllerImpl mc = (GameControllerImpl) localGame.getMode().getController();
        if (mc != null) {
            if (mc.RELOADING_GUN.containsKey(player.getUniqueId())) {
                plugin.chatManager.sendPlayer(player, "gun.reloading_warn");
                return false;
            }
        }
        GunModel gm = gunItem.getModel();
        if (gm == null) return false;
        Scope scp = gunItem.getScope();
        if (scp == null) {
            if (gm.getDefaultScope() != null) {
                gunItem.setScope(scp = new Scope());
                scp.setModel(gm.getDefaultScope());
            }
        }
        if (scp == null || scp.getModel() == null) {
            plugin.chatManager.sendPlayer(player, "gun.none_scope_message");
            return false;
        }
        int next = scp.nextZoomLevel();
        if (next == -1) {
            rmvZoom(player);
        } else {
            ScopeModel sm = scp.getModel();
            int nextLv = sm.getZoomLevels().get(next);
            player.getInventory().setHelmet(PUMPKIN_HELMET);
            player.setMetadata("zoom", new FixedMetadataValue(plugin, nextLv));
            SpeedUtil.setModifier(player, SpeedFactor.ZOOM, -9999);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 696969, nextLv, false), true);
            plugin.playerListener.FROZEN_PLAYERS.put(player.getUniqueId(), player.getLocation());
        }
        return true;
    }

    public boolean shoot(LocalGame localGame, Player player, Gun gunItem) {
        GunModel gm = gunItem.getModel();
        if (gm == null) return false;
        GameControllerImpl mc = (GameControllerImpl) localGame.getMode().getController();
        if (mc != null) {
            if (mc.RELOADING_GUN.containsKey(player.getUniqueId())) {
                plugin.chatManager.sendPlayer(player, "gun.reloading_warn");
                return false;
            }
        }
        Magazine mag = gunItem.getMagazine();
        if (mag.getModel() == null) {
            plugin.chatManager.sendPlayer(player, "gun.none_magazine_message");
            return false;
        }
        if (mag.getAmmo().getModel() == null || mag.getAmmoCount() == 0) {
            if (plugin.getGeneralConfig().shouldAutoReloadGun()) {
                Objects.requireNonNull((GameControllerImpl) localGame.getMode().getController()).doReloadGun(player, gunItem);
                return false;
            }
            plugin.chatManager.sendPlayer(player, "gun.out_of_ammo");
            return false;
        }
        mag.setAmmoCount(mag.getAmmoCount() - 1);
        gm.getShootSound().play(player.getLocation());

        BattleDebugger.startTiming("gun-shoot");
        Location start = player.getEyeLocation();
        Vector originVec = player.getEyeLocation().toVector();
        Vector dir = start.getDirection().normalize();
        Vector sprayVec;
        if (gunItem.nextSpray() != -1) {
            Pair<Double, Double> pair = gm.getSprayPattern().get(gunItem.getNextSpray());
            if (pair.getFirst() == null || pair.getSecond() == null) {
                throw new IllegalStateException();
            }
            sprayVec = new Vector(pair.getFirst(), pair.getSecond(), pair.getFirst());
            if (sprayVec.length() > 0) {
                sprayVec.normalize();
            }
            VectUtil.rotate(sprayVec, start.getYaw(), start.getPitch());
            sprayVec.multiply(player.isSneaking() ? 0.25 : 0.4);
        } else
            sprayVec = new Vector();

        Map<LivingEntity, BoundingBox> entities = new HashMap<>();
        for (LivingEntity livingEntity : start.getWorld().getEntitiesByClass(LivingEntity.class)) {
            entities.put(livingEntity, EntityUtil.getBoundingBox(livingEntity).expand(.25, .25, .25));
        }

        final double maxHeight = player.getWorld().getMaxHeight();
        final double angle = Math.toRadians(-start.getPitch() / 5);
        final double cosA = Math.cos(angle);
        final double sinA = Math.sin(angle);

        Collection<Ammo.Bullet> bullets = mag.getAmmo().getModel().getBullets();
        for (Ammo.Bullet b : bullets) {
            Block lastBlock = null;
            int power = b.getPenetrationPower();
            long currentTime = 0;
            while (true) {
                double deltaTime = (currentTime += b.getTimeOffset()) / 1000d;
                double x = gm.getMuzzleVelocity() * cosA * deltaTime;
                double y = gm.getMuzzleVelocity() * sinA * deltaTime - 0.5 * 9.8 * deltaTime * deltaTime;
                Location loc = start.clone().add(dir.clone().multiply(x).add(sprayVec)).add(0, y, 0);
                if (loc.getY() > maxHeight || loc.getY() < 0) break;

                if (b.getParticleEffect() != null && ThreadLocalRandom.current().nextDouble() <= plugin.generalConf.getBulletParticleChance())
                    b.getParticleEffect().spawn(loc);

                Block block = loc.getBlock();
                if (lastBlock == null || !lastBlock.equals(block)) {
                    power -= plugin.generalConf.getMaterialHardness(block.getType());
                    if (power <= 0) {
                        int id = loc.hashCode();
                        int st = RandomUtil.randomInt(0, 9);
                        BlockUtil.createBreakAnimation(id, block, st, entities.keySet().stream()
                                .filter(ent -> ent instanceof Player)
                                .map(livingEntity -> (Player) livingEntity)
                                .collect(Collectors.toList()));
                        break;
                    } else lastBlock = block;
                }

                for (Map.Entry<LivingEntity, BoundingBox> ent : entities.entrySet()) {
                    LivingEntity ve = ent.getKey();
                    if (ve.equals(player) || !ent.getValue().contains(loc)) continue;
                    PlayerAttackReport attackReport = new PlayerAttackReport(ve, b.getDamage(), player, gunItem);
                    attackReport.setHeadshotDamage(isHeadShot(loc, ent.getValue()));
                    WeaponUseEvent event = new WeaponUseEvent(localGame, attackReport);
                    Bukkit.getPluginManager().callEvent(event);
                    if (event.isCancelled()) continue;

                    ve.damage(event.getReport().getDamage(), player);
                    if (b.getFireTicks() > 0) ve.setFireTicks(b.getFireTicks());
                    if(Math.abs(b.getKnockback()) > 0.01) {
                        Vector vec = ve.getVelocity().add(ve.getLocation().toVector().subtract(originVec)
                                .normalize().multiply(b.getKnockback()));
                        ve.setVelocity(vec);
                    }
                    power -= plugin.generalConf.getEntityHardness(ve.getType());
                    EntityEquipment ee = ve.getEquipment();
                    if (ee != null) {
                        for (ItemStack item : ee.getArmorContents()) {
                            if (ItemUtil.isEmpty(item)) continue;
                            power -= plugin.generalConf.getMaterialHardness(item.getType());
                        }
                    }
                }
            }
        }
        BattleDebugger.endTiming("gun-shoot");
        return true;
    }
}
