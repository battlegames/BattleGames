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
package dev.anhcraft.battle.api.misc;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import dev.anhcraft.battle.api.inventory.ItemStorage;
import dev.anhcraft.battle.api.inventory.item.ItemType;
import dev.anhcraft.battle.api.storage.data.PlayerData;
import dev.anhcraft.battle.utils.ConfigurableObject;
import dev.anhcraft.battle.utils.EnumUtil;
import dev.anhcraft.confighelper.ConfigHelper;
import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.annotation.*;
import dev.anhcraft.confighelper.exception.InvalidValueException;
import dev.anhcraft.craftkit.abif.PreparedItem;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Schema
public class Kit extends ConfigurableObject {
    public static final ConfigSchema<Kit> SCHEMA = ConfigSchema.of(Kit.class);
    private static final PreparedItem DEF_NO_ACCESS = new PreparedItem();

    static {
        DEF_NO_ACCESS.material(Material.BARRIER);
    }

    private String id;

    @Key("icon")
    @Explanation("The kit's icon (when players can get it)")
    @Validation(notNull = true)
    private PreparedItem icon;

    @Key("no_access_icon")
    @Explanation("The icon to be showed when players can't access the kit")
    @IgnoreValue(ifNull = true)
    private PreparedItem noAccessIcon = DEF_NO_ACCESS.duplicate();

    @Key("permission")
    @Explanation("The permission that players must have to get the kit")
    private String permission;

    @Key("renew_time")
    @Explanation("The delay time that players have to wait before get the kit again")
    private int renewTime;

    @Key("items.vanilla")
    @Explanation("All vanilla items in this kit")
    @IgnoreValue(ifNull = true)
    @Example({
            "items:",
            "  vanilla:",
            "    '1':",
            "      material: bread",
            "      amount: 16"
    })
    private PreparedItem[] vanillaItems = new PreparedItem[0];

    @Key("items.battle")
    @Explanation({
            "All Battle items in this kit",
            "Example:",
            "<code>gun:",
            "- ak_47",
            "grenade:",
            "- grenade2",
            "ammo:",
            "- 7_62mm",
            "- _50_ae</code>"
    })
    @IgnoreValue(ifNull = true)
    @Example({
            "items:",
            "  battle:",
            "    gun: # gun, ammo, magazine, scope, grenade",
            "    - ak_47",
            "    - desert_eagle"
    })
    private Multimap<ItemType, String> battleItems = HashMultimap.create();

    @Key("boosters")
    @Explanation("The boosters to be given")
    @IgnoreValue(ifNull = true)
    private List<String> boosters = new ArrayList<>();

    @Key("first_join")
    @Explanation("Players receive the kit automatically on their first joins")
    private boolean firstJoin;

    public Kit(@NotNull String id) {
        Validate.notNull(id, "Id must be non-null");
        this.id = id;
    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public PreparedItem getIcon() {
        return icon;
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
    public Multimap<ItemType, String> getBattleItems() {
        return battleItems;
    }

    @NotNull
    public PreparedItem[] getVanillaItems() {
        return vanillaItems;
    }

    public boolean isFirstJoin() {
        return firstJoin;
    }

    @NotNull
    public List<String> getBoosters() {
        return boosters;
    }

    public void givePlayer(@NotNull Player player, @NotNull PlayerData playerData){
        Location loc = player.getLocation();
        for(PreparedItem pi : vanillaItems){
            int in = player.getInventory().firstEmpty();
            if(in == -1){
                player.getWorld().dropItemNaturally(loc, pi.build());
            } else {
                player.getInventory().setItem(in, pi.build());
            }
        }
        battleItems.forEach((type, x) -> {
            ItemStorage is = playerData.getInventory().getStorage(type);
            is.put(x);
        });
        boosters.forEach(s -> playerData.getBoosters().putIfAbsent(s, System.currentTimeMillis()));
    }

    @Override
    protected @Nullable Object conf2schema(@Nullable Object o, ConfigSchema.Entry entry) {
        o = super.conf2schema(o, entry);
        if(o != null) {
            switch (entry.getKey()) {
                case "items.vanilla": {
                    ConfigurationSection cs = (ConfigurationSection) o;
                    Set<String> keys = cs.getKeys(false);
                    PreparedItem[] vanillaItems = new PreparedItem[keys.size()];
                    int i = 0;
                    for(String s : keys){
                        try {
                            vanillaItems[i++] = ConfigHelper.readConfig(cs.getConfigurationSection(s), ConfigSchema.of(PreparedItem.class));
                        } catch (InvalidValueException e) {
                            e.printStackTrace();
                        }
                    }
                    return vanillaItems;
                }
                case "items.battle": {
                    ConfigurationSection cs = (ConfigurationSection) o;
                    Multimap<ItemType, String> items = HashMultimap.create();
                    Set<String> keys = cs.getKeys(false);
                    for(String s : keys){
                        ItemType type = EnumUtil.getEnum(ItemType.values(), s);
                        items.putAll(type, cs.getStringList(s));
                    }
                    return items;
                }
            }
        }
        return o;
    }

    @Override
    protected @Nullable Object schema2conf(@Nullable Object o, ConfigSchema.Entry entry) {
        if(o != null) {
            switch (entry.getKey()) {
                case "items.vanilla": {
                    ConfigurationSection parent = new YamlConfiguration();
                    int i = 0;
                    for(PreparedItem item : (PreparedItem[]) o){
                        YamlConfiguration c = new YamlConfiguration();
                        ConfigHelper.writeConfig(c, ConfigSchema.of(PreparedItem.class), item);
                        parent.set(String.valueOf(i++), c);
                    }
                    return parent;
                }
                case "items.battle": {
                    Multimap<ItemType, String> map = (Multimap<ItemType, String>) o;
                    ConfigurationSection parent = new YamlConfiguration();
                    for (ItemType type : map.keys()){
                        // hashMultimap returns set, that is not friendly with yaml
                        // we have to change it to array list
                        List<String> x = new ArrayList<>(map.get(type));
                        parent.set(type.name().toLowerCase(), x);
                    }
                    return parent;
                }
            }
        }
        return o;
    }
}
