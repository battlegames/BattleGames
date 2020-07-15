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
import dev.anhcraft.battle.ApiProvider;
import dev.anhcraft.battle.api.BattleApi;
import dev.anhcraft.battle.api.economy.CurrencyType;
import dev.anhcraft.battle.api.inventory.Backpack;
import dev.anhcraft.battle.api.inventory.item.BattleItemModel;
import dev.anhcraft.battle.api.inventory.item.ItemType;
import dev.anhcraft.battle.api.misc.Booster;
import dev.anhcraft.battle.api.misc.Perk;
import dev.anhcraft.battle.api.stats.natives.ExpStat;
import dev.anhcraft.battle.api.storage.data.PlayerData;
import dev.anhcraft.battle.impl.Informative;
import dev.anhcraft.battle.utils.ConfigurableObject;
import dev.anhcraft.battle.utils.EnumUtil;
import dev.anhcraft.battle.utils.PlaceholderUtil;
import dev.anhcraft.battle.utils.info.InfoHolder;
import dev.anhcraft.confighelper.ConfigHelper;
import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.annotation.*;
import dev.anhcraft.confighelper.exception.InvalidValueException;
import dev.anhcraft.craftkit.abif.PreparedItem;
import dev.anhcraft.jvmkit.utils.CollectionUtil;
import dev.anhcraft.jvmkit.utils.Condition;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("FieldMayBeFinal")
@Schema
public class Product extends ConfigurableObject implements Informative {
    public static final PreparedItem DEFAULT_ICON = new PreparedItem();
    public static final ConfigSchema<Product> SCHEMA = ConfigSchema.of(Product.class);

    static {
        DEFAULT_ICON.material(Material.STONE);
    }

    private final String id;

    @Key("icon")
    @Explanation("Product's icon")
    private PreparedItem icon;

    @Key("package_name")
    @Explanation({
            "A nice name for the package icon.",
            "This product is 'a package' when it gives player booster, perks, exp or items",
            "(for items, <b>requires the amount of two or more</b>). You can read more",
            "at the Market schema (option 'default_product_icon.package')",
            "The icon for a package will be created automatically when no icon is set"
    })
    private String packageName;

    @Key("package_material")
    @Explanation({
            "The material for the package icon",
            "<i>What is a package?</i> Read above (option 'package_name')"
    })
    @PrettyEnum
    private Material packageMaterial;

    @Key("currency")
    @Explanation("The currency to be used")
    @PrettyEnum
    private CurrencyType currency = CurrencyType.VAULT;

    @Key("price")
    @Explanation("The cost of this product")
    private double price;

    @Key("in_game_only")
    @Explanation("Make this product only available during the game")
    private boolean inGameOnly;

    @Key("reserved_game_modes")
    @Explanation({
            "Make this product only available during certain game modes",
            "This option only takes effect if <b>in_game_only</b> set to <i>true</i>",
            "All game modes are non-case-sensitive"
    })
    private List<String> gameModeReserved;

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

    @Key("executions.give_boosters")
    @Explanation("The boosters to be given later")
    @IgnoreValue(ifNull = true)
    private List<String> boosters = new ArrayList<>();

    @Key("executions.give_items.vanilla")
    @Explanation("The vanilla items to be given later")
    @IgnoreValue(ifNull = true)
    @Example({
            "executions:",
            "  give_items:",
            "    vanilla:",
            "      '1':",
            "        material: cookie",
            "        amount: 16"
    })
    private PreparedItem[] vanillaItems = new PreparedItem[0];

    @Key("executions.give_items.battle")
    @Explanation("The Battle items to be given later")
    @IgnoreValue(ifNull = true)
    @Example({
            "executions:",
            "  give_items:",
            "    battle:",
            "      gun: # gun, ammo, magazine, scope, grenade",
            "      - ak_47",
    })
    private Multimap<ItemType, String> battleItems = HashMultimap.create();

    @Key("executions.give_exp.vanilla")
    @Explanation("The vanilla exp to be given later")
    private int vanillaExp;

    @Key("executions.give_exp.battle")
    @Explanation("The Battle exp to be given later")
    private long battleExp;

    @Key("executions.call_function")
    @Explanation("Function to be called when purchased successfully")
    private List<String> callFunction;

    public Product(@NotNull String id) {
        Condition.argNotNull("id", id);
        this.id = id;
    }

    @NotNull
    public String getId() {
        return id;
    }

    @Nullable
    public String getPackageName() {
        return packageName;
    }

    @Nullable
    public Material getPackageMaterial() {
        return packageMaterial;
    }

    @NotNull
    public PreparedItem getIcon() {
        return icon == null ? createIcon() : icon;
    }

    public void setIcon(@NotNull PreparedItem icon) {
        this.icon = icon;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @NotNull
    public CurrencyType getCurrency() {
        return currency;
    }

    public void setCurrency(@NotNull CurrencyType currency) {
        Condition.argNotNull("currency", currency);
        this.currency = currency;
    }

    public boolean isInGameOnly() {
        return inGameOnly;
    }

    public void setInGameOnly(boolean inGameOnly) {
        this.inGameOnly = inGameOnly;
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
    public List<String> getBoosters() {
        return boosters;
    }

    @NotNull
    public PreparedItem[] getVanillaItems() {
        return vanillaItems;
    }

    public void setVanillaItems(@NotNull PreparedItem[] vanillaItems) {
        Condition.argNotNull("vanillaItems", vanillaItems);
        this.vanillaItems = vanillaItems;
    }

    @NotNull
    public Multimap<ItemType, String> getBattleItems() {
        return battleItems;
    }

    public double getVanillaExp() {
        return vanillaExp;
    }

    public void setVanillaExp(int vanillaExp) {
        this.vanillaExp = vanillaExp;
    }

    public double getBattleExp() {
        return battleExp;
    }

    public void setBattleExp(long battleExp) {
        this.battleExp = battleExp;
    }

    @Nullable
    public List<String> getGameModeReserved() {
        return gameModeReserved;
    }

    @Nullable
    public List<String> getCallFunction() {
        return callFunction;
    }

    public void givePlayer(@NotNull Player player, @NotNull PlayerData playerData){
        Location loc = player.getLocation();
        player.getInventory().addItem(CollectionUtil.toArray(Arrays.stream(vanillaItems).map(PreparedItem::build).collect(Collectors.toList()), ItemStack.class)).values().forEach(i -> player.getWorld().dropItemNaturally(loc, i));
        battleItems.forEach((type, x) -> {
            Backpack.Compartment is = playerData.getBackpack().getStorage(type);
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
            playerData.getStats().of(ExpStat.class).increase(player, battleExp);
        }
        for(String booster : boosters){
            playerData.getBoosters().putIfAbsent(booster, System.currentTimeMillis());
        }
    }

    @NotNull
    private PreparedItem createIcon() {
        BattleApi api = BattleApi.getInstance();
        Market market = api.getMarket();
        List<String> perks = getPerks();
        List<String> boosters = getBoosters();
        double battleExp = getBattleExp();
        double vanillaExp = getVanillaExp();
        Multimap<ItemType, String> battleItems = getBattleItems();
        PreparedItem[] vanillaItems = getVanillaItems();

        if(perks.isEmpty() && boosters.isEmpty() && battleExp <= 0 && vanillaExp <= 0){
            if(battleItems.isEmpty() && vanillaItems.length == 0) {
                return icon = market.getDefaultIconForEmptyProduct().duplicate();
            } else if(!market.shouldTreatSingleItemAsPackage()) {
                if (battleItems.size() == 1 && vanillaItems.length == 0) {
                    Map.Entry<ItemType, String> e = battleItems.entries().iterator().next();
                    BattleItemModel bi = api.getItemModel(e.getKey(), e.getValue());
                    if(bi != null) {
                        PreparedItem pi = api.getItemManager().make(bi);
                        if(pi != null) {
                            return icon = pi.duplicate();
                        }
                    }
                } else if (battleItems.isEmpty() && vanillaItems.length == 1) {
                    return icon = vanillaItems[0].duplicate();
                }
            }
        }

        PreparedItem pi = market.getDefaultIconForPackage().duplicate();
        if(packageName != null) {
            pi.name(packageName);
        }
        if(packageMaterial != null) {
            pi.material(packageMaterial);
        }
        PackageDetails details = market.getPackageDetails();
        boolean fe = false;

        if(!battleItems.isEmpty() || vanillaItems.length > 0) {
            pi.lore().add(details.getItemHeader());
            battleItems.forEach((type, _id) -> {
                BattleItemModel bi = api.getItemModel(type, _id);
                if(bi != null) {
                    InfoHolder ih = new InfoHolder("");
                    bi.inform(ih);
                    pi.lore().add(ih.compile().replace(details.getBattleItemFormat()));
                } else {
                    pi.lore().add(new InfoHolder("")
                            .inform("id", _id)
                            .inform("name", _id)
                            .compile().replace(details.getBattleItemFormat()));
                }
            });
            for (PreparedItem i : vanillaItems) {
                String n = i.name();
                if(n == null) {
                    n = i.build().getItemMeta().getLocalizedName();
                }
                pi.lore().add(new InfoHolder("")
                        .inform("name", n)
                        .inform("amount", i.amount())
                        .compile().replace(details.getVanillaItemFormat()));
            }
            fe = true;
        }

        if(!perks.isEmpty()) {
            if(fe && details.shouldSeparatedPartByNewLine()) {
                pi.lore().add(ChatColor.WHITE.toString());
            }
            pi.lore().add(details.getPerkHeader());
            for (String perk : perks) {
                Perk p = api.getPerk(perk);
                if(p == null) {
                    pi.lore().add(new InfoHolder("")
                            .inform("id", perk)
                            .inform("name", perk)
                            .compile().replace(details.getPerkFormat()));
                } else {
                    InfoHolder ih = new InfoHolder("");
                    p.inform(ih);
                    pi.lore().add(ih.compile().replace(details.getPerkFormat()));
                }
            }
            fe = true;
        }

        if(!boosters.isEmpty()) {
            if(fe && details.shouldSeparatedPartByNewLine()) {
                pi.lore().add(ChatColor.WHITE.toString());
            }
            pi.lore().add(details.getBoosterHeader());
            for (String booster : boosters) {
                Booster b = api.getBooster(booster);
                if(b == null) {
                    pi.lore().add(new InfoHolder("")
                            .inform("id", booster)
                            .inform("name", booster)
                            .compile().replace(details.getBoosterFormat()));
                } else {
                    InfoHolder ih = new InfoHolder("");
                    b.inform(ih);
                    pi.lore().add(ih.compile().replace(details.getBoosterFormat()));
                }
            }
            fe = true;
        }

        if(battleExp > 0 || vanillaExp > 0) {
            if(fe && details.shouldSeparatedPartByNewLine()) {
                pi.lore().add(ChatColor.WHITE.toString());
            }
            pi.lore().add(details.getExpHeader());
            if(battleExp > 0) {
                pi.lore().add(new InfoHolder("")
                        .inform("amount", battleExp)
                        .compile().replace(details.getBattleExpFormat()));
            }
            if(vanillaExp > 0) {
                pi.lore().add(new InfoHolder("")
                        .inform("amount", vanillaExp)
                        .compile().replace(details.getVanillaExpFormat()));
            }
        }
        return icon = pi;
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
                .inform("currency", currency.name().toLowerCase())
                .inform("command_count", commands.size())
                .inform("perk_count", perks.size())
                .inform("vanilla_item_count", vanillaItems.length)
                .inform("vanilla_exp_count", vanillaExp)
                .inform("battle_item_count", battleItems.size())
                .inform("battle_exp_count", battleExp);
    }
}
