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
package dev.anhcraft.abm.system.managers;

import dev.anhcraft.abm.BattleComponent;
import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.BattleItemManager;
import dev.anhcraft.abm.api.inventory.items.BattleItem;
import dev.anhcraft.abm.api.inventory.items.BattleItemModel;
import dev.anhcraft.abm.api.inventory.items.ItemTag;
import dev.anhcraft.abm.api.inventory.items.ItemType;
import dev.anhcraft.abm.api.misc.info.InfoHolder;
import dev.anhcraft.abm.utils.PlaceholderUtil;
import dev.anhcraft.craftkit.abif.ABIF;
import dev.anhcraft.craftkit.abif.PreparedItem;
import dev.anhcraft.craftkit.cb_common.nbt.CompoundTag;
import dev.anhcraft.craftkit.cb_common.nbt.StringTag;
import dev.anhcraft.craftkit.helpers.ItemNBTHelper;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ItemManager extends BattleComponent implements BattleItemManager {
    private final Map<ItemType, PreparedItem> ITEMS = new HashMap<>();
    private final Map<ItemType, PreparedItem> ITEM_MODELS = new HashMap<>();

    public ItemManager(BattlePlugin plugin) {
        super(plugin);

        ItemType[] types = ItemType.values();
        for(ItemType type : types){
            String k = type.name().toLowerCase();
            ConfigurationSection sec = plugin.getItemConf().getConfigurationSection("model_"+k);
            if(sec != null) ITEM_MODELS.put(type, ABIF.read(sec));
            sec = plugin.getItemConf().getConfigurationSection(k);
            if(sec != null) ITEMS.put(type, ABIF.read(sec));
        }
    }

    @Override
    @Nullable
    public <R extends BattleItemModel> PreparedItem make(@Nullable BattleItem<R> battleItem){
        return make(battleItem, null);
    }

    @Override
    @Nullable
    public <R extends BattleItemModel> PreparedItem make(@Nullable BattleItem<R> battleItem, @Nullable Map<String, String> addition){
        if(battleItem == null) return null;
        R model = battleItem.getModel();
        if(model != null){
            InfoHolder map = battleItem.collectInfo(null);
            if(map == null) return null;
            Map<String, String> info = plugin.mapInfo(map);
            if(addition != null) info.putAll(addition);
            PreparedItem pi = ITEMS.get(model.getItemType()).duplicate();
            pi.name(ChatColor.translateAlternateColorCodes('&', PlaceholderUtil.formatInfo(pi.name(), info)));
            pi.lore().replaceAll(s -> ChatColor.translateAlternateColorCodes('&', PlaceholderUtil.formatInfo(s, info)));
            return pi;
        }
        return null;
    }

    @Override
    @Nullable
    public PreparedItem make(@Nullable BattleItemModel bim){
        return make(bim, null);
    }

    @Override
    @Nullable
    public PreparedItem make(@Nullable BattleItemModel bim, @Nullable Map<String, String> addition){
        if(bim == null) return null;
        Map<String, String> info = plugin.mapInfo(bim.collectInfo(null));
        if(addition != null) info.putAll(addition);
        PreparedItem pi = ITEM_MODELS.get(bim.getItemType()).duplicate();
        pi.name(ChatColor.translateAlternateColorCodes('&', PlaceholderUtil.formatInfo(pi.name(), info)));
        pi.lore().replaceAll(s -> ChatColor.translateAlternateColorCodes('&', PlaceholderUtil.formatInfo(s, info)));
        return pi;
    }

    @Override
    @Nullable
    public BattleItem read(@Nullable ItemStack itemStack){
        if(itemStack == null) return null;
        ItemNBTHelper nbtHelper = ItemNBTHelper.of(itemStack);
        CompoundTag compoundTag = nbtHelper.getTag().get("abm", CompoundTag.class);
        if(compoundTag == null) return null;
        StringTag typeTag = compoundTag.get(ItemTag.ITEM_TYPE, StringTag.class);
        if(typeTag == null) return null;
        BattleItem item = ItemType.valueOf(typeTag.getValue()).make();
        item.load(compoundTag);
        return item;
    }

    @Override
    @Nullable
    public ItemStack write(@Nullable ItemStack itemStack, @Nullable BattleItem<?> battleItem){
        if(itemStack == null || battleItem == null) return null;
        ItemNBTHelper nbtHelper = ItemNBTHelper.of(itemStack);
        CompoundTag compoundTag = nbtHelper.getTag().getOrCreateDefault("abm", CompoundTag.class);
        battleItem.save(compoundTag);
        if(battleItem.getModel() != null)
            compoundTag.put(ItemTag.ITEM_TYPE, battleItem.getModel().getItemType().name());
        nbtHelper.getTag().put("abm", compoundTag);
        return nbtHelper.save();
    }
}
