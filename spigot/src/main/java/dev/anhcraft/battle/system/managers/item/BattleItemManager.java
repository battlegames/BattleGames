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
package dev.anhcraft.battle.system.managers.item;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import dev.anhcraft.battle.BattleComponent;
import dev.anhcraft.battle.BattlePlugin;
import dev.anhcraft.battle.api.inventory.item.*;
import dev.anhcraft.battle.utils.ItemUtil;
import dev.anhcraft.battle.utils.PreparedItem;
import dev.anhcraft.battle.utils.info.InfoHolder;
import dev.anhcraft.battle.utils.info.InfoReplacer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public class BattleItemManager extends BattleComponent implements ItemManager {
    private final Map<ItemType, PreparedItem> ITEMS = new EnumMap<>(ItemType.class);
    private final Map<ItemType, PreparedItem> MODELS = new EnumMap<>(ItemType.class);

    public BattleItemManager(BattlePlugin plugin) {
        super(plugin);

        PreparedItem empty = new PreparedItem();
        for (ItemType type : ItemType.values()) {
            ITEMS.put(type, empty);
            MODELS.put(type, empty);
        }
    }

    public void defineItemTemplate(ItemType itemType, PreparedItem preparedItem) {
        ITEMS.put(itemType, preparedItem);
    }

    public void defineModelTemplate(ItemType itemType, PreparedItem preparedItem) {
        MODELS.put(itemType, preparedItem);
    }

    public boolean selectItem(Player player, BattleItemModel m) {
        if (m instanceof GunModel) {
            return plugin.gunManager.selectGun(player, (GunModel) m);
        }
        //noinspection rawtypes
        BattleItem bi = m.getItemType().make();
        //noinspection unchecked
        bi.setModel(m);
        return selectItem(player, bi);
    }

    public boolean selectItem(Player player, BattleItem<?> bi) {
        if (bi instanceof Gun) {
            return plugin.gunManager.selectGun(player, (Gun) bi);
        }
        BattleItemModel m = Objects.requireNonNull(bi.getModel());
        int slot = player.getInventory().firstEmpty();
        if (slot == -1) return false;
        player.getInventory().setItem(slot, createItem(bi, m));
        return true;
    }

    public ItemStack createItem(BattleItem<?> bi, BattleItemModel m) {
        if (bi instanceof Gun) {
            throw new UnsupportedOperationException("call GunManager#createItem instead");
        }
        PreparedItem pi = plugin.itemManager.make(bi);
        if (pi == null) return null;
        else {
            if (m instanceof SingleSkinItem) pi = ((SingleSkinItem) m).getSkin().transform(pi);
            return plugin.itemManager.write(pi.build(), bi);
        }
    }

    @Override
    @Nullable
    public <R extends BattleItemModel> PreparedItem make(@Nullable BattleItem<R> battleItem) {
        return make(battleItem, null);
    }

    @Override
    @Nullable
    public <R extends BattleItemModel> PreparedItem make(@Nullable BattleItem<R> battleItem, @Nullable Map<String, String> addition) {
        if (battleItem == null) return null;
        R model = battleItem.getModel();
        if (model != null) {
            InfoHolder map = battleItem.collectInfo(null);
            if (map == null) return null;
            InfoReplacer ipr = map.compile();
            if (addition != null) ipr.getMap().putAll(addition);
            return ipr.replace(ITEMS.get(model.getItemType()).duplicate());
        }
        return null;
    }

    @Override
    @Nullable
    public PreparedItem make(@Nullable BattleItemModel bim) {
        return make(bim, null);
    }

    @Override
    @Nullable
    public PreparedItem make(@Nullable BattleItemModel bim, @Nullable Map<String, String> addition) {
        if (bim == null) return null;
        InfoReplacer info = bim.collectInfo(null).compile();
        if (addition != null) info.getMap().putAll(addition);
        return info.replace(MODELS.get(bim.getItemType()).duplicate());
    }

    @Override
    @Nullable
    public BattleItem read(@Nullable ItemStack itemStack) {
        if (ItemUtil.isEmpty(itemStack)) return null;
        NBTItem i = new NBTItem(itemStack);
        NBTCompound compoundTag = i.getCompound("abm");
        if (compoundTag == null) return null;
        String typeTag = compoundTag.getString(ItemTag.ITEM_TYPE);
        if (typeTag == null) return null;
        BattleItem item = ItemType.valueOf(typeTag).make();
        item.load(compoundTag);
        return item;
    }

    @Override
    @Nullable
    public ItemStack write(@Nullable ItemStack itemStack, @Nullable BattleItem<?> battleItem) {
        if (battleItem == null || ItemUtil.isEmpty(itemStack)) return null;
        NBTItem nbtHelper = new NBTItem(itemStack);
        NBTCompound compoundTag = nbtHelper.getOrCreateCompound("abm");
        battleItem.save(compoundTag);
        if (battleItem.getModel() != null)
            compoundTag.setString(ItemTag.ITEM_TYPE, battleItem.getModel().getItemType().name());
        nbtHelper.applyNBT(itemStack);
        return itemStack;
    }
}
