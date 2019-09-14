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
package dev.anhcraft.abm.system.listeners;

import dev.anhcraft.abm.BattleComponent;
import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.events.GamePlayerDamageEvent;
import dev.anhcraft.abm.api.events.ItemChooseEvent;
import dev.anhcraft.abm.api.events.PlayerDamageEvent;
import dev.anhcraft.abm.api.game.Game;
import dev.anhcraft.abm.api.game.GamePlayer;
import dev.anhcraft.abm.api.inventory.items.BattleItem;
import dev.anhcraft.abm.api.inventory.items.Gun;
import dev.anhcraft.abm.api.misc.DamageReport;
import dev.anhcraft.abm.system.QueueTitle;
import dev.anhcraft.abm.system.controllers.ModeController;
import dev.anhcraft.abm.system.handlers.GunHandler;
import dev.anhcraft.abm.utils.PlaceholderUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;

public class PlayerListener extends BattleComponent implements Listener {
    public PlayerListener(BattlePlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void join(PlayerJoinEvent event){
        Player player = event.getPlayer();
        handleJoin(player);
    }

    public void handleJoin(Player player) {
        plugin.taskHelper.newDelayedTask(() -> {
            if(!player.isOnline()) return;
            player.teleport(plugin.getServerData().getSpawnPoint());
            player.setWalkSpeed(plugin.getDefaultWalkingSpeed());
            player.setFlySpeed(plugin.getDefaultFlyingSpeed());
            plugin.guiManager.setBottomInv(player, "main_player_inv");
            plugin.resetScoreboard(player);
            plugin.taskHelper.newAsyncTask(() -> plugin.dataManager.loadPlayerData(player));
        }, 60);
    }

    @EventHandler
    public void quit(PlayerQuitEvent event){
        plugin.guiManager.destroyPlayerGui(event.getPlayer());
        plugin.gameManager.quit(event.getPlayer());
        plugin.getHandler(GunHandler.class).handleZoomOut(event.getPlayer());
        plugin.taskHelper.newAsyncTask(() -> plugin.dataManager.unloadPlayerData(event.getPlayer()));
    }

    public void secondarySkin(Player player, BattleItem newItem, BattleItem oldItem){
        if(newItem instanceof Gun)
            player.getInventory().setItemInOffHand(plugin.getHandler(GunHandler.class).createGun(
                    (Gun) newItem, true));
        else if(newItem == null && oldItem instanceof Gun)
            player.getInventory().setItemInOffHand(null);
    }

    @EventHandler
    public void swap(PlayerSwapHandItemsEvent event) {
        plugin.guiManager.callEvent(event.getPlayer(), event.getPlayer().getInventory().getHeldItemSlot(), false, event);
        plugin.gameManager.getGame(event.getPlayer()).ifPresent(game -> {
            game.getMode().getController(c -> c.onSwapHand(event, game));
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void drop(PlayerDropItemEvent event) {
        if(plugin.guiManager.validateButton(event.getPlayer(), event.getPlayer().getInventory().getHeldItemSlot(), false)) event.setCancelled(true);
        plugin.gameManager.getGame(event.getPlayer()).ifPresent(game -> {
            game.getMode().getController(c -> c.onDropItem(event, game));
        });
    }

    @EventHandler
    public void chooseItem(ItemChooseEvent event) {
        plugin.gameManager.getGame(event.getPlayer()).ifPresent(game -> {
            game.getMode().getController(c -> c.onChooseItem(event, game));
        });
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        if(event.getHand() == EquipmentSlot.OFF_HAND)
            return;
        if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            BattleItem item = plugin.itemManager.read(event.getItem());
            if(item != null) {
                if (item instanceof Gun) {
                    plugin.gameManager.getGame(p).ifPresent(game -> {
                        Gun gun = (Gun) item;
                        if(plugin.getHandler(GunHandler.class).shoot(game, p, gun))
                            p.getInventory().setItemInMainHand(plugin.getHandler(GunHandler.class).createGun(gun, event.getHand() == EquipmentSlot.OFF_HAND));
                        event.setCancelled(true);
                        event.setUseInteractedBlock(Event.Result.DENY);
                    });
                }
                return;
            }
        }
        else if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            BattleItem item = plugin.itemManager.read(event.getItem());
            if(item != null) {
                if (item instanceof Gun) {
                    plugin.gameManager.getGame(p).ifPresent(game -> {
                        Gun gun = (Gun) item;
                        if(plugin.getHandler(GunHandler.class).handleZoomIn(game, p, gun))
                            p.getInventory().setItemInMainHand(plugin.getHandler(GunHandler.class).createGun(gun, event.getHand() == EquipmentSlot.OFF_HAND));
                        event.setCancelled(true);
                        event.setUseInteractedBlock(Event.Result.DENY);
                    });
                }
                return;
            }
        }
        plugin.guiManager.callEvent(p, p.getInventory().getHeldItemSlot(), false, event);
    }

    @EventHandler
    public void damage(PlayerDamageEvent e) {
        if(e.getEntity() instanceof Player) {
            Player ent = (Player) e.getEntity();
            Optional<Game> g1 = plugin.gameManager.getGame(e.getDamager());
            Optional<Game> g2 = plugin.gameManager.getGame(ent);
            if(g1.isPresent()){
                Game game1 = g1.get();
                if(g2.isPresent()) {
                    if(g2.get().equals(game1)) {
                        GamePlayer gp1 = game1.getPlayer(e.getDamager());
                        GamePlayer gp2 = game1.getPlayer(ent);
                        if(gp1 == null || gp2 == null) return;
                        // spectators can't attack or receive damage
                        if(gp1.isSpectator() || gp2.isSpectator()){
                            e.setCancelled(true);
                            return;
                        }
                        GamePlayerDamageEvent event = new GamePlayerDamageEvent(game1, e.getReport(), ent, e.getWeapon(), gp1, gp2);
                        Bukkit.getPluginManager().callEvent(event);
                        e.setCancelled(event.isCancelled());

                        if(!event.isCancelled()) game1.getDamageReports().get(ent).add(event.getReport());
                    } else
                        // can't attack players in another arena
                        e.setCancelled(true);
                } else
                    // game players can't attack normal players
                    e.setCancelled(true);
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
        if(newItem != null){
            if(newItem instanceof Gun) {
                ((Gun) newItem).getModel().ifPresent(m -> {
                    plugin.getHandler(GunHandler.class).reduceSpeed(player, m);
                });
                secondarySkin(player, newItem, oldItem);
            } else {
                // OTHER ITEM TYPE PUT HERE
                // .........................
                secondarySkin(player, null, oldItem);
            }
        } else if(oldItem != null) {
            if(oldItem instanceof Gun){
                Gun gun = (Gun) oldItem;
                gun.getModel().ifPresent(m -> {
                    if(!plugin.getHandler(GunHandler.class).handleZoomOut(player)){
                        player.setWalkSpeed(plugin.getDefaultWalkingSpeed());
                        player.setFlySpeed(plugin.getDefaultFlyingSpeed());
                    }
                });
            }
            secondarySkin(player, null, oldItem);
        }
    }

    @EventHandler
    public void death(PlayerDeathEvent e) {
        if(plugin.getGeneralConf().getBoolean("misc.anti_death_drops")){
            e.getDrops().clear();
            e.setDroppedExp(0);
            e.setKeepInventory(true);
            e.setKeepLevel(true);
        }
        plugin.gameManager.getGame(e.getEntity()).ifPresent(game -> {
            e.setDeathMessage(null);
            Objects.requireNonNull(game.getPlayer(e.getEntity())).getDeathCounter().incrementAndGet();
            Collection<DamageReport> reports = game.getDamageReports().removeAll(e.getEntity());
            DoubleSummaryStatistics stats = reports
                    .stream()
                    .mapToDouble(DamageReport::getDamage)
                    .summaryStatistics();
            Set<Player> headshooters = new HashSet<>();
            Set<Player> killers = new HashSet<>();
            Set<Player> assistants = new HashSet<>();
            for(DamageReport report : reports){
                if(report.getDamage() >= stats.getAverage()) {
                    killers.add(report.getDamager());
                    if(report.isHeadshotDamage()) headshooters.add(report.getDamager());
                } else assistants.add(report.getDamager());
            }
            String hst = plugin.getLocaleConf().getString("medal.headshot_title");
            String hsst = plugin.getLocaleConf().getString("medal.headshot_subtitle");
            String ast = plugin.getLocaleConf().getString("medal.assist_title");
            String asst = plugin.getLocaleConf().getString("medal.assist_subtitle");
            headshooters.forEach(player -> {
                GamePlayer gp = game.getPlayer(player);
                if(gp == null) return; // ignore attackers who quit the game
                gp.getHeadshotCounter().incrementAndGet();
                plugin.queueTitleTask.put(player, new QueueTitle(PlaceholderUtils.formatPAPI(player, hst), PlaceholderUtils.formatPAPI(player, hsst)));
            });
            assistants.forEach(player -> {
                GamePlayer gp = game.getPlayer(player);
                if(gp == null) return;
                gp.getAssistCounter().incrementAndGet();
                plugin.queueTitleTask.put(player, new QueueTitle(PlaceholderUtils.formatPAPI(player, ast), PlaceholderUtils.formatPAPI(player, asst)));
            });
            killers.forEach(player -> Objects.requireNonNull(game.getPlayer(player)).getKillCounter().incrementAndGet());

            if(game.getArena().isRenderGuiOnDeath()){
                plugin.guiManager.renderBottomInv(e.getEntity(), plugin.guiManager.getPlayerGui(e.getEntity()));
            }

            game.getMode().getController(c -> {
                c.onDeath(e, game);
                ((ModeController) c).cancelReloadGun(e.getEntity());
            });

            plugin.getHandler(GunHandler.class).handleZoomOut(e.getEntity());
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void chat(AsyncPlayerChatEvent event) {
        if(plugin.chatManager.chat(event.getPlayer(), event.getMessage()))
            event.setCancelled(true);
    }

    @EventHandler
    public void clickInv(InventoryClickEvent event) {
        if(event.getWhoClicked() instanceof Player && event.getClickedInventory() != null) {
            Player p = (Player) event.getWhoClicked();
            plugin.guiManager.callEvent(p, event.getSlot(), !(event.getClickedInventory() instanceof PlayerInventory), event);
            plugin.gameManager.getGame(p).ifPresent(game -> {
                game.getMode().getController(c -> c.onClickInventory(event, game, p));
            });
        }
    }

    @EventHandler
    public void respawn(PlayerRespawnEvent event){
        Player player = event.getPlayer();
        Optional<Game> opt = plugin.gameManager.getGame(player);
        if(opt.isPresent()){
            Game g = opt.get();
            g.getMode().getController(c -> c.onRespawn(event, g));
        } else event.setRespawnLocation(plugin.getServerData().getSpawnPoint());
    }
}
