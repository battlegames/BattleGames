package dev.anhcraft.abm.api.objects;

import dev.anhcraft.abif.ABIF;
import dev.anhcraft.abm.api.enums.ItemType;
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
    private ItemStack icon;
    private ItemStack noAccessIcon;
    private String permission;
    private int renewTime;
    private ItemStack[] vanillaItems;

    public Kit(@NotNull String id, @NotNull ConfigurationSection conf) {
        Validate.notNull(id, "Id must be non-null");
        Validate.notNull(conf, "Conf must be non-null");
        this.id = id;

        ConfigurationSection ic = conf.getConfigurationSection("icon");
        if(ic == null) throw new NullPointerException("Icon must be specified");
        icon = ABIF.load(ic);
        ConfigurationSection naic = conf.getConfigurationSection("no_access_icon");
        noAccessIcon = naic == null ? new ItemStack(Material.BARRIER, 1) : ABIF.load(naic);
        permission = conf.getString("permission");
        renewTime = conf.getInt("renew_time", -1);

        ConfigurationSection iv = conf.getConfigurationSection("items.vanilla");
        if(iv != null){
            Set<String> keys = iv.getKeys(false);
            vanillaItems = new ItemStack[keys.size()];
            int i = 0;
            for(String k : keys){
                ConfigurationSection x = iv.getConfigurationSection(k);
                if(x != null) vanillaItems[i++] = ABIF.load(x);
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
    public ItemStack getIcon() {
        return icon;
    }

    @NotNull
    public ItemStack getNoAccessIcon() {
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
