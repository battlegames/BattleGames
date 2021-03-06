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
import dev.anhcraft.battle.api.BattleBar;
import dev.anhcraft.battle.api.BattleSound;
import dev.anhcraft.battle.api.arena.game.LocalGame;
import dev.anhcraft.battle.api.arena.game.Mode;
import dev.anhcraft.battle.api.arena.game.controllers.GameController;
import dev.anhcraft.battle.api.gui.screen.Window;
import dev.anhcraft.battle.api.inventory.item.*;
import dev.anhcraft.battle.system.renderers.bossbar.PlayerBossBar;
import dev.anhcraft.battle.utils.CooldownMap;
import dev.anhcraft.battle.utils.PlaceholderUtil;
import dev.anhcraft.battle.utils.info.InfoHolder;
import dev.anhcraft.battle.utils.info.InfoReplacer;
import dev.anhcraft.jvmkit.utils.MathUtil;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
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
import java.util.stream.Collectors;

public abstract class GameControllerImpl extends BattleComponent implements Listener, GameController {
    public final Map<UUID, Runnable> RELOADING_GUN = new ConcurrentHashMap<>();
    private final Map<String, Integer> RUNNING_TASKS = new ConcurrentHashMap<>();
    private final Map<String, CooldownMap> COOLDOWN = new ConcurrentHashMap<>();
    private final Mode mode;

    GameControllerImpl(BattlePlugin plugin, Mode mode) {
        super(plugin);
        this.mode = mode;
    }

    protected String blp(String path) {
        return "mode_" + mode.getId() + "." + path;
    }

    @Override
    public void onDeath(@NotNull PlayerDeathEvent event, @NotNull LocalGame game) {
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            event.getEntity().spigot().respawn();
        });
    }

    @Override
    public void onSwapItem(@NotNull PlayerSwapHandItemsEvent event, @NotNull LocalGame game) {
        BattleItem<?> item = ApiProvider.consume().getItemManager().read(event.getOffHandItem());
        if (item instanceof Gun) {
            Gun gun = (Gun) item;
            event.setCancelled(true);
            doReloadGun(event.getPlayer(), gun);
        }
    }

    @Override
    public void onDropItem(@NotNull PlayerDropItemEvent event, @NotNull LocalGame game) {
        BattleItem<?> item = plugin.itemManager.read(event.getItemDrop().getItemStack());
        if (item != null) event.setCancelled(true);
    }

    @Override
    public void onClickInventory(@NotNull InventoryClickEvent event, @NotNull LocalGame game, @NotNull Player player, @NotNull Window window) {
        if (event.getClickedInventory() instanceof PlayerInventory) {
            BattleItem<?> item = plugin.itemManager.read(event.getCurrentItem());
            if (item instanceof Gun && RELOADING_GUN.containsKey(player.getUniqueId())) {
                event.setCancelled(true);
                event.setResult(Event.Result.DENY);
            }
        }
    }

    public void broadcast(@NotNull LocalGame game, @NotNull String localePath) {
        plugin.chatManager.sendPlayers(game.getPlayers().keySet(), blp(localePath));
    }

    public void broadcast(@NotNull LocalGame game, @NotNull String localePath, @NotNull InfoReplacer infoReplacer) {
        plugin.chatManager.sendPlayers(game.getPlayers().keySet(), blp(localePath), infoReplacer);
    }

    public void broadcast(@NotNull LocalGame game, @NotNull String localePath, @NotNull ChatMessageType type) {
        plugin.chatManager.sendPlayers(game.getPlayers().keySet(), blp(localePath), type, null);
    }

    public void broadcast(@NotNull LocalGame game, @NotNull String localePath, @NotNull ChatMessageType type, @NotNull InfoReplacer infoReplacer) {
        plugin.chatManager.sendPlayers(game.getPlayers().keySet(), blp(localePath), type, infoReplacer);
    }

    public void broadcastTitle(@NotNull LocalGame game, @NotNull String titleLocalePath, @NotNull String subtitleLocalePath) {
        sendTitle(game.getPlayers().keySet(), titleLocalePath, subtitleLocalePath, null);
    }

    public void broadcastTitle(@NotNull LocalGame game, @NotNull String titleLocalePath, @NotNull String subtitleLocalePath, @Nullable InfoReplacer infoReplacer) {
        sendTitle(game.getPlayers().keySet(), titleLocalePath, subtitleLocalePath, infoReplacer);
    }

    public void sendTitle(@NotNull Player player, @NotNull String titleLocalePath, @NotNull String subtitleLocalePath) {
        sendTitle(player, titleLocalePath, subtitleLocalePath, null);
    }

    public void sendTitle(@NotNull Player player, @NotNull String titleLocalePath, @NotNull String subtitleLocalePath, @Nullable InfoReplacer infoReplacer) {
        String s1 = Objects.requireNonNull(plugin.getLocalizedMessage(blp(titleLocalePath)));
        String s2 = Objects.requireNonNull(plugin.getLocalizedMessage(blp(subtitleLocalePath)));
        if (infoReplacer == null) {
            player.sendTitle(s1, s2, 10, 70, 20);
        } else {
            player.sendTitle(infoReplacer.replace(s1), infoReplacer.replace(s2), 10, 70, 20);
        }
    }

    public void sendTitle(@NotNull Collection<Player> players, @NotNull String titleLocalePath, @NotNull String subtitleLocalePath, @Nullable InfoReplacer infoReplacer) {
        String s1 = Objects.requireNonNull(plugin.getLocalizedMessage(blp(titleLocalePath)));
        String s2 = Objects.requireNonNull(plugin.getLocalizedMessage(blp(subtitleLocalePath)));
        if (infoReplacer == null) {
            for (Player player : players) {
                player.sendTitle(s1, s2, 10, 70, 20);
            }
        } else {
            s1 = infoReplacer.replace(s1);
            s2 = infoReplacer.replace(s2);
            for (Player player : players) {
                player.sendTitle(s1, s2, 10, 70, 20);
            }
        }
    }

    public void trackTask(@NotNull LocalGame game, @NotNull String id, int task) {
        RUNNING_TASKS.put(game.getArena().getId() + id, task);
    }

    public boolean hasTask(@NotNull LocalGame game, @NotNull String id) {
        return RUNNING_TASKS.containsKey(game.getArena().getId() + id);
    }

    public void cancelTask(@NotNull LocalGame game, @NotNull String id) {
        Integer x = RUNNING_TASKS.remove(game.getArena().getId() + id);
        if (x != null) plugin.getServer().getScheduler().cancelTask(x);
    }

    public void cancelAllTasks(@NotNull LocalGame game) {
        List<Map.Entry<String, Integer>> x = RUNNING_TASKS.entrySet().stream()
                .filter(e -> e.getKey().startsWith(game.getArena().getId()))
                .collect(Collectors.toList());
        x.forEach(e -> plugin.getServer().getScheduler().cancelTask(RUNNING_TASKS.remove(e.getKey())));
    }

    public void playSound(@NotNull LocalGame game, @Nullable BattleSound sound) {
        if (sound == null) return;
        for (Player p : game.getPlayers().keySet()) {
            sound.play(p);
        }
    }

    @Nullable
    public CooldownMap getCooldownMap(LocalGame game, String id) {
        return COOLDOWN.get(game.getArena().getId() + id);
    }

    public void clearCooldown() {
        COOLDOWN.clear();
    }

    public void performCooldownMap(@NotNull LocalGame game, @NotNull String id, @NotNull Consumer<CooldownMap> ifPresent) {
        String k = game.getArena().getId() + id;
        CooldownMap m = COOLDOWN.get(k);
        if (m != null) ifPresent.accept(m);
    }

    public void performCooldownMap(@NotNull LocalGame game, @NotNull String id, @NotNull Consumer<CooldownMap> ifPresent, @NotNull Callable<CooldownMap> otherwise) {
        String k = game.getArena().getId() + id;
        CooldownMap m = COOLDOWN.get(k);
        if (m == null) {
            try {
                COOLDOWN.put(k, otherwise.call());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else ifPresent.accept(m);
    }

    @Override
    @NotNull
    public Mode getMode() {
        return mode;
    }

    public void cancelReloadGun(@NotNull Player player) {
        Runnable runnable = RELOADING_GUN.remove(player.getUniqueId());
        if (runnable != null) runnable.run();
    }

    public void doReloadGun(@NotNull Player player, @NotNull Gun gun) {
        GunModel gm = gun.getModel();
        if (gm == null) return;
        Magazine m = gun.getMagazine();
        MagazineModel mm = m.getModel();
        if (mm == null) {
            plugin.chatManager.sendPlayer(player, "gun.none_magazine_message");
            return;
        }

        if (RELOADING_GUN.containsKey(player.getUniqueId())) return;

        AmmoModel am = m.getAmmo().getModel();
        int maxCap;
        if (am == null) {
            Map.Entry<AmmoModel, Integer> x = mm.getAmmunition().entrySet().iterator().next();
            am = x.getKey();
            maxCap = x.getValue();
        } else {
            maxCap = mm.getAmmunition().getOrDefault(am, 0);
        }
        if (maxCap == 0) return;

        int curCap = m.getAmmoCount();
        if (curCap >= maxCap) return;

        plugin.gunManager.handleZoomOut(player);

        int slot = player.getInventory().getHeldItemSlot();
        long delay = am.getReloadDelay();
        AtomicLong curDeltaTime = new AtomicLong();
        BattleBar cb = gm.getReloadBar();

        PlayerBossBar bar = new PlayerBossBar(player, cb.getTitle(), cb.getColor(), cb.getStyle(), playerBossBar -> {
            long now = curDeltaTime.getAndIncrement();
            int amc = m.getAmmoCount();
            if (now < delay && amc < maxCap) return;
            curDeltaTime.set(0);

            double d = maxCap - amc;
            playerBossBar.getBar().setProgress(1 - MathUtil.clampDouble(d / (maxCap - curCap), 0, 1));
            InfoHolder info = new InfoHolder("gun_");
            gun.inform(info);
            playerBossBar.getBar().setTitle(PlaceholderUtil.formatPAPI(player, info.compile().replace(cb.getTitle())));
            playerBossBar.show();

            if (amc > maxCap) {
                m.setAmmoCount(maxCap);
                gun.setNextSpray(-1);
                player.getInventory().setItem(slot, plugin.gunManager.createGun(gun, false));
                RELOADING_GUN.remove(player.getUniqueId()).run();
            } else {
                m.setAmmoCount(amc + 1);
            }
        });

        RELOADING_GUN.put(player.getUniqueId(), () -> {
            if (cb.isPrimarySlot()) plugin.bossbarRenderer.removePrimaryBar(player);
            else plugin.bossbarRenderer.removeSecondaryBar(player);

            if (gm.getReloadEndSound() != null) gm.getReloadEndSound().play(player);
        });

        if (cb.isPrimarySlot()) plugin.bossbarRenderer.setPrimaryBar(bar);
        else plugin.bossbarRenderer.setSecondaryBar(bar);

        if (gm.getReloadStartSound() != null) gm.getReloadStartSound().play(player);
    }
}
