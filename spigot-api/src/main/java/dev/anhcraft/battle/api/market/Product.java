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

package dev.anhcraft.battle.api.market;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import dev.anhcraft.battle.api.ApiProvider;
import dev.anhcraft.battle.api.inventory.ItemStorage;
import dev.anhcraft.battle.api.inventory.items.ItemType;
import dev.anhcraft.battle.api.misc.ConfigurableObject;
import dev.anhcraft.battle.api.misc.Perk;
import dev.anhcraft.battle.api.misc.info.InfoHolder;
import dev.anhcraft.battle.api.misc.info.Informative;
import dev.anhcraft.battle.api.storage.data.PlayerData;
import dev.anhcraft.battle.utils.EnumUtil;
import dev.anhcraft.battle.utils.PlaceholderUtil;
import dev.anhcraft.confighelper.ConfigHelper;
import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.annotation.*;
import dev.anhcraft.confighelper.exception.InvalidValueException;
import dev.anhcraft.craftkit.abif.PreparedItem;
import dev.anhcraft.jvmkit.utils.Condition;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Schema
public class Product extends ConfigurableObject implements Informative {
    public static final ConfigSchema<Product> SCHEMA = ConfigSchema.of(Product.class);

    private String id;

    @Key("icon")
    @Explanation("Product's icon")
    @Validation(notNull = true)
    private PreparedItem icon;

    @Key("price")
    @Explanation("How much does this product cost")
    private double price;

    @Key("in_game_only")
    @Explanation("Make this product only available during the game")
    private boolean inGameOnly;

    @Key("executions.perform_commands")
    @Explanation({
            "The commands to be performed by the console later",
            "You can use placeholders here; they are parsed",
            "from the buyer's info"
    })
    @IgnoreValue(ifNull = true)
    private List<String> commands = new ArrayList<>();

    @Key("executions.give_perks")
    @Explanation("The perks to be given later")
    @IgnoreValue(ifNull = true)
    private List<String> perks = new ArrayList<>();

    @Key("executions.give_items.vanilla")
    @Explanation("The vanilla items to be given later")
    @IgnoreValue(ifNull = true)
    private PreparedItem[] vanillaItems = new PreparedItem[0];

    @Key("executions.give_items.battle")
    @Explanation("The Battle items to be given later")
    @IgnoreValue(ifNull = true)
    private Multimap<ItemType, String> battleItems = HashMultimap.create();

    @Key("executions.give_exp.vanilla")
    @Explanation("The vanilla exp to be given later")
    private int vanillaExp;

    @Key("executions.give_exp.battle")
    @Explanation("The Battle exp to be given later")
    private long battleExp;

    public Product(@NotNull String id) {
        Condition.argNotNull("id", id);
        this.id = id;
    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public PreparedItem getIcon() {
        return icon.duplicate();
    }

    public double getPrice() {
        return price;
    }

    public boolean isInGameOnly() {
        return inGameOnly;
    }

    @NotNull
    public List<String> getCommands() {
        return commands;
    }

    @NotNull
    public List<String> getPerks() {
        return perks;
    }

    @NotNull
    public PreparedItem[] getVanillaItems() {
        return vanillaItems;
    }

    @NotNull
    public Multimap<ItemType, String> getBattleItems() {
        return battleItems;
    }

    public double getVanillaExp() {
        return vanillaExp;
    }

    public double getBattleExp() {
        return battleExp;
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
        for(String perk : perks){
            Perk p = ApiProvider.consume().getPerk(perk);
            if(p != null) p.give(player);
        }
        for(String cmd : commands){
            cmd = PlaceholderUtil.formatPAPI(player, cmd);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        }
        if(vanillaExp > 0){
            player.giveExp(vanillaExp);
        }
        if(battleExp > 0){
            playerData.getExp().addAndGet(battleExp);
        }
    }

    @Override
    protected @Nullable Object conf2schema(@Nullable Object o, ConfigSchema.Entry entry) {
        if(o != null) {
            switch (entry.getKey()) {
                case "executions.give_items.vanilla": {
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
                case "executions.give_items.battle": {
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
                case "executions.give_items.vanilla": {
                    ConfigurationSection parent = new YamlConfiguration();
                    int i = 0;
                    for(PreparedItem item : (PreparedItem[]) o){
                        YamlConfiguration c = new YamlConfiguration();
                        ConfigHelper.writeConfig(c, ConfigSchema.of(PreparedItem.class), item);
                        parent.set(String.valueOf(i++), c);
                    }
                    return parent;
                }
                case "executions.give_items.battle": {
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

    @Override
    public void inform(@NotNull InfoHolder holder) {
        holder.inform("id", id)
                .inform("price", price)
                .inform("command_count", commands.size())
                .inform("perk_count", perks.size())
                .inform("vanilla_item_count", vanillaItems.length)
                .inform("vanilla_exp_count", vanillaExp)
                .inform("battle_item_count", battleItems.size())
                .inform("battle_exp_count", battleExp);
    }
}
