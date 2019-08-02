package dev.anhcraft.abm.system.listeners;

import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.events.GamePlayerDamageEvent;
import dev.anhcraft.abm.api.events.PlayerDamageEvent;
import dev.anhcraft.abm.api.ext.BattleComponent;
import dev.anhcraft.abm.api.ext.BattleItem;
import dev.anhcraft.abm.api.objects.DamageReport;
import dev.anhcraft.abm.api.objects.Game;
import dev.anhcraft.abm.api.objects.GamePlayer;
import dev.anhcraft.abm.api.objects.Gun;
import dev.anhcraft.abm.gui.core.BattleSlot;
import dev.anhcraft.abm.gui.core.PlayerGui;
import dev.anhcraft.abm.system.QueueTitle;
import dev.anhcraft.abm.system.handlers.GunHandler;
import dev.anhcraft.abm.system.handlers.PlayerInventoryHandler;
import dev.anhcraft.abm.utils.PlayerUtil;
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
        player.teleport(plugin.getServerData().getSpawnPoint());
        plugin.taskManager.newAsyncTask(() -> plugin.dataManager.loadPlayerData(player));
        plugin.taskManager.newTask(() -> {
            plugin.resetScoreboard(player);
            plugin.guiManager.setPlayerInventory(player, "main_player_inv");
        });
    }

    @EventHandler
    public void quit(PlayerQuitEvent event){
        plugin.guiManager.destroyGui(event.getPlayer());
        plugin.gameManager.quit(event.getPlayer());
        plugin.taskManager.newAsyncTask(() -> plugin.dataManager.unloadPlayerData(event.getPlayer()));
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
        plugin.getHandler(PlayerInventoryHandler.class).handleSlot(event.getPlayer(), event, event.getPlayer().getInventory().getHeldItemSlot());
    }

    @EventHandler
    public void drop(PlayerDropItemEvent event) {
        plugin.getHandler(PlayerInventoryHandler.class).handleSlot(event.getPlayer(), event, event.getPlayer().getInventory().getHeldItemSlot());
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            BattleItem item = plugin.itemManager.read(event.getItem());
            if(item != null && event.getHand() != EquipmentSlot.OFF_HAND) {
                if (item instanceof Gun) {
                    plugin.gameManager.getGame(p).ifPresent(game -> {
                        Gun gun = (Gun) item;
                        plugin.getHandler(GunHandler.class).shoot(game, p, gun);
                        p.getInventory().setItemInMainHand(plugin.getHandler(GunHandler.class).createGun(gun, event.getHand() == EquipmentSlot.OFF_HAND));
                        event.setCancelled(true);
                        event.setUseInteractedBlock(Event.Result.DENY);
                    });
                }
                return;
            }
        }
        plugin.getHandler(PlayerInventoryHandler.class).handleSlot(p, event, p.getInventory().getHeldItemSlot());
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
                ((Gun) newItem).getModel().ifPresent(m ->
                        PlayerUtil.reduceSpeed(player, m.getWeight()));
                secondarySkin(player, newItem, oldItem);
            } else {
                // OTHER ITEM TYPE PUT HERE
                // .........................
                secondarySkin(player, null, oldItem);
            }
        } else if(oldItem != null) {
            if(oldItem instanceof Gun)
                ((Gun) oldItem).getModel().ifPresent(m ->
                    PlayerUtil.increaseSpeed(player, m.getWeight()));
            secondarySkin(player, null, oldItem);
        }
    }

    @EventHandler
    public void death(PlayerDeathEvent e) {
        e.setKeepInventory(true);
        e.setKeepLevel(true);
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
                plugin.queueTitleTask.put(player, new QueueTitle(PlaceholderUtils.formatPlaceholders(player, hst), PlaceholderUtils.formatPlaceholders(player, hsst)));
            });
            assistants.forEach(player -> {
                GamePlayer gp = game.getPlayer(player);
                if(gp == null) return;
                gp.getAssistCounter().incrementAndGet();
                plugin.queueTitleTask.put(player, new QueueTitle(PlaceholderUtils.formatPlaceholders(player, ast), PlaceholderUtils.formatPlaceholders(player, asst)));
            });
            killers.forEach(player -> Objects.requireNonNull(game.getPlayer(player)).getKillCounter().incrementAndGet());
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void chat(AsyncPlayerChatEvent event) {
        if(plugin.chatProvider.chat(event.getPlayer(), event.getMessage()))
            event.setCancelled(true);
    }

    @EventHandler
    public void clickInv(InventoryClickEvent event) {
        if(event.getWhoClicked() instanceof Player && event.getClickedInventory() != null) {
            Player p = (Player) event.getWhoClicked();
            PlayerGui apg = plugin.guiManager.getGui(p);
            if(event.getRawSlot() == event.getSlot()){
                if(event.getClickedInventory() instanceof PlayerInventory)
                    plugin.getHandler(PlayerInventoryHandler.class).handleSlot(p, event, event.getSlot());
                else if(event.getClickedInventory().equals(apg.getInventory()) && apg.getGui() != null) {
                    BattleSlot[] x = apg.getGui().getSlots();
                    if(event.getSlot() < x.length) {
                        BattleSlot s = x[event.getSlot()];
                        event.setCancelled(s == null || plugin.guiManager.callSlotHandler(event, apg, s));
                    }
                }
            } else plugin.getHandler(PlayerInventoryHandler.class).handleSlot(p, event, event.getSlot());
        }
    }

    @EventHandler
    public void respawn(PlayerRespawnEvent event){
        Player player = event.getPlayer();
        Optional<Game> opt = plugin.gameManager.getGame(player);
        if(opt.isPresent()){
            Game g = opt.get();
            Objects.requireNonNull(g.getMode().getController()).onRespawn(event, g);
        } else player.teleport(plugin.getServerData().getSpawnPoint());
    }
}
