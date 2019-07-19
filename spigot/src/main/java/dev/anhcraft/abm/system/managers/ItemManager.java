package dev.anhcraft.abm.system.managers;

import dev.anhcraft.abif.ABIF;
import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.enums.ItemType;
import dev.anhcraft.abm.api.ext.BattleComponent;
import dev.anhcraft.abm.api.ext.BattleItem;
import dev.anhcraft.abm.api.impl.BattleItemModel;
import dev.anhcraft.abm.api.impl.Informative;
import dev.anhcraft.abm.api.objects.AmmoItem;
import dev.anhcraft.abm.api.objects.GunItem;
import dev.anhcraft.abm.api.objects.MagazineItem;
import dev.anhcraft.abm.system.ItemTag;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.tags.CustomItemTagContainer;
import org.bukkit.inventory.meta.tags.ItemTagType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemManager extends BattleComponent {
    private static final Pattern INFO_PLACEHOLDER_PATTERN = Pattern.compile("\\{__[a-zA-Z0-9_]+__}");

    public ItemManager(BattlePlugin plugin) {
        super(plugin);
    }

    public static String rplc(String str, Map<String, String> x){
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
            case GUN: return new GunItem();
            case MAGAZINE: return new MagazineItem();
            case AMMO: return new AmmoItem();
        }
        return null;
    }

    @Nullable
    public ItemStack preMake(@Nullable BattleItem battleItem){
        if(battleItem == null) return null;
        ConfigurationSection sec = plugin.getItemConf().getConfigurationSection(battleItem.getModel().getItemType().name().toLowerCase());
        if(sec == null) return null;
        return write(ABIF.load(sec, s -> {
            Map<String, String> plc = new HashMap<>();
            ConfigurationSection cs = plugin.getLocaleConf().getConfigurationSection("items."+battleItem.getModel().getItemType().name().toLowerCase());
            if(cs == null) return s;
            if(battleItem instanceof Informative)  ((Informative) battleItem).writeInfo(plc, cs);
            if(battleItem.getModel() instanceof Informative)  ((Informative) battleItem.getModel()).writeInfo(plc, cs);
            return rplc(s, plc);
        }), battleItem);
    }

    @Nullable
    public ItemStack makeModel(@Nullable BattleItemModel bim){
        if(bim == null) return null;
        ConfigurationSection sec = plugin.getItemConf().getConfigurationSection("model_"+bim.getItemType().name().toLowerCase());
        if(sec == null) return null;
        return ABIF.load(sec, s -> {
            Map<String, String> plc = new HashMap<>();
            ConfigurationSection cs = plugin.getLocaleConf().getConfigurationSection("items."+
                    bim.getItemType().name().toLowerCase());
            if(bim instanceof Informative) ((Informative) bim).writeInfo(plc, cs);
            return rplc(s, plc);
        });
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
    public ItemStack write(@Nullable ItemStack itemStack, @Nullable BattleItem battleItem){
        if(itemStack == null || battleItem == null) return null;
        ItemMeta meta = itemStack.getItemMeta();
        if(meta == null) return null;
        CustomItemTagContainer c = meta.getCustomTagContainer();
        battleItem.save(c);
        c.setCustomTag(ItemTag.ITEM_TYPE, ItemTagType.STRING, battleItem.getModel().getItemType().name());
        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
