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
package dev.anhcraft.battle.system.listeners;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import dev.anhcraft.battle.BattleComponent;
import dev.anhcraft.battle.BattlePlugin;
import dev.anhcraft.battle.api.BattleApi;
import dev.anhcraft.battle.api.MouseClick;
import dev.anhcraft.battle.api.WorldSettings;
import dev.anhcraft.battle.api.arena.Arena;
import dev.anhcraft.battle.api.arena.game.GamePhase;
import dev.anhcraft.battle.api.arena.game.GamePlayer;
import dev.anhcraft.battle.api.arena.game.LocalGame;
import dev.anhcraft.battle.api.events.ItemChooseEvent;
import dev.anhcraft.battle.api.events.game.GamePlayerDamageEvent;
import dev.anhcraft.battle.api.events.game.GamePlayerDeathEvent;
import dev.anhcraft.battle.api.events.game.GamePlayerWeaponUseEvent;
import dev.anhcraft.battle.api.events.game.WeaponUseEvent;
import dev.anhcraft.battle.api.gui.NativeGui;
import dev.anhcraft.battle.api.gui.screen.Window;
import dev.anhcraft.battle.api.inventory.item.*;
import dev.anhcraft.battle.api.reports.DamageReport;
import dev.anhcraft.battle.api.reports.PlayerAttackReport;
import dev.anhcraft.battle.api.reports.PlayerAttackedReport;
import dev.anhcraft.battle.api.reports.PlayerDamagedReport;
import dev.anhcraft.battle.api.stats.StatisticMap;
import dev.anhcraft.battle.api.stats.natives.*;
import dev.anhcraft.battle.api.storage.data.PlayerData;
import dev.anhcraft.battle.system.QueueTitle;
import dev.anhcraft.battle.system.ResourcePack;
import dev.anhcraft.battle.system.controllers.GameControllerImpl;
import dev.anhcraft.battle.system.debugger.BattleDebugger;
import dev.anhcraft.battle.utils.*;
import dev.anhcraft.battle.utils.info.InfoHolder;
import dev.anhcraft.battle.utils.info.InfoReplacer;
import dev.anhcraft.config.bukkit.NMSVersion;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerListener extends BattleComponent implements Listener {
    public final Map<UUID, Location> FROZEN_PLAYERS = new HashMap<>();

    public PlayerListener(BattlePlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void move(PlayerMoveEvent event) {
        Location to = event.getTo();
        if(to == null || to.getWorld() == null) return;
        Location last = FROZEN_PLAYERS.get(event.getPlayer().getUniqueId());
        if(last != null) {
            if(Objects.equals(last.getWorld(), to.getWorld())) {
                double offX = to.getX() - last.getX();
                double offY = to.getY() - last.getY();
                double offZ = to.getZ() - last.getZ();
                if (offX * offX + offY * offY + offZ * offZ >= 1) {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void join(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        handleJoin(player);
    }

    public void handleJoin(Player player) {
        SpeedUtil.resetSpeed(player);
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (!player.isOnline()) return;
            BattleDebugger.startTiming("player-join");
            for (PotionEffect pe : player.getActivePotionEffects()) {
                player.removePotionEffect(pe.getType());
            }
            EntityUtil.teleport(player, plugin.getServerData().getSpawnPoint(), ok -> {
                plugin.guiManager.setBottomGui(player, NativeGui.MAIN_PLAYER_INV);
                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                    if (plugin.generalConf.isResourcePackEnabled()) {
                        ResourcePack.send(player);
                    }
                    PlayerData playerData = plugin.dataManager.loadPlayerData(player);
                    // back to main thread
                    plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                        plugin.resetScoreboard(player);
                        plugin.listKits(kit -> {
                            if (kit.isFirstJoin() && !playerData.getReceivedFirstJoinKits().contains(kit.getId())) {
                                kit.givePlayer(player, playerData);
                                playerData.getReceivedFirstJoinKits().add(kit.getId());
                            }
                        });
                        if (player.hasPermission("battle.pleasesetrollback")) {
                            for (Arena arena : plugin.listArenas()) {
                                if (arena.getRollback() == null) {
                                    player.sendMessage(ChatColor.GOLD + "For safety reasons, you should specify rollback for arena #" + arena.getId());
                                }
                            }
                        }
                        BattleDebugger.endTiming("player-join");
                    }, 40);
                });
            });
        }, 15);
    }

    @EventHandler
    public void quit(PlayerQuitEvent event) {
        plugin.guiManager.destroyWindow(event.getPlayer());
        plugin.arenaManager.quit(event.getPlayer());
        plugin.gunManager.handleZoomOut(event.getPlayer());
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> plugin.dataManager.unloadPlayerData(event.getPlayer()));
    }

    @EventHandler
    public void resourcePackStatus(PlayerResourcePackStatusEvent event) {
        if (plugin.generalConf.isResourcePackEnabled()) {
            Player player = event.getPlayer();
            switch (event.getStatus()) {
                case DECLINED: {
                    BattleApi.getInstance().getChatManager().sendPlayer(player, "resource_pack.declined");
                    if(!plugin.generalConf.isResourcePackOptional()) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                player.kickPlayer("Resource pack declined");
                            }
                        }.runTaskLater(plugin, 60);
                    }
                    break;
                }
                case FAILED_DOWNLOAD: {
                    BattleApi.getInstance().getChatManager().sendPlayer(player, "resource_pack.failed");
                    if(!plugin.generalConf.isResourcePackOptional()) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                player.kickPlayer("Resource pack installation failed");
                            }
                        }.runTaskLater(plugin, 60);
                    }
                    break;
                }
                case SUCCESSFULLY_LOADED: {
                    BattleApi.getInstance().getChatManager().sendPlayer(player, "resource_pack.loaded");
                    break;
                }
                case ACCEPTED: {
                    BattleApi.getInstance().getChatManager().sendPlayer(player, "resource_pack.accepted");
                }
            }
        }
    }

    @EventHandler
    public void food(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player p = (Player) event.getEntity();
            LocalGame game = plugin.arenaManager.getGame(p);
            if (game != null) {
                if (game.getPhase() == GamePhase.WAITING) {
                    event.setCancelled(true);
                    event.setFoodLevel(20);
                }
            }
        }
    }

    @EventHandler
    public void swap(PlayerSwapHandItemsEvent event) {
        plugin.guiManager.callClickEvent(event.getPlayer(), event.getPlayer().getInventory().getHeldItemSlot(), false, event, "SWAP_ITEM");
        LocalGame game = plugin.arenaManager.getGame(event.getPlayer());
        if (game != null) {
            game.getMode().getController(c -> c.onSwapItem(event, game));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void drop(PlayerDropItemEvent event) {
        Player p = event.getPlayer();
        LocalGame game = plugin.arenaManager.getGame(p);
        if (game != null) {
            game.getMode().getController(c -> c.onDropItem(event, game));
        }
        InventoryType f = p.getOpenInventory().getType();
        if (f == InventoryType.CRAFTING || (f == InventoryType.CREATIVE && p.getGameMode() == GameMode.CREATIVE)) {
            plugin.guiManager.callClickEvent(p, p.getInventory().getHeldItemSlot(), false, event, "DROP_ITEM");
        }
    }

    @EventHandler
    public void chooseItem(ItemChooseEvent event) {
        LocalGame game = plugin.arenaManager.getGame(event.getPlayer());
        if (game != null) {
            game.getMode().getController(c -> c.onChooseItem(event, game));
        }
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null) {
            String s = plugin.getServerData().getJoinSign(BlockPosition.of(event.getClickedBlock()));
            if (s != null) {
                Arena a = plugin.getArena(s);
                if (a != null) {
                    plugin.arenaManager.join(p, a);
                }
                return;
            }
        }
        LocalGame g = plugin.arenaManager.getGame(p);
        if (event.getAction() != Action.PHYSICAL) {
            WorldSettings ws = plugin.getWorldSettings(event.getPlayer().getWorld().getName());
            if (ws != null && ws.isInteractDisabled()) {
                event.setCancelled(true);
            }
            ItemStack item = event.getItem();
            if (item != null && item.getType() == Material.STONE_SWORD && item.getItemMeta() != null && item.getItemMeta().isUnbreakable()) {
                if (item.getDurability() == 1) {
                    double max = p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                    double now = p.getHealth();
                    if (max != now) {
                        p.setHealth(Math.min(max, plugin.generalConf.getMedicalKitBonusHealth() + now));
                        p.getInventory().setItemInMainHand(null);
                        if (plugin.generalConf.getMedicalKitUseSound() != null) {
                            plugin.generalConf.getMedicalKitUseSound().play(p.getLocation());
                        }
                        PlayerData pd = BattleApi.getInstance().getPlayerData(p);
                        if (pd != null) pd.getStats().of(MedicalKitUseStat.class).increase(p);
                    }
                    event.setCancelled(true);
                    return;
                } else if (item.getDurability() == 4) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 300, 0));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 300, 0));
                    if (plugin.generalConf.getAdrenalineShotUseSound() != null) {
                        plugin.generalConf.getAdrenalineShotUseSound().play(p.getLocation());
                    }
                    p.getInventory().setItemInMainHand(null);
                    PlayerData pd = BattleApi.getInstance().getPlayerData(p);
                    if (pd != null) pd.getStats().of(AdrenalineShotUseStat.class).increase(p);
                    event.setCancelled(true);
                    return;
                }
            }
            BattleItem bi = plugin.itemManager.read(item);
            if (bi != null) {
                LocalGame game = plugin.arenaManager.getGame(p);
                if (game != null && game.getPhase() == GamePhase.PLAYING) {
                    boolean left = event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK;
                    boolean right = event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK;
                    boolean act1 = (left && plugin.generalConf.getGunShootClick() == MouseClick.LEFT_CLICK)
                            || (right && plugin.generalConf.getGunShootClick() == MouseClick.RIGHT_CLICK);
                    boolean act2 = (left && plugin.generalConf.getGunZoomClick() == MouseClick.LEFT_CLICK)
                            || (right && plugin.generalConf.getGunZoomClick() == MouseClick.RIGHT_CLICK);
                    boolean act3 = (left && plugin.generalConf.getGrenadeThrowClick() == MouseClick.LEFT_CLICK)
                            || (right && plugin.generalConf.getGrenadeThrowClick() == MouseClick.RIGHT_CLICK);
                    if (bi instanceof Gun && (act1 || act2)) {
                        Gun gun = (Gun) bi;
                        if (act1) {
                            if (plugin.gunManager.shoot(game, p, gun)) {
                                p.getInventory().setItemInMainHand(plugin.gunManager.createGun(gun, event.getHand() == EquipmentSlot.OFF_HAND));
                            }
                        } else {
                            if (plugin.gunManager.handleZoomIn(game, p, gun)) {
                                p.getInventory().setItemInMainHand(plugin.gunManager.createGun(gun, event.getHand() == EquipmentSlot.OFF_HAND));
                            }
                        }
                    } else if (bi instanceof Grenade && act3) {
                        if (plugin.grenadeManager.throwGrenade(p, (Grenade) bi)) {
                            if (event.getHand() == EquipmentSlot.HAND) {
                                ItemStack i = p.getInventory().getItemInMainHand();
                                i.setAmount(i.getAmount() - 1);
                                p.getInventory().setItemInMainHand(i);
                            } else {
                                ItemStack i = p.getInventory().getItemInOffHand();
                                i.setAmount(i.getAmount() - 1);
                                p.getInventory().setItemInOffHand(i);
                            }
                        }
                    }
                }
                event.setCancelled(true);
                event.setUseInteractedBlock(Event.Result.DENY);
                return;
            }
            plugin.guiManager.callClickEvent(p, p.getInventory().getHeldItemSlot(), false, event, "INTERACT_" + event.getAction().name());
        }
    }

    @EventHandler
    public void useWeapon(WeaponUseEvent e) {
        LocalGame g1 = plugin.arenaManager.getGame(e.getReport().getDamager());
        if (g1 != null && g1.getPhase() == GamePhase.PLAYING) {
            GamePlayer gp1 = g1.getPlayer(e.getReport().getDamager());
            if (gp1 == null || gp1.isSpectator()) {
                e.setCancelled(true);
                return;
            }
            if (e.getReport().getEntity() instanceof Player) {
                Player p2 = (Player) e.getReport().getEntity();
                LocalGame g2 = plugin.arenaManager.getGame(p2);
                if (g2 == null || !g2.equals(g1)) {
                    e.setCancelled(true);
                    return;
                }
                GamePlayer gp2 = g1.getPlayer(p2);
                if (gp2 == null || gp2.isSpectator()) {
                    e.setCancelled(true);
                    return;
                }
                GamePlayerWeaponUseEvent event = new GamePlayerWeaponUseEvent(g1, e.getReport(), gp1, gp2);
                Bukkit.getPluginManager().callEvent(event);
                e.setCancelled(event.isCancelled());
            } else {
                GamePlayerWeaponUseEvent event = new GamePlayerWeaponUseEvent(g1, e.getReport(), gp1, null);
                Bukkit.getPluginManager().callEvent(event);
                e.setCancelled(event.isCancelled());
            }
            if (!e.isCancelled()) {
                gp1.getDataContainer().put("lastWeaponUsed", e.getWeapon());
                gp1.getDataContainer().put("lastDamageReport", e.getReport());
                Objects.requireNonNull(g1.getMode().getController()).onUseWeapon(e, g1);
            }
        } else {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void entityDamageEvent(EntityDamageEvent e) {
        LocalGame g = null;
        Player p2 = null;
        GamePlayer gp1 = null;
        GamePlayer gp2 = null;
        DamageReport report = null;
        GamePlayerDamageEvent.BattleType battleType = null;

        if (e instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent ed = (EntityDamageByEntityEvent) e;
            if (ed.getDamager() instanceof Player && ed.getEntity() instanceof LivingEntity) {
                Player attacker = (Player) ed.getDamager();
                g = plugin.arenaManager.getGame(attacker);
                if (g == null) return;
                if (g.getPhase() != GamePhase.PLAYING) {
                    e.setCancelled(true);
                    return;
                }
                gp1 = g.getPlayer(attacker);
                if (gp1 == null || gp1.isSpectator()) {
                    e.setCancelled(true);
                    return;
                }
                Weapon weaponUsed = (Weapon) gp1.getDataContainer().remove("lastWeaponUsed");
                if (weaponUsed != null) {
                    report = (PlayerAttackReport) gp1.getDataContainer().remove("lastDamageReport");
                }

                if (ed.getEntity() instanceof Player) {
                    battleType = GamePlayerDamageEvent.BattleType.PLAYER_ATTACK_PLAYER;
                    p2 = (Player) ed.getEntity();
                    LocalGame g2 = plugin.arenaManager.getGame(p2);
                    if (g2 == null || !g2.equals(g)) {
                        e.setCancelled(true);
                        return;
                    }
                    gp2 = g2.getPlayer(p2);
                    if (gp2 == null || gp2.isSpectator()) {
                        e.setCancelled(true);
                        return;
                    }
                } else battleType = GamePlayerDamageEvent.BattleType.PLAYER_ATTACK_ENTITY;

                if (report == null) {
                    report = new PlayerAttackReport((LivingEntity) e.getEntity(), e.getFinalDamage(), attacker, weaponUsed);
                }
            } else if (ed.getDamager() instanceof LivingEntity && ed.getEntity() instanceof Player) {
                LivingEntity le = (LivingEntity) ed.getDamager();
                p2 = (Player) e.getEntity();
                g = plugin.arenaManager.getGame(p2);
                if (g == null) return;
                if (g.getPhase() != GamePhase.PLAYING) {
                    e.setCancelled(true);
                    return;
                }
                gp2 = g.getPlayer(p2);
                if (gp2 == null || gp2.isSpectator()) {
                    e.setCancelled(true);
                    return;
                }
                report = new PlayerAttackedReport(p2, e.getFinalDamage(), le, null);
                battleType = GamePlayerDamageEvent.BattleType.ENTITY_ATTACK_PLAYER;
            }
        } else if (e.getEntity() instanceof Player) {
            p2 = (Player) e.getEntity();
            g = plugin.arenaManager.getGame(p2);
            if (g == null) return;
            if (g.getPhase() != GamePhase.PLAYING) {
                e.setCancelled(true);
                return;
            }
            gp2 = g.getPlayer(p2);
            if (gp2 == null || gp2.isSpectator()) {
                e.setCancelled(true);
                return;
            }
            report = new PlayerDamagedReport(p2, e.getFinalDamage());
            battleType = GamePlayerDamageEvent.BattleType.PLAYER_DAMAGED;
        }
        if (report == null) return;
        GamePlayerDamageEvent event = new GamePlayerDamageEvent(g, report, gp1, gp2, battleType);
        Bukkit.getPluginManager().callEvent(event);
        e.setCancelled(event.isCancelled());
        if (!event.isCancelled()) {
            if(p2 != null) {
                g.getDamageReports().put(p2, report);
            }
            if (e.getEntity() instanceof LivingEntity &&
                    plugin.generalConf.isBloodEffectEnabled() && NMSVersion.current().compare(NMSVersion.v1_13_R1) >= 0) {
                double ratio = plugin.generalConf.getBloodEffectParticleRatio();
                int amount = (int) Math.ceil(ratio * Math.min(report.getDamage(), ((LivingEntity) e.getEntity()).getHealth()));
                e.getEntity().getWorld().spawnParticle(Particle.BLOCK_CRACK, e.getEntity().getLocation(), amount, Bukkit.createBlockData(Material.REDSTONE_BLOCK));
            }
        }
    }

    private void updateSecondaryGunSkin(Player player, GunModel newModel) {
        if (newModel == null) {
            player.getInventory().setItemInOffHand(null);
        } else {
            PreparedItem item = plugin.itemManager.make(newModel);
            if (item != null) {
                player.getInventory().setItemInOffHand(newModel.getSecondarySkin().transform(item).build());
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void held(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack oldItemStack = player.getInventory().getItem(event.getPreviousSlot());
        ItemStack newItemStack = player.getInventory().getItem(event.getNewSlot());
        BattleItem newItem = plugin.itemManager.read(newItemStack);
        BattleItem oldItem = plugin.itemManager.read(oldItemStack);
        if (oldItem == null) {
            // old == null & new != null
            if (newItem != null) {
                if (newItem instanceof Gun) {
                    GunModel gm = ((Gun) newItem).getModel();
                    if (gm != null) {
                        SpeedUtil.setModifier(player, SpeedFactor.ITEM, -gm.getWeight());
                        updateSecondaryGunSkin(player, gm);
                    }
                }
            }
        } else {
            // old != null & new != null
            if (newItem != null) {
                if (newItem instanceof Gun) {
                    GunModel gm = ((Gun) newItem).getModel();
                    if (gm != null) {
                        if (oldItem instanceof Gun) {
                            plugin.gunManager.handleZoomOut(player);
                        }
                        SpeedUtil.setModifier(player, SpeedFactor.ITEM, -gm.getWeight());
                        updateSecondaryGunSkin(player, gm);
                    }
                } else if (oldItem instanceof Gun) {
                    plugin.gunManager.handleZoomOut(player);
                    updateSecondaryGunSkin(player, null);
                    SpeedUtil.setModifier(player, SpeedFactor.ITEM, 0);
                }
            }
            // old != null & new == null
            else {
                if (oldItem instanceof Gun) {
                    plugin.gunManager.handleZoomOut(player);
                    updateSecondaryGunSkin(player, null);
                    SpeedUtil.setModifier(player, SpeedFactor.ITEM, 0);
                }
            }
        }
    }

    @EventHandler
    public void death(PlayerDeathEvent e) {
        if (plugin.generalConf.shouldAntiDeathDrops()) {
            e.getDrops().clear();
            e.setDroppedExp(0);
            e.setKeepInventory(true);
            e.setKeepLevel(true);
        } else {
            SpeedUtil.setModifier(e.getEntity(), SpeedFactor.ITEM, 0);
        }
        LocalGame game = plugin.arenaManager.getGame(e.getEntity());
        if (game != null) {
            GamePlayer gamePlayer = Objects.requireNonNull(game.getPlayer(e.getEntity()));
            StatisticMap st = gamePlayer.getStats();
            st.of(DeathStat.class).increase(e.getEntity());

            Collection<DamageReport> reports = game.getDamageReports().removeAll(e.getEntity());

            Map<Player, GamePlayerDeathEvent.Contribution> damagerMap = new HashMap<>();

            double totalPlayerDamage = 0;
            double totalNatureDamage = 0;

            for (DamageReport report : reports) {
                if (report instanceof PlayerAttackReport) {
                    PlayerAttackReport par = (PlayerAttackReport) report;
                    GamePlayerDeathEvent.Contribution c = damagerMap.get(par.getDamager());
                    if (c == null) {
                        damagerMap.put(par.getDamager(), c = new GamePlayerDeathEvent.Contribution());
                    }
                    if (report.isHeadshotDamage()) {
                        c.setHeadshooter(true);
                    }
                    c.setTotalDamage(c.getTotalDamage() + par.getDamage());
                    c.getDamageReports().add(par);
                    totalPlayerDamage += par.getDamage();
                } else {
                    totalNatureDamage = report.getDamage();
                }
            }

            double avgDamage = (totalPlayerDamage + totalNatureDamage) / reports.size();
            double mostPlayerDamage = 0;
            Player mostDamager = null;

            for (Map.Entry<Player, GamePlayerDeathEvent.Contribution> ent : damagerMap.entrySet()) {
                GamePlayerDeathEvent.Contribution c = ent.getValue();
                c.setAvgDamage(c.getTotalDamage() / c.getDamageReports().size());
                if (c.getAvgDamage() >= avgDamage) {
                    c.setKiller(true); // (*)
                } else {
                    c.setAssistant(true);
                }
                // dont move this to (*) since it may cause bugs if nature damage is high
                if (c.getAvgDamage() > mostPlayerDamage) {
                    mostDamager = ent.getKey();
                    mostPlayerDamage = c.getAvgDamage();
                }
                c.readOnly();
            }

            damagerMap = ImmutableMap.copyOf(damagerMap);
            reports = ImmutableList.copyOf(reports);

            GamePlayerDeathEvent event = new GamePlayerDeathEvent(game, e.getEntity(), reports, damagerMap, mostDamager, mostPlayerDamage, totalPlayerDamage, totalNatureDamage, avgDamage);
            Bukkit.getPluginManager().callEvent(event);

            totalNatureDamage = event.getTotalNatureDamage();
            totalPlayerDamage = event.getTotalPlayerDamage();

            if(game.getArena().isDeathMessageDisabled()) {
                e.setDeathMessage(null);
            } else {
                if (totalPlayerDamage == 0) {
                    InfoReplacer ir = new InfoHolder("")
                            .inform("player", e.getEntity().getName())
                            .compile();
                    e.setDeathMessage(ir.replace(ChatUtil.formatColorCodes(plugin.getLocalizedMessage("game.death_message.by_nature"))));
                } else {
                    boolean over = damagerMap.keySet().size() > 3;
                    String attackerNames = damagerMap.keySet()
                            .stream()
                            .limit(3)
                            .map(Player::getName)
                            .collect(Collectors.joining(", "));
                    double playerPtg = totalPlayerDamage * 100 / (totalPlayerDamage + totalNatureDamage);
                    double naturePtg = 100 - playerPtg;
                    InfoReplacer ir = new InfoHolder("")
                            .inform("attackers", attackerNames)
                            .inform("nature_ptg", naturePtg)
                            .inform("player_ptg", playerPtg)
                            .inform("player", e.getEntity().getName())
                            .compile();
                    String x;
                    if (totalNatureDamage == 0) {
                        if (over) x = "game.death_message.by_players.over";
                        else x = "game.death_message.by_players.enough";
                    } else {
                        if (over) x = "game.death_message.with_players.over";
                        else x = "game.death_message.with_players.enough";
                    }
                    e.setDeathMessage(ChatUtil.formatColorCodes(ir.replace(Objects.requireNonNull(plugin.getLocalizedMessage(x)))));
                }
            }

            String hst = plugin.getLocalizedMessage("medal.headshot_title");
            String hsst = plugin.getLocalizedMessage("medal.headshot_subtitle");
            String ast = plugin.getLocalizedMessage("medal.assist_title");
            String asst = plugin.getLocalizedMessage("medal.assist_subtitle");
            String fkt = plugin.getLocalizedMessage("medal.first_kill_title");
            String fkst = plugin.getLocalizedMessage("medal.first_kill_subtitle");

            for (Map.Entry<Player, GamePlayerDeathEvent.Contribution> ent : damagerMap.entrySet()) {
                Player player = ent.getKey();
                GamePlayer gp = game.getPlayer(player);
                if (gp == null) continue;
                StatisticMap stats = gp.getStats();
                if (ent.getValue().isHeadshooter()) {
                    stats.of(HeadshotStat.class).increase(player);
                    plugin.queueTitleTask.put(player, new QueueTitle(PlaceholderUtil.formatPAPI(player, hst), PlaceholderUtil.formatPAPI(player, hsst)));
                }
                if (ent.getValue().isAssistant()) {
                    stats.of(AssistStat.class).increase(player);
                    plugin.queueTitleTask.put(player, new QueueTitle(PlaceholderUtil.formatPAPI(player, ast), PlaceholderUtil.formatPAPI(player, asst)));
                } else if (ent.getValue().isKiller()) {
                    stats.of(KillStat.class).increase(player); // (*)
                }
                // most damager not means he is a killer (what if nature damage is high?) moved from (*)
                if (player.equals(mostDamager) && !game.hasFirstKill() && !gp.hasFirstKill()) {
                    // dont increase first kill stats here! we do it at the end
                    gp.setHasFirstKill(true);
                    game.setHasFirstKill(true);
                    plugin.queueTitleTask.put(player, new QueueTitle(PlaceholderUtil.formatPAPI(player, fkt), PlaceholderUtil.formatPAPI(player, fkst)));
                }
            }

            game.getMode().getController(c -> {
                c.onDeath(e, game);
                ((GameControllerImpl) c).cancelReloadGun(e.getEntity());
            });

            // note: render gui on death should be put after the call to game controller
            if (game.getArena().isRenderGuiOnDeath()) {
                gamePlayer.getIgBackpack().clear();
                e.getEntity().getInventory().clear();
                plugin.guiManager.updateView(e.getEntity(), plugin.guiManager.getWindow(e.getEntity()).getBottomView());
                SpeedUtil.setModifier(e.getEntity(), SpeedFactor.ITEM, 0);
            }

            plugin.gunManager.handleZoomOut(e.getEntity());
        }
    }

    @EventHandler
    public void deathEntity(EntityDeathEvent e) {
        if(e.getEntity() instanceof Player) return; // players are handled above
        if (plugin.generalConf.shouldAntiDeathDrops()) {
            e.getDrops().clear();
            e.setDroppedExp(0);
        }
        Player killer = e.getEntity().getKiller();
        if (killer == null) return;
        LocalGame game = plugin.arenaManager.getGame(killer);
        if (game != null && game.getArena().isKillStatsByMonsters()) {
            Objects.requireNonNull(game.getPlayer(killer)).getStats().of(KillStat.class).increase(killer);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void chat(AsyncPlayerChatEvent event) {
        if (plugin.chatManager.chat(event.getPlayer(), event.getMessage()))
            event.setCancelled(true);
    }

    @EventHandler
    public void clickInv(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player && event.getClickedInventory() != null) {
            Player p = (Player) event.getWhoClicked();
            Window w = plugin.guiManager.callClickEvent(p, event.getSlot(), !(event.getClickedInventory() instanceof PlayerInventory), event, "INVENTORY_" + event.getClick().name());
            LocalGame game = plugin.arenaManager.getGame(p);
            if (game != null) {
                game.getMode().getController(c -> c.onClickInventory(event, game, p, w));
            }
        }
    }

    @EventHandler
    public void closeInv(InventoryCloseEvent event) {
        Window w = plugin.guiManager.getWindow(event.getPlayer());
        Object v = w.getDataContainer().remove("switchView");
        if (v instanceof Boolean && (Boolean) v) return;
        w.setTopView(null);
    }

    @EventHandler
    public void respawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        LocalGame game = plugin.arenaManager.getGame(player);
        if (game != null) {
            game.getMode().getController(c -> {
                c.onRespawn(event, game);
            });
        } else {
            event.setRespawnLocation(plugin.getServerData().getSpawnPoint());
        }
    }
}
