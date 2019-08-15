package dev.anhcraft.abm.system.managers;

import dev.anhcraft.abif.ABIF;
import dev.anhcraft.abif.PreparedItem;
import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.enums.ItemType;
import dev.anhcraft.abm.api.ext.BattleComponent;
import dev.anhcraft.abm.api.ext.BattleItem;
import dev.anhcraft.abm.api.ext.BattleItemModel;
import dev.anhcraft.abm.system.ItemTag;
import dev.anhcraft.abm.utils.PlaceholderUtils;
import dev.anhcraft.abm.utils.info.*;
import dev.anhcraft.jvmkit.utils.MathUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.tags.CustomItemTagContainer;
import org.bukkit.inventory.meta.tags.ItemTagType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ItemManager extends BattleComponent {
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
    public <R extends BattleItemModel> PreparedItem make(@Nullable BattleItem<R> battleItem, Map<String, String> addition){
        if(battleItem == null) return null;
        Optional<R> opt = battleItem.getModel();
        if(opt.isPresent()){
            Map<String, String> info = handleInfo(battleItem.collectInfo(null));
            if(addition != null) info.putAll(addition);
            PreparedItem pi = ITEMS.get(opt.get().getItemType()).duplicate();
            pi.name(ChatColor.translateAlternateColorCodes('&', PlaceholderUtils.formatInfo(pi.name(), info)));
            pi.lore(pi.lore().stream().map(s -> ChatColor.translateAlternateColorCodes('&', PlaceholderUtils.formatInfo(s, info))).collect(Collectors.toList()));
            return pi;
        }
        return null;
    }

    @Nullable
    public PreparedItem make(@Nullable BattleItemModel bim){
        return make(bim, null);
    }

    @Nullable
    public PreparedItem make(@Nullable BattleItemModel bim, Map<String, String> addition){
        if(bim == null) return null;
        Map<String, String> info = handleInfo(bim.collectInfo(null));
        if(addition != null) info.putAll(addition);
        PreparedItem pi = ITEM_MODELS.get(bim.getItemType()).duplicate();
        pi.name(ChatColor.translateAlternateColorCodes('&', PlaceholderUtils.formatInfo(pi.name(), info)));
        pi.lore(pi.lore().stream().map(s -> ChatColor.translateAlternateColorCodes('&', PlaceholderUtils.formatInfo(s, info))).collect(Collectors.toList()));
        return pi;
    }

    @SuppressWarnings("ConstantConditions")
    private Map<String, String> handleInfo(InfoHolder holder){
        return holder.read().entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> {
                    InfoData data = entry.getValue();
                    if(data instanceof InfoBooleanData){
                        if(((InfoBooleanData) data).getValue())
                            return plugin.getLocaleConf().getString("state.enabled");
                        else
                            return plugin.getLocaleConf().getString("state.disabled");
                    }
                    else if(data instanceof InfoDoubleData)
                        return MathUtil.formatRound(((InfoDoubleData) data).getValue());
                    else if(data instanceof InfoIntData)
                        return Integer.toString(((InfoIntData) data).getValue());
                    else if(data instanceof InfoLongData)
                        return Long.toString(((InfoLongData) data).getValue());
                    else if(data instanceof InfoStringData)
                        return ((InfoStringData) data).getValue();
                    return "Error! (data class="+data.getClass().getSimpleName()+")";
                }
        ));
    }

    @Nullable
    public BattleItem read(@Nullable ItemStack itemStack){
        if(itemStack == null) return null;
        ItemMeta meta = itemStack.getItemMeta();
        if(meta == null) return null;
        CustomItemTagContainer c = meta.getCustomTagContainer();
        String type = c.getCustomTag(ItemTag.ITEM_TYPE, ItemTagType.STRING);
        if(type == null) return null;
        BattleItem item = ItemType.valueOf(type).make();
        item.load(c);
        return item;
    }

    @Nullable
    public ItemStack write(@Nullable ItemStack itemStack, @Nullable BattleItem<?> battleItem){
        if(itemStack == null || battleItem == null) return null;
        ItemMeta meta = itemStack.getItemMeta();
        if(meta == null) return null;
        CustomItemTagContainer c = meta.getCustomTagContainer();
        battleItem.save(c);
        if(battleItem.getModel().isPresent())
            c.setCustomTag(ItemTag.ITEM_TYPE, ItemTagType.STRING, battleItem.getModel().get().getItemType().name());
        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
