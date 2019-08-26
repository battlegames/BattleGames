package dev.anhcraft.abm.api.misc;

import dev.anhcraft.craftkit.kits.abif.ABIF;
import dev.anhcraft.craftkit.kits.abif.PreparedItem;
import dev.anhcraft.abm.api.inventory.items.ItemType;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Kit {
    private final Map<ItemType, List<String>> abmItems = new HashMap<>();
    private String id;
    private PreparedItem icon;
    private PreparedItem noAccessIcon;
    private String permission;
    private int renewTime;
    private ItemStack[] vanillaItems;

    public Kit(@NotNull String id, @NotNull ConfigurationSection conf) {
        Validate.notNull(id, "Id must be non-null");
        Validate.notNull(conf, "Conf must be non-null");
        this.id = id;

        ConfigurationSection ic = conf.getConfigurationSection("icon");
        if(ic == null) throw new NullPointerException("Icon must be specified");
        icon = ABIF.read(ic);
        ConfigurationSection naic = conf.getConfigurationSection("no_access_icon");
        noAccessIcon = naic == null ? PreparedItem.of(new ItemStack(Material.BARRIER, 1)) : ABIF.read(naic);
        permission = conf.getString("permission");
        renewTime = conf.getInt("renew_time", -1);

        ConfigurationSection iv = conf.getConfigurationSection("items.vanilla");
        if(iv != null){
            Set<String> keys = iv.getKeys(false);
            vanillaItems = new ItemStack[keys.size()];
            int i = 0;
            for(String k : keys){
                ConfigurationSection x = iv.getConfigurationSection(k);
                if(x != null) vanillaItems[i++] = ABIF.read(x).build();
            }
        } else vanillaItems = new ItemStack[0];

        ConfigurationSection ia = conf.getConfigurationSection("items.abm");
        if(ia != null){
            Set<String> keys = ia.getKeys(false);
            for(String s : keys){
                ItemType type = ItemType.valueOf(s.toUpperCase());
                abmItems.put(type, ia.getStringList(s));
            }
        }
    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public PreparedItem getIcon() {
        return icon.duplicate();
    }

    @NotNull
    public PreparedItem getNoAccessIcon() {
        return noAccessIcon;
    }

    @Nullable
    public String getPermission() {
        return permission;
    }

    public int getRenewTime() {
        return renewTime;
    }

    @NotNull
    public Map<ItemType, List<String>> getAbmItems() {
        return abmItems;
    }

    @NotNull
    public ItemStack[] getVanillaItems() {
        return vanillaItems;
    }
}
