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

import com.google.common.collect.ImmutableMap;
import dev.anhcraft.battle.BattleComponent;
import dev.anhcraft.battle.BattlePlugin;
import dev.anhcraft.battle.api.MouseClick;
import dev.anhcraft.battle.api.arena.game.GamePhase;
import dev.anhcraft.battle.api.arena.game.GamePlayer;
import dev.anhcraft.battle.api.arena.game.LocalGame;
import dev.anhcraft.battle.api.events.ItemChooseEvent;
import dev.anhcraft.battle.api.events.WeaponUseEvent;
import dev.anhcraft.battle.api.events.game.GamePlayerDamageEvent;
import dev.anhcraft.battle.api.events.game.GamePlayerDeathEvent;
import dev.anhcraft.battle.api.events.game.GamePlayerWeaponUseEvent;
import dev.anhcraft.battle.api.gui.NativeGui;
import dev.anhcraft.battle.api.gui.screen.Window;
import dev.anhcraft.battle.api.inventory.item.*;
import dev.anhcraft.battle.api.reports.DamageReport;
import dev.anhcraft.battle.api.reports.PlayerAttackReport;
import dev.anhcraft.battle.api.reports.PlayerAttackedReport;
import dev.anhcraft.battle.api.reports.PlayerDamagedReport;
import dev.anhcraft.battle.api.stats.natives.AssistStat;
import dev.anhcraft.battle.api.stats.natives.DeathStat;
import dev.anhcraft.battle.api.stats.natives.HeadshotStat;
import dev.anhcraft.battle.api.stats.natives.KillStat;
import dev.anhcraft.battle.api.storage.data.PlayerData;
import dev.anhcraft.battle.system.QueueTitle;
import dev.anhcraft.battle.system.controllers.ModeController;
import dev.anhcraft.battle.utils.PlaceholderUtil;
import dev.anhcraft.battle.utils.info.InfoHolder;
import dev.anhcraft.battle.utils.info.InfoReplacer;
import dev.anhcraft.craftkit.abif.PreparedItem;
import dev.anhcraft.craftkit.common.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;
import java.util.stream.Collectors;

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
        player.setWalkSpeed((float) plugin.GENERAL_CONF.getWalkSpeed());
        player.setFlySpeed((float) plugin.GENERAL_CONF.getFlySpeed());
        plugin.taskHelper.newDelayedTask(() -> {
            if(!player.isOnline()) return;
            player.teleport(plugin.getServerData().getSpawnPoint());
            plugin.guiManager.setBottomGui(player, NativeGui.MAIN_PLAYER_INV);
            plugin.taskHelper.newAsyncTask(() -> {
                PlayerData playerData = plugin.dataManager.loadPlayerData(player);
                // back to main thread
                plugin.taskHelper.newTask(() -> {
                    plugin.resetScoreboard(player);
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
        plugin.guiManager.destroyWindow(event.getPlayer());
        plugin.arenaManager.quit(event.getPlayer());
        plugin.gunManager.handleZoomOut(event.getPlayer());
        plugin.taskHelper.newAsyncTask(() -> plugin.dataManager.unloadPlayerData(event.getPlayer()));
    }

    @EventHandler
    public void swap(PlayerSwapHandItemsEvent event) {
        plugin.guiManager.callEvent(event.getPlayer(), event.getPlayer().getInventory().getHeldItemSlot(), false, event);
        LocalGame game = plugin.arenaManager.getGame(event.getPlayer());
        if(game != null){
            game.getMode().getController(c -> c.onSwapItem(event, game));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void drop(PlayerDropItemEvent event) {
        Player p = event.getPlayer();
        LocalGame game = plugin.arenaManager.getGame(p);
        if(game != null){
            game.getMode().getController(c -> c.onDropItem(event, game));
        }
        InventoryType f = p.getOpenInventory().getType();
        if(f == InventoryType.CRAFTING || (f == InventoryType.CREATIVE && p.getGameMode() == GameMode.CREATIVE)){
            plugin.guiManager.callEvent(p, p.getInventory().getHeldItemSlot(), false, event);
        }
    }

    @EventHandler
    public void chooseItem(ItemChooseEvent event) {
        LocalGame game = plugin.arenaManager.getGame(event.getPlayer());
        if(game != null){
            game.getMode().getController(c -> c.onChooseItem(event, game));
        }
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        if(event.getHand() == EquipmentSlot.OFF_HAND) return;
        if(event.getAction() != Action.PHYSICAL) {
            BattleItem item = plugin.itemManager.read(event.getItem());
            if(item != null) {
                LocalGame game = plugin.arenaManager.getGame(p);
                if(game != null && game.getPhase() == GamePhase.PLAYING){
                    boolean left = event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK;
                    boolean right = event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK;
                    boolean act1 = (left && plugin.GENERAL_CONF.getGunShootClick() == MouseClick.LEFT_CLICK)
                            || (right && plugin.GENERAL_CONF.getGunShootClick() == MouseClick.RIGHT_CLICK);
                    boolean act2 = (left && plugin.GENERAL_CONF.getGunZoomClick() == MouseClick.LEFT_CLICK)
                            || (right && plugin.GENERAL_CONF.getGunZoomClick() == MouseClick.RIGHT_CLICK);
                    boolean act3 = (left && plugin.GENERAL_CONF.getGrenadeThrowClick() == MouseClick.LEFT_CLICK)
                            || (right && plugin.GENERAL_CONF.getGrenadeThrowClick() == MouseClick.RIGHT_CLICK);
                    if(item instanceof Gun && (act1 || act2)){
                        Gun gun = (Gun) item;
                        if(act1){
                            if(plugin.gunManager.shoot(game, p, gun)) {
                                p.getInventory().setItemInMainHand(plugin.gunManager.createGun(gun, event.getHand() == EquipmentSlot.OFF_HAND));
                            }
                        } else {
                            if(plugin.gunManager.handleZoomIn(game, p, gun)) {
                                p.getInventory().setItemInMainHand(plugin.gunManager.createGun(gun, event.getHand() == EquipmentSlot.OFF_HAND));
                            }
                        }
                    } else if(item instanceof Grenade && act3){
                        if(plugin.grenadeManager.throwGrenade(p, (Grenade) item)){
                            if(event.getHand() == EquipmentSlot.HAND) {
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
        }
        plugin.guiManager.callEvent(p, p.getInventory().getHeldItemSlot(), false, event);
    }

    @EventHandler
    public void useWeapon(WeaponUseEvent e) {
        LocalGame g1 = plugin.arenaManager.getGame(e.getReport().getDamager());
        if(g1 != null && g1.getPhase() == GamePhase.PLAYING){
            GamePlayer gp1 = g1.getPlayer(e.getReport().getDamager());
            if(gp1 == null || gp1.isSpectator()){
                e.setCancelled(true);
                return;
            }
            if(e.getReport().getEntity() instanceof Player){
                Player p2 = (Player) e.getReport().getEntity();
                LocalGame g2 = plugin.arenaManager.getGame(p2);
                if(g2 == null || !g2.equals(g1)){
                    e.setCancelled(true);
                    return;
                }
                GamePlayer gp2 = g1.getPlayer(p2);
                if(gp2 == null || gp2.isSpectator()){
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
            if(!e.isCancelled()){
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

        if(e instanceof EntityDamageByEntityEvent){
            EntityDamageByEntityEvent ed = (EntityDamageByEntityEvent) e;
            if(ed.getDamager() instanceof Player && ed.getEntity() instanceof LivingEntity){
                Player attacker = (Player) ed.getDamager();
                g = plugin.arenaManager.getGame(attacker);
                if(g == null) return;
                if(g.getPhase() != GamePhase.PLAYING) {
                    e.setCancelled(true);
                    return;
                }
                gp1 = g.getPlayer(attacker);
                if(gp1 == null || gp1.isSpectator()){
                    e.setCancelled(true);
                    return;
                }
                Weapon weaponUsed = (Weapon) gp1.getDataContainer().remove("lastWeaponUsed");
                if(weaponUsed != null){
                    report = (PlayerAttackReport) gp1.getDataContainer().remove("lastDamageReport");
                }

                if(ed.getEntity() instanceof Player){
                    battleType = GamePlayerDamageEvent.BattleType.PLAYER_ATTACK_PLAYER;
                    p2 = (Player) ed.getEntity();
                    LocalGame g2 = plugin.arenaManager.getGame(p2);
                    if(g2 == null || !g2.equals(g)) {
                        e.setCancelled(true);
                        return;
                    }
                    gp2 = g2.getPlayer(p2);
                    if(gp2 == null || gp2.isSpectator()) {
                        e.setCancelled(true);
                        return;
                    }
                } else battleType = GamePlayerDamageEvent.BattleType.PLAYER_ATTACK_ENTITY;

                if(report == null) {
                    report = new PlayerAttackReport((LivingEntity) e.getEntity(), e.getFinalDamage(), attacker, weaponUsed);
                }
            } else if(ed.getDamager() instanceof LivingEntity && ed.getEntity() instanceof Player){
                LivingEntity le = (LivingEntity) ed.getDamager();
                p2 = (Player) e.getEntity();
                g = plugin.arenaManager.getGame(p2);
                if(g == null) return;
                if(g.getPhase() != GamePhase.PLAYING) {
                    e.setCancelled(true);
                    return;
                }
                gp2 = g.getPlayer(p2);
                if(gp2 == null || gp2.isSpectator()){
                    e.setCancelled(true);
                    return;
                }
                report = new PlayerAttackedReport(p2, e.getFinalDamage(), le, null);
                battleType = GamePlayerDamageEvent.BattleType.ENTITY_ATTACK_PLAYER;
            }
        } else if(e.getEntity() instanceof Player) {
            p2 = (Player) e.getEntity();
            g = plugin.arenaManager.getGame(p2);
            if(g == null) return;
            if(g.getPhase() != GamePhase.PLAYING) {
                e.setCancelled(true);
                return;
            }
            gp2 = g.getPlayer(p2);
            if(gp2 == null || gp2.isSpectator()){
                e.setCancelled(true);
                return;
            }
            report = new PlayerDamagedReport(p2, e.getFinalDamage());
            battleType = GamePlayerDamageEvent.BattleType.PLAYER_DAMAGED;
        }
        if(report == null) return;
        GamePlayerDamageEvent event = new GamePlayerDamageEvent(g, report, gp1, gp2, battleType);
        Bukkit.getPluginManager().callEvent(event);
        e.setCancelled(event.isCancelled());
        if(!event.isCancelled() && p2 != null){
            g.getDamageReports().put(p2, report);
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
                        plugin.gunManager.reduceSpeed(player, gm);
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
                            if(!plugin.gunManager.handleZoomOut(player, gm)){
                                player.setWalkSpeed(plugin.getDefaultWalkingSpeed());
                                player.setFlySpeed(plugin.getDefaultFlyingSpeed());
                            }
                        }
                        plugin.gunManager.reduceSpeed(player, gm);
                        updateSecondaryGunSkin(player, gm);
                    }
                } else if(oldItem instanceof Gun){
                    if(!plugin.gunManager.handleZoomOut(player, null)){
                        player.setWalkSpeed((float) plugin.GENERAL_CONF.getWalkSpeed());
                        player.setFlySpeed((float) plugin.GENERAL_CONF.getFlySpeed());
                    }
                    updateSecondaryGunSkin(player, null);
                }
            }
            // old != null & new == null
            else {
                if(oldItem instanceof Gun){
                    updateSecondaryGunSkin(player, null);
                    if(!plugin.gunManager.handleZoomOut(player, null)){
                        player.setWalkSpeed((float) plugin.GENERAL_CONF.getWalkSpeed());
                        player.setFlySpeed((float) plugin.GENERAL_CONF.getFlySpeed());
                    }
                }
            }
        }
    }

    @EventHandler
    public void death(PlayerDeathEvent e) {
        if(plugin.GENERAL_CONF.shouldAntiDeathDrops()){
            e.getDrops().clear();
            e.setDroppedExp(0);
            e.setKeepInventory(true);
            e.setKeepLevel(true);
        }
        LocalGame game = plugin.arenaManager.getGame(e.getEntity());
        if(game != null){
            Objects.requireNonNull(game.getPlayer(e.getEntity())).getStats().of(DeathStat.class).incrementAndGet();

            Collection<DamageReport> reports = game.getDamageReports().removeAll(e.getEntity());
            if(reports.isEmpty()) return;
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

            Map<Player, Double> damagerMap = new HashMap<>();
            Set<Player> headshooters = new HashSet<>();
            Set<Player> killers = new HashSet<>();
            Set<Player> assistants = new HashSet<>();
            double mostPlayerDamage = 0;
            Player mostDamager = null;
            double totalPlayerDamage = 0;
            double totalNatureDamage = 0;

            for(DamageReport report : reports){
                if(report instanceof PlayerAttackReport) {
                    totalPlayerDamage += report.getDamage();
                    PlayerAttackReport par = (PlayerAttackReport) report;
                    damagerMap.compute(par.getDamager(), (s, f) -> f == null ? report.getDamage() : f + report.getDamage());
                    if (report.getDamage() >= avgDamage) {
                        killers.add(par.getDamager());
                        if (report.getDamage() > mostPlayerDamage) {
                            mostPlayerDamage = report.getDamage();
                            mostDamager = par.getDamager();
                        }
                        if (report.isHeadshotDamage()) {
                            headshooters.add(par.getDamager());
                        }
                    } else {
                        assistants.add(par.getDamager());
                    }
                } else {
                    totalNatureDamage += report.getDamage();
                }
            }

            headshooters.removeAll(assistants);
            killers.removeAll(assistants);

            ImmutableMap.Builder<Player, Double> damagerMapBuilder = ImmutableMap.builder();
            damagerMapBuilder.orderEntriesByValue(Comparator.reverseOrder());
            damagerMapBuilder.putAll(damagerMap);
            ImmutableMap<Player, Double> damagers = damagerMapBuilder.build();

            GamePlayerDeathEvent event = new GamePlayerDeathEvent(game, e.getEntity(), damagers, headshooters, killers, assistants, mostDamager, mostPlayerDamage, totalPlayerDamage, totalNatureDamage);
            Bukkit.getPluginManager().callEvent(event);
            totalNatureDamage = event.getTotalNatureDamage();
            totalPlayerDamage = event.getTotalPlayerDamage();

            if(totalPlayerDamage == 0){
                InfoReplacer ir = new InfoHolder("")
                        .inform("player", e.getEntity().getName())
                        .compile();
                e.setDeathMessage(ir.replace(ChatUtil.formatColorCodes(plugin.getLocaleConf().getString("game.death_message.by_nature"))));
            } else {
                boolean over = damagers.keySet().size() > 3;
                String attackerNames = damagers.keySet()
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
                if(totalNatureDamage == 0){
                    if(over) x = "game.death_message.by_players.over";
                    else x = "game.death_message.by_players.enough";
                } else {
                    if(over) x = "game.death_message.with_players.over";
                    else x = "game.death_message.with_players.enough";
                }
                e.setDeathMessage(ChatUtil.formatColorCodes(ir.replace(plugin.getLocaleConf().getString(x))));
            }

            for(Player player : headshooters){
                GamePlayer gp = game.getPlayer(player);
                if(gp == null) continue;
                gp.getStats().of(HeadshotStat.class).incrementAndGet();
                plugin.queueTitleTask.put(player, new QueueTitle(PlaceholderUtil.formatPAPI(player, hst), PlaceholderUtil.formatPAPI(player, hsst)));
            }

            for(Player player : assistants){
                GamePlayer gp = game.getPlayer(player);
                if(gp == null) continue;
                gp.getStats().of(AssistStat.class).incrementAndGet();
                plugin.queueTitleTask.put(player, new QueueTitle(PlaceholderUtil.formatPAPI(player, ast), PlaceholderUtil.formatPAPI(player, asst)));
            }

            for(Player player : killers){
                GamePlayer gp = game.getPlayer(player);
                if(gp == null) continue;
                gp.getStats().of(KillStat.class).incrementAndGet();
                if(player.equals(mostDamager) && !game.hasFirstKill() && !gp.hasFirstKill()) {
                    gp.setHasFirstKill(true);
                    game.setHasFirstKill(true);
                    plugin.queueTitleTask.put(player, new QueueTitle(PlaceholderUtil.formatPAPI(player, fkt), PlaceholderUtil.formatPAPI(player, fkst)));
                }
            }

            if(game.getArena().isRenderGuiOnDeath()){
                e.getEntity().getInventory().clear();
                plugin.guiManager.updateView(e.getEntity(), plugin.guiManager.getWindow(e.getEntity()).getBottomView());
            }

            game.getMode().getController(c -> {
                c.onDeath(e, game);
                ((ModeController) c).cancelReloadGun(e.getEntity());
            });

            plugin.gunManager.handleZoomOut(e.getEntity());
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
            LocalGame game = plugin.arenaManager.getGame(p);
            if(game != null) game.getMode().getController(c -> c.onClickInventory(event, game, p));
        }
    }

    @EventHandler
    public void closeInv(InventoryCloseEvent event) {
        Window w = plugin.guiManager.getWindow(event.getPlayer());
        Object v = w.getDataContainer().remove("switchView");
        if(v instanceof Boolean && (Boolean) v) return;
        w.setTopView(null);
    }

    @EventHandler
    public void respawn(PlayerRespawnEvent event){
        Player player = event.getPlayer();
        LocalGame game = plugin.arenaManager.getGame(player);
        if(game != null) game.getMode().getController(c -> c.onRespawn(event, game));
        else event.setRespawnLocation(plugin.getServerData().getSpawnPoint());
    }
}
