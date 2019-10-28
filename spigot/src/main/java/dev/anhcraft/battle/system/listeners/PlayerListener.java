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

import dev.anhcraft.battle.BattleComponent;
import dev.anhcraft.battle.BattlePlugin;
import dev.anhcraft.battle.api.events.GamePlayerDamageEvent;
import dev.anhcraft.battle.api.events.ItemChooseEvent;
import dev.anhcraft.battle.api.events.PlayerDamageEvent;
import dev.anhcraft.battle.api.game.GamePhase;
import dev.anhcraft.battle.api.game.GamePlayer;
import dev.anhcraft.battle.api.game.LocalGame;
import dev.anhcraft.battle.api.inventory.items.BattleItem;
import dev.anhcraft.battle.api.inventory.items.Grenade;
import dev.anhcraft.battle.api.inventory.items.Gun;
import dev.anhcraft.battle.api.inventory.items.GunModel;
import dev.anhcraft.battle.api.misc.DamageReport;
import dev.anhcraft.battle.api.storage.data.PlayerData;
import dev.anhcraft.battle.system.QueueTitle;
import dev.anhcraft.battle.system.controllers.ModeController;
import dev.anhcraft.battle.system.handlers.GrenadeHandler;
import dev.anhcraft.battle.system.handlers.GunHandler;
import dev.anhcraft.battle.utils.PlaceholderUtil;
import dev.anhcraft.craftkit.abif.PreparedItem;
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
            plugin.taskHelper.newAsyncTask(() -> {
                PlayerData playerData = plugin.dataManager.loadPlayerData(player);
                // back to main thread
                plugin.taskHelper.newTask(() -> {
                    plugin.listKits(kit -> {
                        if(kit.isFirstJoin() && !playerData.getReceivedFirstJoinKits().contains(kit.getId())){
                            kit.givePlayer(player, playerData);
                            playerData.getReceivedFirstJoinKits().add(kit.getId());
                        }
                    });
                });
            });
        }, 60);
    }

    @EventHandler
    public void quit(PlayerQuitEvent event){
        plugin.guiManager.destroyPlayerGui(event.getPlayer());
        plugin.gameManager.quit(event.getPlayer());
        plugin.getHandler(GunHandler.class).handleZoomOut(event.getPlayer());
        plugin.taskHelper.newAsyncTask(() -> plugin.dataManager.unloadPlayerData(event.getPlayer()));
    }

    @EventHandler
    public void swap(PlayerSwapHandItemsEvent event) {
        plugin.guiManager.callEvent(event.getPlayer(), event.getPlayer().getInventory().getHeldItemSlot(), false, event);
        LocalGame game = plugin.gameManager.getGame(event.getPlayer());
        if(game != null){
            game.getMode().getController(c -> c.onSwapItem(event, game));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void drop(PlayerDropItemEvent event) {
        if(plugin.guiManager.validateButton(event.getPlayer(), event.getPlayer().getInventory().getHeldItemSlot(), false)) event.setCancelled(true);
        LocalGame game = plugin.gameManager.getGame(event.getPlayer());
        if(game != null){
            game.getMode().getController(c -> c.onDropItem(event, game));
        }
    }

    @EventHandler
    public void chooseItem(ItemChooseEvent event) {
        LocalGame game = plugin.gameManager.getGame(event.getPlayer());
        if(game != null){
            game.getMode().getController(c -> c.onChooseItem(event, game));
        }
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
                    LocalGame game = plugin.gameManager.getGame(p);
                    if(game != null && game.getPhase() == GamePhase.PLAYING){
                        Gun gun = (Gun) item;
                        if(plugin.getHandler(GunHandler.class).shoot(game, p, gun))
                            p.getInventory().setItemInMainHand(plugin.getHandler(GunHandler.class).createGun(gun, event.getHand() == EquipmentSlot.OFF_HAND));
                        event.setCancelled(true);
                        event.setUseInteractedBlock(Event.Result.DENY);
                    }
                }
                else if (item instanceof Grenade) {
                    if(plugin.getHandler(GrenadeHandler.class).throwGrenade(p, (Grenade) item)){
                        if(event.getHand() == EquipmentSlot.HAND) {
                            ItemStack i = p.getInventory().getItemInMainHand();
                            i.setAmount(i.getAmount() - 1);
                            p.getInventory().setItemInMainHand(i);
                        } else {
                            ItemStack i = p.getInventory().getItemInOffHand();
                            i.setAmount(i.getAmount() - 1);
                            p.getInventory().setItemInOffHand(i);
                        }
                        event.setCancelled(true);
                        event.setUseInteractedBlock(Event.Result.DENY);
                    }
                }
                return;
            }
        }
        else if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            BattleItem item = plugin.itemManager.read(event.getItem());
            if(item != null) {
                if (item instanceof Gun) {
                    LocalGame game = plugin.gameManager.getGame(p);
                    if(game != null && game.getPhase() == GamePhase.PLAYING){
                        Gun gun = (Gun) item;
                        if(plugin.getHandler(GunHandler.class).handleZoomIn(game, p, gun))
                            p.getInventory().setItemInMainHand(plugin.getHandler(GunHandler.class).createGun(gun, event.getHand() == EquipmentSlot.OFF_HAND));
                        event.setCancelled(true);
                        event.setUseInteractedBlock(Event.Result.DENY);
                    }
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
            LocalGame g1 = plugin.gameManager.getGame(e.getDamager());
            LocalGame g2 = plugin.gameManager.getGame(ent);
            if(g1 != null && g2 != null){
                if(g1.equals(g2) && g1.getPhase() == GamePhase.PLAYING) {
                    GamePlayer gp1 = g1.getPlayer(e.getDamager());
                    GamePlayer gp2 = g1.getPlayer(ent);
                    if(gp1 == null || gp2 == null) return;
                    // spectators can't attack or receive damage
                    if(gp1.isSpectator() || gp2.isSpectator()){
                        e.setCancelled(true);
                        return;
                    }
                    GamePlayerDamageEvent event = new GamePlayerDamageEvent(g1, e.getReport(), ent, e.getWeapon(), gp1, gp2);
                    Bukkit.getPluginManager().callEvent(event);
                    e.setCancelled(event.isCancelled());

                    if(!event.isCancelled()) g1.getDamageReports().get(ent).add(event.getReport());
                } else e.setCancelled(true);
            }
        }
    }

    private void updateSecondaryGunSkin(Player player, GunModel newModel){
        if(newModel == null) {
            player.getInventory().setItemInOffHand(null);
        } else {
            PreparedItem item = plugin.itemManager.make(newModel);
            if(item != null) {
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
        if(oldItem == null){
            // old == null & new != null
            if(newItem != null){
                if(newItem instanceof Gun) {
                    GunModel gm = ((Gun) newItem).getModel();
                    if(gm != null) {
                        plugin.getHandler(GunHandler.class).reduceSpeed(player, gm);
                        updateSecondaryGunSkin(player, gm);
                    }
                }
            }
        } else {
            // old != null & new != null
            if(newItem != null){
                if(newItem instanceof Gun) {
                    GunModel gm = ((Gun) newItem).getModel();
                    if(gm != null) {
                        if(oldItem instanceof Gun){
                            if(!plugin.getHandler(GunHandler.class).handleZoomOut(player, gm)){
                                player.setWalkSpeed(plugin.getDefaultWalkingSpeed());
                                player.setFlySpeed(plugin.getDefaultFlyingSpeed());
                            }
                        }
                        plugin.getHandler(GunHandler.class).reduceSpeed(player, gm);
                        updateSecondaryGunSkin(player, gm);
                    }
                } else if(oldItem instanceof Gun){
                    if(!plugin.getHandler(GunHandler.class).handleZoomOut(player, null)){
                        player.setWalkSpeed(plugin.getDefaultWalkingSpeed());
                        player.setFlySpeed(plugin.getDefaultFlyingSpeed());
                    }
                    updateSecondaryGunSkin(player, null);
                }
            }
            // old != null & new == null
            else {
                if(oldItem instanceof Gun){
                    updateSecondaryGunSkin(player, null);
                    if(!plugin.getHandler(GunHandler.class).handleZoomOut(player, null)){
                        player.setWalkSpeed(plugin.getDefaultWalkingSpeed());
                        player.setFlySpeed(plugin.getDefaultFlyingSpeed());
                    }
                }
            }
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
        LocalGame game = plugin.gameManager.getGame(e.getEntity());
        if(game != null){
            e.setDeathMessage(null);
            Objects.requireNonNull(game.getPlayer(e.getEntity())).getDeathCounter().incrementAndGet();

            Collection<DamageReport> reports = game.getDamageReports().removeAll(e.getEntity());
            DoubleSummaryStatistics stats = reports
                    .stream()
                    .mapToDouble(DamageReport::getDamage)
                    .summaryStatistics();
            double avgDamage = stats.getAverage();

            String hst = plugin.getLocaleConf().getString("medal.headshot_title");
            String hsst = plugin.getLocaleConf().getString("medal.headshot_subtitle");
            String ast = plugin.getLocaleConf().getString("medal.assist_title");
            String asst = plugin.getLocaleConf().getString("medal.assist_subtitle");
            String fkt = plugin.getLocaleConf().getString("medal.first_kill_title");
            String fkst = plugin.getLocaleConf().getString("medal.first_kill_subtitle");
            Set<Player> headshooters = new HashSet<>();
            Set<Player> killers = new HashSet<>();
            Set<Player> assistants = new HashSet<>();
            double mostDamage = 0;
            Player mostDamager = null;

            for(DamageReport report : reports){
                if(report.getDamage() >= avgDamage) {
                    killers.add(report.getDamager());
                    if(report.getDamage() > mostDamage){
                        mostDamage = report.getDamage();
                        mostDamager = report.getDamager();
                    }
                    if(report.isHeadshotDamage()) headshooters.add(report.getDamager());
                } else assistants.add(report.getDamager());
            }

            for(Player player : headshooters){
                GamePlayer gp = game.getPlayer(player);
                if(gp == null) return; // ignore attackers who quit the game
                gp.getHeadshotCounter().incrementAndGet();
                plugin.queueTitleTask.put(player, new QueueTitle(PlaceholderUtil.formatPAPI(player, hst), PlaceholderUtil.formatPAPI(player, hsst)));
            }

            for(Player player : assistants){
                GamePlayer gp = game.getPlayer(player);
                if(gp == null) return;
                gp.getAssistCounter().incrementAndGet();
                plugin.queueTitleTask.put(player, new QueueTitle(PlaceholderUtil.formatPAPI(player, ast), PlaceholderUtil.formatPAPI(player, asst)));
            }

            for(Player player : killers){
                GamePlayer gp = game.getPlayer(player);
                if(gp == null) return;
                gp.getKillCounter().incrementAndGet();
                if(player.equals(mostDamager)){
                    gp.setHasFirstKill(true);
                    plugin.queueTitleTask.put(player, new QueueTitle(PlaceholderUtil.formatPAPI(player, fkt), PlaceholderUtil.formatPAPI(player, fkst)));
                }
            }

            if(game.getArena().isRenderGuiOnDeath()){
                plugin.guiManager.renderBottomInv(e.getEntity(), plugin.guiManager.getPlayerGui(e.getEntity()));
            }

            game.getMode().getController(c -> {
                c.onDeath(e, game);
                ((ModeController) c).cancelReloadGun(e.getEntity());
            });

            plugin.getHandler(GunHandler.class).handleZoomOut(e.getEntity());
        }
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
            LocalGame game = plugin.gameManager.getGame(p);
            if(game != null) game.getMode().getController(c -> c.onClickInventory(event, game, p));
        }
    }

    @EventHandler
    public void respawn(PlayerRespawnEvent event){
        Player player = event.getPlayer();
        LocalGame game = plugin.gameManager.getGame(player);
        if(game != null) game.getMode().getController(c -> c.onRespawn(event, game));
        else event.setRespawnLocation(plugin.getServerData().getSpawnPoint());
    }
}
