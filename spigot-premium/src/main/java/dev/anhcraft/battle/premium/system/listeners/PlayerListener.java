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

package dev.anhcraft.battle.premium.system.listeners;

import dev.anhcraft.battle.api.BattleApi;
import dev.anhcraft.battle.api.storage.data.PlayerData;
import dev.anhcraft.battle.premium.PremiumModule;
import dev.anhcraft.battle.premium.config.ItemSettings;
import dev.anhcraft.battle.premium.config.WorldSettings;
import dev.anhcraft.battle.premium.stats.AdrenalineShotUseStat;
import dev.anhcraft.battle.premium.stats.MedicalKitUseStat;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerListener implements Listener {
    @EventHandler
    public void craft(CraftItemEvent event) {
        WorldSettings ws = PremiumModule.getInstance().getWorldConfigManagerX().getWorldSettings(event.getWhoClicked().getWorld().getName());
        if (ws != null && ws.isDisableCrafting()) {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
        }
    }

    @EventHandler
    public void food(FoodLevelChangeEvent event) {
        WorldSettings ws = PremiumModule.getInstance().getWorldConfigManagerX().getWorldSettings(event.getEntity().getWorld().getName());
        if (ws != null && ws.isPreventHungry()) {
            event.setFoodLevel(20);
        }
    }

    @EventHandler
    public void itemUse(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = event.getItem();
            Player p = event.getPlayer();
            if (item != null && item.getType() == Material.STONE_SWORD && item.getItemMeta() != null && item.getItemMeta().isUnbreakable()) {
                if (item.getDurability() == 1) {
                    double max = p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                    double now = p.getHealth();
                    if (max != now) {
                        ItemSettings is = PremiumModule.getInstance().getItemConfigManagerX().getItemSettings();
                        p.setHealth(Math.min(max, is.getMedicalKitBonusHealth() + now));
                        p.getInventory().setItemInMainHand(null);
                        if (is.getMedicalKitUseSound() != null) {
                            is.getMedicalKitUseSound().play(p.getLocation());
                        }
                        PlayerData pd = BattleApi.getInstance().getPlayerData(p);
                        if (pd != null) pd.getStats().of(MedicalKitUseStat.class).increase(p);
                    }
                    event.setCancelled(true);
                } else if (item.getDurability() == 4) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 300, 0));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 300, 0));
                    ItemSettings is = PremiumModule.getInstance().getItemConfigManagerX().getItemSettings();
                    if (is.getAdrenalineShotUseSound() != null) {
                        is.getAdrenalineShotUseSound().play(p.getLocation());
                    }
                    p.getInventory().setItemInMainHand(null);
                    PlayerData pd = BattleApi.getInstance().getPlayerData(p);
                    if (pd != null) pd.getStats().of(AdrenalineShotUseStat.class).increase(p);
                    event.setCancelled(true);
                }
            }
        }
        WorldSettings ws = PremiumModule.getInstance().getWorldConfigManagerX().getWorldSettings(event.getPlayer().getWorld().getName());
        if (ws != null && ws.isInteractDisabled()) {
            event.setCancelled(true);
        }
    }
}
