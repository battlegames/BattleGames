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
package dev.anhcraft.abm.api.misc;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import dev.anhcraft.craftkit.kits.abif.ABIF;
import dev.anhcraft.craftkit.kits.abif.PreparedItem;
import dev.anhcraft.abm.api.inventory.items.ItemType;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Kit {
    private Multimap<ItemType, String> abmItems = HashMultimap.create();
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
                abmItems.putAll(type, ia.getStringList(s));
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
    public Multimap<ItemType, String> getAbmItems() {
        return abmItems;
    }

    @NotNull
    public ItemStack[] getVanillaItems() {
        return vanillaItems;
    }
}
