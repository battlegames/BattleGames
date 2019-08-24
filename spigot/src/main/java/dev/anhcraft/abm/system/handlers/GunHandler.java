package dev.anhcraft.abm.system.handlers;

import dev.anhcraft.abif.PreparedItem;
import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.entity.Bullet;
import dev.anhcraft.abm.api.entity.BulletEntity;
import dev.anhcraft.abm.api.events.PlayerDamageEvent;
import dev.anhcraft.abm.api.game.Game;
import dev.anhcraft.abm.api.inventory.items.*;
import dev.anhcraft.abm.api.misc.DamageReport;
import dev.anhcraft.abm.api.misc.Skin;
import dev.anhcraft.abm.system.controllers.ModeController;
import dev.anhcraft.abm.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Map;

public class GunHandler extends Handler {
    public GunHandler(BattlePlugin plugin) {
        super(plugin);
    }

    public ItemStack createGun(Gun gunItem, boolean secondarySkin){
        return gunItem.getModel()
                .map(gunModel -> {
                    Skin skin = secondarySkin ? gunModel.getSecondarySkin() : gunModel.getPrimarySkin();
                    PreparedItem pi = plugin.itemManager.make(gunItem);
                    if(pi == null) return null;
                    else {
                        pi = skin.transform(pi);
                        return plugin.itemManager.write(pi.build(), gunItem);
                    }
                }).orElse(null);
    }

    public void selectGun(Player player, GunModel g) {
        Map.Entry<AmmoModel, Integer> ammo = g.getDefaultMagazine().getAmmunition().entrySet().iterator().next();
        Gun gun = new Gun();
        gun.setModel(g);
        Magazine magazine = gun.getMagazine();
        magazine.setModel(g.getDefaultMagazine());
        magazine.setAmmoCount(ammo.getValue());
        Ammo ammoItem = magazine.getAmmo();
        ammoItem.setModel(ammo.getKey());

        player.getInventory().setItem(g.getInventorySlot(), createGun(gun, false));
        int held = player.getInventory().getHeldItemSlot();
        if(held == g.getInventorySlot()) {
            player.getInventory().setItemInOffHand(createGun(gun, true));
            PlayerUtil.reduceSpeed(player, g.getWeight());
        }
    }

    private double getBodyHeight(LivingEntity e) {
        if (e instanceof Player) {
            Player p = (Player)e;
            double z = p.isSleeping() ? 0.2 : (p.isSneaking() ? 1.65 : (p.isGliding() ? 0.6 : 1.8));
            return z * 0.9;
        }
        if (e instanceof Ageable && !((Ageable)e).isAdult()) return 1.35;
        if (e instanceof Zombie && ((Zombie)e).isBaby()) return 1.35;
        return 2.2;
    }

    private boolean isHeadShot(Location q, LivingEntity ve) {
        return q.getY() - ve.getLocation().getY() > getBodyHeight(ve);
    }

    public boolean shoot(Game game, Player player, Gun gunItem){
        ModeController mc = (ModeController) game.getMode().getController();
        if(mc != null){
            if(mc.RELOADING_GUN.containsKey(player.getUniqueId())){
                plugin.chatManager.sendPlayer(player, "gun.reloading_warn");
                return false;
            }
        }
        Magazine mag = gunItem.getMagazine();
        if(!mag.getModel().isPresent()) {
            plugin.chatManager.sendPlayer(player, "gun.none_magazine_message");
            return false;
        }
        if(!mag.getAmmo().getModel().isPresent() || mag.getAmmoCount() == 0) {
            plugin.chatManager.sendPlayer(player, "gun.out_of_ammo");
            return false;
        }
        mag.setAmmoCount(mag.getAmmoCount()-1);

        gunItem.getModel().ifPresent(s -> s.getShootSound().play(player.getWorld(), player.getLocation()));
        Vector originVec = player.getEyeLocation().toVector();
        List<Bullet> bullets = mag.getAmmo().getModel().get().getBullets();
        for(Bullet b : bullets){
            Location start = player.getEyeLocation();
            BulletEntity entity = new BulletEntity(start, b);
            for(double d = 0.5; d < 100; d += 0.5){
                Location clone = start.clone();
                entity.setLocation(clone.add(clone.getDirection().normalize().multiply(d)));
                entity.spawnParticle();
                Block block = entity.getLocation().getBlock();
                if(block.getType().isSolid()) break;

                 block.getWorld().getNearbyEntities(entity.getLocation(), 0.5, 0.5, 0.5).stream()
                     .filter(entity1 -> entity1 instanceof LivingEntity && !entity1.equals(player))
                     .forEach(e -> {
                         LivingEntity ve = (LivingEntity) e;
                         DamageReport dr = new DamageReport(player, b.getDamage());
                         dr.setHeadshotDamage(isHeadShot(entity.getLocation(), ve));
                         PlayerDamageEvent event = new PlayerDamageEvent(game, dr, ve, gunItem);
                         Bukkit.getPluginManager().callEvent(event);
                         if(event.isCancelled()) return;

                         ve.damage(event.getDamage(), player);
                         Vector vec = ve.getVelocity().add(ve.getLocation().toVector().subtract(originVec)
                                 .normalize().multiply(b.getKnockback()));
                         ve.setVelocity(vec);
                     });
            }
        }
        return true;
    }
}
