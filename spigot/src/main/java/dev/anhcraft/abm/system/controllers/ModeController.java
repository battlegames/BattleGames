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
package dev.anhcraft.abm.system.controllers;

import dev.anhcraft.abm.BattleComponent;
import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.ApiProvider;
import dev.anhcraft.abm.api.BattleModeController;
import dev.anhcraft.abm.api.game.LocalGame;
import dev.anhcraft.abm.api.game.Mode;
import dev.anhcraft.abm.api.inventory.items.*;
import dev.anhcraft.abm.api.misc.CustomBossBar;
import dev.anhcraft.abm.api.misc.info.InfoHolder;
import dev.anhcraft.abm.system.handlers.GunHandler;
import dev.anhcraft.abm.system.renderers.bossbar.PlayerBossBar;
import dev.anhcraft.abm.utils.CooldownMap;
import dev.anhcraft.abm.utils.PlaceholderUtil;
import dev.anhcraft.jvmkit.utils.MathUtil;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public abstract class ModeController extends BattleComponent implements Listener, BattleModeController {
    private final Map<String, Integer> RUNNING_TASKS = Collections.synchronizedMap(new HashMap<>());
    private final Map<String, CooldownMap> COOLDOWN = new ConcurrentHashMap<>();
    public final Map<UUID, Runnable> RELOADING_GUN = Collections.synchronizedMap(new HashMap<>());
    private final Mode mode;

    ModeController(BattlePlugin plugin, Mode mode) {
        super(plugin);
        this.mode = mode;
    }

    protected String blp(String path){
        return "mode_"+mode.getId()+"."+path;
    }

    @Override
    public void onDeath(PlayerDeathEvent event, LocalGame localGame){
        plugin.taskHelper.newTask(() -> {
            event.getEntity().spigot().respawn();
        });
    }

    @Override
    public void onSwapHand(PlayerSwapHandItemsEvent event, LocalGame localGame){
        BattleItem item = ApiProvider.consume().getItemManager().read(event.getOffHandItem());
        if(item instanceof Gun){
            Gun gun = (Gun) item;
            event.setCancelled(true);
            doReloadGun(event.getPlayer(), gun);
        }
    }

    @Override
    public void onDropItem(PlayerDropItemEvent event, LocalGame localGame){
        BattleItem item = plugin.itemManager.read(event.getItemDrop().getItemStack());
        if(item != null) event.setCancelled(true);
    }

    @Override
    public void onClickInventory(InventoryClickEvent event, LocalGame localGame, Player player){
        if(event.getClickedInventory() instanceof PlayerInventory){
            BattleItem item = plugin.itemManager.read(event.getCurrentItem());
            if(item != null) event.setCancelled(true);
        }
    }

    void broadcast(LocalGame localGame, String localePath){
        localGame.getPlayers().keySet().forEach(player -> {
            plugin.chatManager.sendPlayer(player, blp(localePath));
        });
    }

    void broadcast(LocalGame localGame, String localePath, UnaryOperator<String> x){
        localGame.getPlayers().keySet().forEach(player -> {
            plugin.chatManager.sendPlayer(player, blp(localePath), x);
        });
    }

    void broadcast(LocalGame localGame, String localePath, ChatMessageType type){
        localGame.getPlayers().keySet().forEach(player -> {
            plugin.chatManager.sendPlayer(player, blp(localePath), type);
        });
    }

    void broadcast(LocalGame localGame, String localePath, ChatMessageType type, UnaryOperator<String> x){
        localGame.getPlayers().keySet().forEach(player -> {
            plugin.chatManager.sendPlayer(player, blp(localePath), type, x);
        });
    }

    void broadcastTitle(LocalGame localGame, String titleLocalePath, String subtitleLocalePath){
        localGame.getPlayers().keySet().forEach(player -> {
            plugin.titleProvider.send(player, blp(titleLocalePath), blp(subtitleLocalePath));
        });
    }

    void broadcastTitle(LocalGame localGame, String titleLocalePath, String subtitleLocalePath, UnaryOperator<String> x){
        localGame.getPlayers().keySet().forEach(player -> {
            plugin.titleProvider.send(player, blp(titleLocalePath), blp(subtitleLocalePath), x);
        });
    }

    void sendTitle(Player player, String titleLocalePath, String subtitleLocalePath, UnaryOperator<String> x){
        plugin.titleProvider.send(player, blp(titleLocalePath), blp(subtitleLocalePath), x);
    }

    void trackTask(LocalGame localGame, String id, int task){
        RUNNING_TASKS.put(localGame.getArena().getId()+id, task);
    }

    boolean hasTask(LocalGame localGame, String id){
        return RUNNING_TASKS.containsKey(localGame.getArena().getId()+id);
    }

    void cancelTask(LocalGame localGame, String id){
        Integer x = RUNNING_TASKS.remove(localGame.getArena().getId()+id);
        if(x != null) plugin.taskHelper.cancelTask(x);
    }

    void cancelAllTasks(LocalGame localGame){
        List<Map.Entry<String, Integer>> x = RUNNING_TASKS.entrySet().stream()
                .filter(e -> e.getKey().startsWith(localGame.getArena().getId()))
                .collect(Collectors.toList());
        x.forEach(e -> plugin.taskHelper.cancelTask(RUNNING_TASKS.remove(e.getKey())));
    }

    void playSound(LocalGame localGame, Sound sound){
        localGame.getPlayers().keySet().forEach(p -> p.playSound(p.getLocation(), sound, 3f, 1f));
    }

    void playSound(LocalGame localGame, String sound){
        localGame.getPlayers().keySet().forEach(p -> p.playSound(p.getLocation(), sound, 3f, 1f));
    }

    @Nullable
    CooldownMap getCooldownMap(LocalGame localGame, String id){
        return COOLDOWN.get(localGame.getArena().getId()+id);
    }

    void clearCooldown(){
        COOLDOWN.clear();
    }

    void performCooldownMap(LocalGame localGame, String id, Consumer<CooldownMap> ifPresent){
        String k = localGame.getArena().getId()+id;
        CooldownMap m = COOLDOWN.get(k);
        if(m != null) ifPresent.accept(m);
    }

    void performCooldownMap(LocalGame localGame, String id, Consumer<CooldownMap> ifPresent, Callable<CooldownMap> otherwise){
        String k = localGame.getArena().getId()+id;
        CooldownMap m = COOLDOWN.get(k);
        if(m == null) {
            try {
                COOLDOWN.put(k, otherwise.call());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else ifPresent.accept(m);
    }

    @Override
    @NotNull public Mode getMode() {
        return mode;
    }

    public void cancelReloadGun(Player player){
        Runnable runnable = RELOADING_GUN.remove(player.getUniqueId());
        if(runnable != null) runnable.run();
    }

    public void doReloadGun(Player player, Gun gun){
        if(RELOADING_GUN.containsKey(player.getUniqueId())) return;

        GunModel gm = gun.getModel();
        MagazineModel mm = gun.getMagazine().getModel();
        AmmoModel am = gun.getMagazine().getAmmo().getModel();
        if(gm == null || mm == null || am == null) return;

        int maxBullet = mm.getAmmunition().getOrDefault(am, 0);
        int currentBullet = gun.getMagazine().getAmmoCount();
        if(currentBullet == maxBullet) return;
        double reloadTime = gm.getReloadTimeCalculator()
                .setVariable("a", currentBullet)
                .setVariable("b", maxBullet).evaluate();
        if(reloadTime <= 0) return;

        plugin.getHandler(GunHandler.class).handleZoomOut(player);

        double maxTime = reloadTime / BattlePlugin.BOSSBAR_UPDATE_INTERVAL;
        int bullerPerTime = (int) Math.floor((maxBullet - currentBullet) / maxTime);
        AtomicLong currentTime = new AtomicLong();
        CustomBossBar cb = gm.getReloadBar();

        int slot = player.getInventory().getHeldItemSlot();

        PlayerBossBar bar = new PlayerBossBar(player, cb.getTitle(), cb.getColor(), cb.getStyle(), playerBossBar -> {
            long now = currentTime.getAndIncrement();
            if(now > maxTime){
                gun.getMagazine().setAmmoCount(Math.min(gun.getMagazine().getAmmoCount(), maxBullet));
                gun.setNextSpray(-1);
                player.getInventory().setItem(slot, plugin.getHandler(GunHandler.class).createGun(gun, false));
                RELOADING_GUN.remove(player.getUniqueId()).run();
            } else {
                playerBossBar.getBar().setProgress(MathUtil.clampDouble(now / maxTime, 0, 1));

                int n = gun.getMagazine().getAmmoCount() + bullerPerTime;
                gun.getMagazine().setAmmoCount(Math.min(n, maxBullet));

                InfoHolder info = new InfoHolder("gun_");
                gun.inform(info);
                playerBossBar.getBar().setTitle(PlaceholderUtil.formatPAPI(player, PlaceholderUtil.formatInfo(cb.getTitle(), plugin.mapInfo(info))));

                playerBossBar.show();
            }
        });

        RELOADING_GUN.put(player.getUniqueId(), () -> {
            if(cb.isPrimarySlot()) plugin.bossbarRenderer.removePrimaryBar(player);
            else plugin.bossbarRenderer.removeSecondaryBar(player);

            if(gm.getReloadEndSound() != null) gm.getReloadEndSound().play(player);
        });

        if(cb.isPrimarySlot()) plugin.bossbarRenderer.setPrimaryBar(bar);
        else plugin.bossbarRenderer.setSecondaryBar(bar);

        if(gm.getReloadStartSound() != null) gm.getReloadStartSound().play(player);
    }
}
