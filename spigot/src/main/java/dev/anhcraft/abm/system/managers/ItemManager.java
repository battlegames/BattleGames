package dev.anhcraft.abm.system.managers;

import dev.anhcraft.abif.ABIF;
import dev.anhcraft.abif.PreparedItem;
import dev.anhcraft.abm.BattleComponent;
import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.BattleItemManager;
import dev.anhcraft.abm.api.inventory.items.BattleItem;
import dev.anhcraft.abm.api.inventory.items.BattleItemModel;
import dev.anhcraft.abm.api.inventory.items.ItemTag;
import dev.anhcraft.abm.api.inventory.items.ItemType;
import dev.anhcraft.abm.utils.ListUtil;
import dev.anhcraft.abm.utils.PlaceholderUtils;
import dev.anhcraft.craftkit.cb_common.kits.nbt.CompoundTag;
import dev.anhcraft.craftkit.cb_common.kits.nbt.StringTag;
import dev.anhcraft.craftkit.helpers.ItemNBTHelper;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    @Nullable
    public <R extends BattleItemModel> PreparedItem make(@Nullable BattleItem<R> battleItem){
        return make(battleItem, null);
    }

    @Nullable
    public <R extends BattleItemModel> PreparedItem make(@Nullable BattleItem<R> battleItem, @Nullable Map<String, String> addition){
        if(battleItem == null) return null;
        Optional<R> opt = battleItem.getModel();
        if(opt.isPresent()){
            Map<String, String> info = plugin.mapInfo(battleItem.collectInfo(null));
            if(addition != null) info.putAll(addition);
            PreparedItem pi = ITEMS.get(opt.get().getItemType()).duplicate();
            pi.name(ChatColor.translateAlternateColorCodes('&', PlaceholderUtils.formatInfo(pi.name(), info)));
            ListUtil.update(pi.lore(), s -> ChatColor.translateAlternateColorCodes('&', PlaceholderUtils.formatInfo(s, info)));
            return pi;
        }
        return null;
    }

    @Nullable
    public PreparedItem make(@Nullable BattleItemModel bim){
        return make(bim, null);
    }

    @Nullable
    public PreparedItem make(@Nullable BattleItemModel bim, @Nullable Map<String, String> addition){
        if(bim == null) return null;
        Map<String, String> info = plugin.mapInfo(bim.collectInfo(null));
        if(addition != null) info.putAll(addition);
        PreparedItem pi = ITEM_MODELS.get(bim.getItemType()).duplicate();
        pi.name(ChatColor.translateAlternateColorCodes('&', PlaceholderUtils.formatInfo(pi.name(), info)));
        ListUtil.update(pi.lore(), s -> ChatColor.translateAlternateColorCodes('&', PlaceholderUtils.formatInfo(s, info)));
        return pi;
    }

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

    @Nullable
    public ItemStack write(@Nullable ItemStack itemStack, @Nullable BattleItem<?> battleItem){
        if(itemStack == null || battleItem == null) return null;
        ItemNBTHelper nbtHelper = ItemNBTHelper.of(itemStack);
        CompoundTag compoundTag = nbtHelper.getTag().getOrCreateDefault("abm", CompoundTag.class);
        battleItem.save(compoundTag);
        if(battleItem.getModel().isPresent())
            compoundTag.put(ItemTag.ITEM_TYPE, battleItem.getModel().get().getItemType().name());
        nbtHelper.getTag().put("abm", compoundTag);
        return nbtHelper.save();
    }
}
