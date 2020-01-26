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
package dev.anhcraft.battle.system.controllers;

import dev.anhcraft.battle.ApiProvider;
import dev.anhcraft.battle.BattleComponent;
import dev.anhcraft.battle.BattlePlugin;
import dev.anhcraft.battle.api.arena.game.LocalGame;
import dev.anhcraft.battle.api.inventory.item.*;
import dev.anhcraft.battle.api.misc.BattleBar;
import dev.anhcraft.battle.api.arena.mode.IMode;
import dev.anhcraft.battle.api.arena.mode.Mode;
import dev.anhcraft.battle.system.renderers.bossbar.PlayerBossBar;
import dev.anhcraft.battle.utils.CooldownMap;
import dev.anhcraft.battle.utils.PlaceholderUtil;
import dev.anhcraft.battle.utils.info.InfoHolder;
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

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public abstract class ModeController extends BattleComponent implements Listener, IMode {
    private final Map<String, Integer> RUNNING_TASKS = new ConcurrentHashMap<>();
    private final Map<String, CooldownMap> COOLDOWN = new ConcurrentHashMap<>();
    public final Map<UUID, Runnable> RELOADING_GUN = new ConcurrentHashMap<>();
    private final Mode mode;

    ModeController(BattlePlugin plugin, Mode mode) {
        super(plugin);
        this.mode = mode;
    }

    protected String blp(String path){
        return "mode_"+mode.getId()+"."+path;
    }

    @Override
    public void onDeath(@NotNull PlayerDeathEvent event, @NotNull LocalGame game){
        plugin.taskHelper.newTask(() -> {
            event.getEntity().spigot().respawn();
        });
    }

    @Override
    public void onSwapItem(@NotNull PlayerSwapHandItemsEvent event, @NotNull LocalGame game){
        BattleItem item = ApiProvider.consume().getItemManager().read(event.getOffHandItem());
        if(item instanceof Gun){
            Gun gun = (Gun) item;
            event.setCancelled(true);
            doReloadGun(event.getPlayer(), gun);
        }
    }

    @Override
    public void onDropItem(@NotNull PlayerDropItemEvent event, @NotNull LocalGame game){
        BattleItem item = plugin.itemManager.read(event.getItemDrop().getItemStack());
        if(item != null) event.setCancelled(true);
    }

    @Override
    public void onClickInventory(@NotNull InventoryClickEvent event, @NotNull LocalGame game, @NotNull Player player){
        if(event.getClickedInventory() instanceof PlayerInventory){
            BattleItem item = plugin.itemManager.read(event.getCurrentItem());
            if(item != null) event.setCancelled(true);
        }
    }

    public void broadcast(LocalGame game, String localePath){
        game.getPlayers().keySet().forEach(player -> {
            plugin.chatManager.sendPlayer(player, blp(localePath));
        });
    }

    public void broadcast(LocalGame game, String localePath, UnaryOperator<String> x){
        game.getPlayers().keySet().forEach(player -> {
            plugin.chatManager.sendPlayer(player, blp(localePath), x);
        });
    }

    public void broadcast(LocalGame game, String localePath, ChatMessageType type){
        game.getPlayers().keySet().forEach(player -> {
            plugin.chatManager.sendPlayer(player, blp(localePath), type);
        });
    }

    public void broadcast(LocalGame game, String localePath, ChatMessageType type, UnaryOperator<String> x){
        game.getPlayers().keySet().forEach(player -> {
            plugin.chatManager.sendPlayer(player, blp(localePath), type, x);
        });
    }

    public void broadcastTitle(LocalGame game, String titleLocalePath, String subtitleLocalePath){
        game.getPlayers().keySet().forEach(player -> {
            sendTitle(player, titleLocalePath, subtitleLocalePath, UnaryOperator.identity());
        });
    }

    public void broadcastTitle(LocalGame game, String titleLocalePath, String subtitleLocalePath, UnaryOperator<String> x){
        game.getPlayers().keySet().forEach(player -> {
            sendTitle(player, titleLocalePath, subtitleLocalePath, x);
        });
    }

    public void sendTitle(Player player, String titleLocalePath, String subtitleLocalePath){
        sendTitle(player, titleLocalePath, subtitleLocalePath, UnaryOperator.identity());
    }

    public void sendTitle(Player player, String titleLocalePath, String subtitleLocalePath, UnaryOperator<String> x){
        String s1 = x.apply(PlaceholderUtil.formatPAPI(player, plugin.getLocaleConf().getString(blp(titleLocalePath))));
        String s2 = x.apply(PlaceholderUtil.formatPAPI(player, plugin.getLocaleConf().getString(blp(subtitleLocalePath))));
        player.sendTitle(s1, s2, 10, 70, 20);
    }

    public void trackTask(LocalGame game, String id, int task){
        RUNNING_TASKS.put(game.getArena().getId()+id, task);
    }

    public boolean hasTask(LocalGame game, String id){
        return RUNNING_TASKS.containsKey(game.getArena().getId()+id);
    }

    public void cancelTask(LocalGame game, String id){
        Integer x = RUNNING_TASKS.remove(game.getArena().getId()+id);
        if(x != null) plugin.taskHelper.cancelTask(x);
    }

    public void cancelAllTasks(LocalGame game){
        List<Map.Entry<String, Integer>> x = RUNNING_TASKS.entrySet().stream()
                .filter(e -> e.getKey().startsWith(game.getArena().getId()))
                .collect(Collectors.toList());
        x.forEach(e -> plugin.taskHelper.cancelTask(RUNNING_TASKS.remove(e.getKey())));
    }

    public void playSound(LocalGame game, Sound sound){
        game.getPlayers().keySet().forEach(p -> p.playSound(p.getLocation(), sound, 3f, 1f));
    }

    public void playSound(LocalGame game, String sound){
        game.getPlayers().keySet().forEach(p -> p.playSound(p.getLocation(), sound, 3f, 1f));
    }

    @Nullable
    public CooldownMap getCooldownMap(LocalGame game, String id){
        return COOLDOWN.get(game.getArena().getId()+id);
    }

    public void clearCooldown(){
        COOLDOWN.clear();
    }

    public void performCooldownMap(LocalGame game, String id, Consumer<CooldownMap> ifPresent){
        String k = game.getArena().getId()+id;
        CooldownMap m = COOLDOWN.get(k);
        if(m != null) ifPresent.accept(m);
    }

    public void performCooldownMap(LocalGame game, String id, Consumer<CooldownMap> ifPresent, Callable<CooldownMap> otherwise){
        String k = game.getArena().getId()+id;
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
    @NotNull
    public Mode getMode() {
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

        plugin.gunManager.handleZoomOut(player, gm);

        double maxTime = reloadTime / BattlePlugin.BOSSBAR_UPDATE_INTERVAL;
        int bullerPerTime = (int) Math.floor((maxBullet - currentBullet) / maxTime);
        AtomicLong currentTime = new AtomicLong();
        BattleBar cb = gm.getReloadBar();

        int slot = player.getInventory().getHeldItemSlot();

        PlayerBossBar bar = new PlayerBossBar(player, cb.getTitle(), cb.getColor(), cb.getStyle(), playerBossBar -> {
            long now = currentTime.getAndIncrement();
            if(now > maxTime){
                gun.getMagazine().setAmmoCount(Math.min(gun.getMagazine().getAmmoCount(), maxBullet));
                gun.setNextSpray(-1);
                player.getInventory().setItem(slot, plugin.gunManager.createGun(gun, false));
                RELOADING_GUN.remove(player.getUniqueId()).run();
            } else {
                playerBossBar.getBar().setProgress(MathUtil.clampDouble(now / maxTime, 0, 1));

                int n = gun.getMagazine().getAmmoCount() + bullerPerTime;
                gun.getMagazine().setAmmoCount(Math.min(n, maxBullet));

                InfoHolder info = new InfoHolder("gun_");
                gun.inform(info);
                playerBossBar.getBar().setTitle(PlaceholderUtil.formatPAPI(player, info.compile().replace(cb.getTitle())));

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
