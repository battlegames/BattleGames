package dev.anhcraft.abm.system.controllers;

import dev.anhcraft.abm.BattleComponent;
import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.APIProvider;
import dev.anhcraft.abm.api.BattleModeController;
import dev.anhcraft.abm.api.game.Game;
import dev.anhcraft.abm.api.game.Mode;
import dev.anhcraft.abm.api.inventory.items.AmmoModel;
import dev.anhcraft.abm.api.inventory.items.BattleItem;
import dev.anhcraft.abm.api.inventory.items.Gun;
import dev.anhcraft.abm.api.inventory.items.GunModel;
import dev.anhcraft.abm.api.misc.CustomBossBar;
import dev.anhcraft.abm.api.misc.info.InfoHolder;
import dev.anhcraft.abm.system.handlers.GunHandler;
import dev.anhcraft.abm.system.renderers.bossbar.PlayerBossBar;
import dev.anhcraft.abm.utils.CooldownMap;
import dev.anhcraft.abm.utils.PlaceholderUtils;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
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
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class ModeController extends BattleComponent implements Listener, BattleModeController {
    private final Map<String, Integer> RUNNING_TASKS = Collections.synchronizedMap(new HashMap<>());
    private final Map<String, CooldownMap> COOLDOWN = new ConcurrentHashMap<>();
    public final Map<UUID, Runnable> RELOADING_GUN = Collections.synchronizedMap(new HashMap<>());
    private Mode mode;

    ModeController(BattlePlugin plugin, Mode mode) {
        super(plugin);
        this.mode = mode;
    }

    @Override
    public void onSwapHand(PlayerSwapHandItemsEvent event, Game game){
        BattleItem item = APIProvider.get().getItemManager().read(event.getOffHandItem());
        if(item instanceof Gun){
            Gun gun = (Gun) item;
            event.setCancelled(true);
            doReloadGun(event.getPlayer(), gun);
        }
    }

    @Override
    public void onDropItem(PlayerDropItemEvent event, Game game){
        BattleItem item = plugin.itemManager.read(event.getItemDrop().getItemStack());
        if(item != null) event.setCancelled(true);
    }

    @Override
    public void onClickInventory(InventoryClickEvent event, Game game, Player player){
        if(event.getClickedInventory() instanceof PlayerInventory){
            BattleItem item = plugin.itemManager.read(event.getCurrentItem());
            if(item != null) event.setCancelled(true);
        }
    }

    void broadcast(Game game, String localePath){
        game.getPlayers().keySet().forEach(player -> plugin.chatManager.sendPlayer(player, localePath));
    }

    void broadcast(Game game, String localePath, Function<String, String> x){
        game.getPlayers().keySet().forEach(player -> plugin.chatManager.sendPlayer(player, localePath, x));
    }

    void broadcast(Game game, String localePath, ChatMessageType type){
        game.getPlayers().keySet().forEach(player -> plugin.chatManager.sendPlayer(player, localePath, type));
    }

    void broadcast(Game game, String localePath, ChatMessageType type, Function<String, String> x){
        game.getPlayers().keySet().forEach(player -> plugin.chatManager.sendPlayer(player, localePath, type, x));
    }

    void broadcastTitle(Game game, String titleLocalePath, String subtitleLocalePath){
        game.getPlayers().keySet().forEach(player -> plugin.titleProvider.send(player, titleLocalePath, subtitleLocalePath));
    }

    void broadcastTitle(Game game, String titleLocalePath, String subtitleLocalePath, Function<String, String> x){
        game.getPlayers().keySet().forEach(player -> plugin.titleProvider.send(player, titleLocalePath, subtitleLocalePath, x));
    }

    void sendTitle(Game game, Player player, String titleLocalePath, String subtitleLocalePath, Function<String, String> x){
        plugin.titleProvider.send(player, titleLocalePath, subtitleLocalePath, x);
    }

    void trackTask(Game game, String id, int task){
        RUNNING_TASKS.put(game.getArena().getId()+id, task);
    }

    boolean hasTask(Game game, String id){
        return RUNNING_TASKS.containsKey(game.getArena().getId()+id);
    }

    void cancelTask(Game game, String id){
        Integer x = RUNNING_TASKS.remove(game.getArena().getId()+id);
        if(x != null) plugin.taskHelper.cancelTask(x);
    }

    void cancelAllTasks(Game game){
        List<Map.Entry<String, Integer>> x = RUNNING_TASKS.entrySet().stream()
                .filter(e -> e.getKey().startsWith(game.getArena().getId()))
                .collect(Collectors.toList());
        x.forEach(e -> plugin.taskHelper.cancelTask(RUNNING_TASKS.remove(e.getKey())));
    }

    void playSound(Game game, Sound sound){
        game.getPlayers().keySet().forEach(p -> p.playSound(p.getLocation(), sound, 3f, 1f));
    }

    void playSound(Game game, String sound){
        game.getPlayers().keySet().forEach(p -> p.playSound(p.getLocation(), sound, 3f, 1f));
    }

    @Nullable
    CooldownMap getCooldownMap(Game game, String id){
        return COOLDOWN.get(game.getArena().getId()+id);
    }

    void clearCooldown(){
        COOLDOWN.clear();
    }

    void performCooldownMap(Game game, String id, Consumer<CooldownMap> ifPresent){
        String k = game.getArena().getId()+id;
        CooldownMap m = COOLDOWN.get(k);
        if(m != null) ifPresent.accept(m);
    }

    void performCooldownMap(Game game, String id, Consumer<CooldownMap> ifPresent, Callable<CooldownMap> otherwise){
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
    @NotNull public Mode getMode() {
        return mode;
    }

    public void cancelReloadGun(Player player){
        Runnable runnable = RELOADING_GUN.remove(player.getUniqueId());
        if(runnable != null) runnable.run();
    }

    public void doReloadGun(Player player, Gun gun){
        if(RELOADING_GUN.containsKey(player.getUniqueId())) return;

        Optional<GunModel> gmo = gun.getModel();
        if(!gmo.isPresent()) return;
        GunModel gm = gmo.get();

        int maxBullet = gun.getMagazine().getModel().map(magazineModel -> {
            Optional<AmmoModel> aop = gun.getMagazine().getAmmo().getModel();
            return aop.isPresent() ? magazineModel.getAmmunition().getOrDefault(aop.get(), 0) : 0;
        }).get();
        int currentBullet = gun.getMagazine().getAmmoCount();
        if(currentBullet == maxBullet) return;
        long maxTime = (long) gm.getReloadTimeCalculator()
                .setVariable("a", currentBullet)
                .setVariable("b", maxBullet).evaluate();
        if(maxTime <= 0) return;

        plugin.getHandler(GunHandler.class).handleZoomOut(player);

        long totalTime = maxTime / BattlePlugin.BOSSBAR_UPDATE_INTERVAL;
        long tickBulletInc = Math.max(totalTime / (maxBullet - currentBullet), 1);
        AtomicLong currentTime = new AtomicLong(totalTime);
        CustomBossBar cb = gm.getReloadBar();

        int slot = player.getInventory().getHeldItemSlot();

        PlayerBossBar bar = new PlayerBossBar(player, cb.getTitle(), cb.getColor(), cb.getStyle(), playerBossBar -> {
            long now = currentTime.getAndDecrement();
            if(now <= 0){
                gun.getMagazine().setAmmoCount(Math.min(gun.getMagazine().getAmmoCount(), maxBullet));
                player.getInventory().setItem(slot, plugin.getHandler(GunHandler.class).createGun(gun, false));
                RELOADING_GUN.remove(player.getUniqueId()).run();
            } else {
                playerBossBar.getBar().setProgress(1 - Math.max(0, 1.0 / totalTime * now));

                if (now % tickBulletInc == 0) {
                    gun.getMagazine().setAmmoCount(gun.getMagazine().getAmmoCount() + 1);
                }

                InfoHolder info = new InfoHolder("gun_");
                gun.inform(info);
                playerBossBar.getBar().setTitle(PlaceholderUtils.formatPAPI(player, PlaceholderUtils.formatInfo(cb.getTitle(), plugin.mapInfo(info))));

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
