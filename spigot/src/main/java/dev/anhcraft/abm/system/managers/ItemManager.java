package dev.anhcraft.abm.system.managers;

import dev.anhcraft.abif.ABIF;
import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.enums.ItemType;
import dev.anhcraft.abm.api.ext.BattleComponent;
import dev.anhcraft.abm.api.ext.BattleItem;
import dev.anhcraft.abm.api.ext.BattleItemModel;
import dev.anhcraft.abm.api.objects.Ammo;
import dev.anhcraft.abm.api.objects.Gun;
import dev.anhcraft.abm.api.objects.Magazine;
import dev.anhcraft.abm.system.ItemTag;
import dev.anhcraft.abm.utils.info.*;
import dev.anhcraft.jvmkit.utils.MathUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.tags.CustomItemTagContainer;
import org.bukkit.inventory.meta.tags.ItemTagType;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ItemManager extends BattleComponent {
    private static final Pattern INFO_PLACEHOLDER_PATTERN = Pattern.compile("\\{__[a-zA-Z0-9_]+__}");

    public ItemManager(BattlePlugin plugin) {
        super(plugin);
    }

    private static String rplc(String str, Map<String, String> x){
        Matcher m = INFO_PLACEHOLDER_PATTERN.matcher(str);
        StringBuffer sb = new StringBuffer(str.length());
        while(m.find()){
            String p = m.group();
            String s = p.substring(3, p.length()-3).trim();
            m.appendReplacement(sb, x.getOrDefault(s, p));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private BattleItem newItemInstance(ItemType type){
        switch (type){
            case GUN: return new Gun();
            case MAGAZINE: return new Magazine();
            case AMMO: return new Ammo();
        }
        return null;
    }

    @Nullable
    public <R extends BattleItemModel> ItemStack preMakeItem(@Nullable BattleItem<R> battleItem){
        if(battleItem == null) return null;
        Optional<R> opt = battleItem.getModel();
        if(opt.isPresent()){
            String k = opt.get().getItemType().name().toLowerCase();
            ConfigurationSection sec = plugin.getItemConf().getConfigurationSection(k);
            if(sec != null)
                return write(ABIF.load(sec, s ->
                        rplc(s, handleInfo(battleItem.collectInfo(null)))), battleItem);
        }
        return null;
    }

    @Nullable
    public ItemStack makeModel(@Nullable BattleItemModel bim){
        if(bim == null) return null;
        String k = bim.getItemType().name().toLowerCase();
        ConfigurationSection sec = plugin.getItemConf().getConfigurationSection("model_"+k);
        if(sec == null) return null;
        return ABIF.load(sec, s -> rplc(s, handleInfo(bim.collectInfo(null))));
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
        ItemType itemType = ItemType.valueOf(type);
        BattleItem item = newItemInstance(itemType);
        if(item == null) return null;
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
