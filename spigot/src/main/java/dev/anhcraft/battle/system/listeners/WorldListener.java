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
import dev.anhcraft.battle.api.WorldSettings;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.CraftItemEvent;

public class WorldListener extends BattleComponent implements Listener {
    public WorldListener(BattlePlugin plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void spawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) return;
        WorldSettings ws = plugin.getWorldSettings(event.getEntity().getWorld().getName());
        if (ws != null && ws.isPreventMobSpawn()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void death(EntityDeathEvent event) {
        WorldSettings ws = plugin.getWorldSettings(event.getEntity().getWorld().getName());
        if (ws != null && ws.isPreventMobDrops()) {
            event.setDroppedExp(0);
            event.getDrops().clear();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void explode(EntityExplodeEvent event) {
        WorldSettings ws = plugin.getWorldSettings(event.getEntity().getWorld().getName());
        if (ws != null && ws.isPreventExplosions()) {
            event.blockList().clear();
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void explode(BlockExplodeEvent event) {
        WorldSettings ws = plugin.getWorldSettings(event.getBlock().getWorld().getName());
        if (ws != null && ws.isPreventExplosions()) {
            event.blockList().clear();
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void breakBlock(BlockBreakEvent event) {
        WorldSettings ws = plugin.getWorldSettings(event.getBlock().getWorld().getName());
        if (ws != null && ws.isProtectBlocks()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void placeBlock(BlockPlaceEvent event) {
        WorldSettings ws = plugin.getWorldSettings(event.getBlock().getWorld().getName());
        if (ws != null && ws.isProtectBlocks()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void fireSpread(BlockIgniteEvent event) {
        if (event.getCause() == BlockIgniteEvent.IgniteCause.SPREAD) {
            WorldSettings ws = plugin.getWorldSettings(event.getBlock().getWorld().getName());
            if (ws != null && ws.isAntiFireSpread()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void craft(CraftItemEvent event) {
        WorldSettings ws = plugin.getWorldSettings(event.getWhoClicked().getWorld().getName());
        if (ws != null && ws.isDisableCrafting()) {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
        }
    }

    @EventHandler
    public void food(FoodLevelChangeEvent event) {
        WorldSettings ws = plugin.getWorldSettings(event.getEntity().getWorld().getName());
        if (ws != null && ws.isPreventHungry()) {
            event.setFoodLevel(20);
        }
    }
}
