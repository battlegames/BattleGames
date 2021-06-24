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

import com.google.common.collect.Multimap;
import dev.anhcraft.battle.ApiProvider;
import dev.anhcraft.battle.api.BattleApi;
import dev.anhcraft.battle.api.Booster;
import dev.anhcraft.battle.api.Perk;
import dev.anhcraft.battle.api.economy.CurrencyType;
import dev.anhcraft.battle.api.inventory.Backpack;
import dev.anhcraft.battle.api.inventory.item.BattleItemModel;
import dev.anhcraft.battle.api.inventory.item.ItemType;
import dev.anhcraft.battle.api.stats.natives.ExpStat;
import dev.anhcraft.battle.api.storage.data.PlayerData;
import dev.anhcraft.battle.impl.Informative;
import dev.anhcraft.battle.utils.PlaceholderUtil;
import dev.anhcraft.battle.utils.PreparedItem;
import dev.anhcraft.battle.utils.info.InfoHolder;
import dev.anhcraft.config.annotations.*;
import dev.anhcraft.jvmkit.utils.CollectionUtil;
import dev.anhcraft.jvmkit.utils.Condition;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("FieldMayBeFinal")
@Configurable
public class Product implements Informative {
    public static final PreparedItem DEFAULT_ICON = new PreparedItem();

    static {
        DEFAULT_ICON.material(Material.STONE);
    }

    private final String id;

    @Setting
    @Description("Product's icon")
    private PreparedItem icon;

    @Setting
    @Path("package_name")
    @Description({
            "A nice name for the package icon.",
            "This product is 'a package' when it gives player booster, perks, exp or items",
            "(for items, <b>requires the amount of two or more</b>). You can read more",
            "at the Market schema (option 'default_product_icon.package')",
            "The icon for a package will be created automatically when no icon is set"
    })
    private String packageName;

    @Setting
    @Path("package_material")
    @Description({
            "The material for the package icon",
            "<i>What is a package?</i> Read above (option 'package_name')"
    })
    private Material packageMaterial;

    @Setting
    @Description("The currency to be used")
    private CurrencyType currency = CurrencyType.VAULT;

    @Setting
    @Description("The cost of this product")
    private double price;

    @Setting
    @Path("in_game_only")
    @Description("Make this product only available during the game")
    private boolean inGameOnly;

    @Setting
    @Path("reserved_game_modes")
    @Description({
            "Make this product only available during certain game modes",
            "This option only takes effect if <b>in_game_only</b> set to <i>true</i>",
            "All game modes are non-case-sensitive"
    })
    private List<String> gameModeReserved;

    @Setting
    @Path("executions.perform_commands")
    @Description({
            "The commands to be performed by the console later",
            "You can use placeholders here; they are parsed",
            "from the buyer's info"
    })
    @Validation(notNull = true, silent = true)
    private List<String> commands = new ArrayList<>();

    @Setting
    @Path("executions.give_perks")
    @Description("The perks to be given later")
    @Validation(notNull = true, silent = true)
    private List<String> perks = new ArrayList<>();

    @Setting
    @Path("executions.give_boosters")
    @Description("The boosters to be given later")
    @Validation(notNull = true, silent = true)
    private List<String> boosters = new ArrayList<>();

    @Setting
    @Path("executions.give_items.vanilla")
    @Description("The vanilla items to be given later")
    @Validation(notNull = true, silent = true)
    @Example({
            "executions:",
            "  give_items:",
            "    vanilla:",
            "      '1':",
            "        material: cookie",
            "        amount: 16"
    })
    private Map<String, PreparedItem> vanillaItems = new HashMap<>();

    @Setting
    @Path("executions.give_items.battle")
    @Description("The Battle items to be given later")
    @Example({
            "executions:",
            "  give_items:",
            "    battle:",
            "      gun: # gun, ammo, magazine, scope, grenade",
            "      - ak_47",
    })
    private Multimap<ItemType, String> battleItems;

    @Setting
    @Path("executions.give_exp.vanilla")
    @Description("The vanilla exp to be given later")
    private int vanillaExp;

    @Setting
    @Path("executions.give_exp.battle")
    @Description("The Battle exp to be given later")
    private long battleExp;

    @Setting
    @Path("functions.on_purchase")
    @Description({
            "Function to be called when a player purchases this product, and the transaction",
            "has not been created (no money is taken, nothing is given to the player)",
            "This function is very useful that allows you to change the cost. For example,",
            "the cost can be reduced by half if the player has a specific rank. Or you can",
            "make a 'tier cost' system that increase the cost each time the player purchases",
            "so the first time is &#36;10, then &#36;20, &#36;50, &#36;100. etc",
            "You can also reject the transaction by setting &#36;forbidden to `true`",
            "Variables: &#36;price, &#36;currency, &#36;forbidden (modifiable)"
    })
    private List<String> purchaseFunction;

    @Setting
    @Path("functions.on_purchased")
    @Description({
            "Function to be called when a player purchased this product successfully",
            "Variables: &#36;price, &#36;currency (read-only)"
    })
    private List<String> purchasedFunction;

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
        // TODO temp fix
        if(commands == null) return Collections.emptyList();
        return commands;
    }

    @NotNull
    public List<String> getPerks() {
        // TODO temp fix
        if(perks == null) return Collections.emptyList();
        return perks;
    }

    @NotNull
    public List<String> getBoosters() {
        // TODO temp fix
        if(boosters == null) return Collections.emptyList();
        return boosters;
    }

    @NotNull
    public Collection<PreparedItem> getVanillaItems() {
        // TODO temp fix
        if(vanillaItems == null) return Collections.emptyList();
        return vanillaItems.values();
    }

    @Nullable
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
    public List<String> getPurchaseFunction() {
        return purchaseFunction;
    }

    @Nullable
    public List<String> getPurchasedFunction() {
        return purchasedFunction;
    }

    public void givePlayer(@NotNull Player player, @NotNull PlayerData playerData) {
        Location loc = player.getLocation();
        player.getInventory().addItem(CollectionUtil.toArray(getVanillaItems().stream().map(PreparedItem::build).collect(Collectors.toList()), ItemStack.class)).values().forEach(i -> player.getWorld().dropItemNaturally(loc, i));
        if (battleItems != null) {
            battleItems.forEach((type, x) -> {
                Backpack.Compartment is = playerData.getBackpack().getStorage(type);
                is.put(x);
            });
        }
        for (String perk : getPerks()) {
            Perk p = ApiProvider.consume().getPerk(perk);
            if (p != null) p.give(player);
        }
        for (String cmd : getCommands()) {
            cmd = PlaceholderUtil.formatPAPI(player, cmd);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        }
        if (vanillaExp > 0) {
            player.giveExp(vanillaExp);
        }
        if (battleExp > 0) {
            playerData.getStats().of(ExpStat.class).increase(player, battleExp);
        }
        for (String booster : getBoosters()) {
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
        Collection<PreparedItem> vanillaItems = getVanillaItems();

        if (perks.isEmpty() && boosters.isEmpty() && battleExp <= 0 && vanillaExp <= 0) {
            if ((battleItems == null || battleItems.isEmpty()) && vanillaItems.isEmpty()) {
                return icon = market.getDefaultIconForEmptyProduct().duplicate();
            } else if (!market.shouldTreatSingleItemAsPackage()) {
                if (battleItems != null && battleItems.size() == 1 && vanillaItems.isEmpty()) {
                    Map.Entry<ItemType, String> e = battleItems.entries().iterator().next();
                    BattleItemModel bi = api.getItemModel(e.getKey(), e.getValue());
                    if (bi != null) {
                        PreparedItem pi = api.getItemManager().make(bi);
                        if (pi != null) {
                            return icon = pi.duplicate();
                        }
                    }
                } else if ((battleItems == null || battleItems.isEmpty()) && vanillaItems.size() == 1) {
                    return icon = vanillaItems.stream().findFirst().get().duplicate();
                }
            }
        }

        PreparedItem pi = market.getDefaultIconForPackage().duplicate();
        if (packageName != null) {
            pi.name(packageName);
        }
        if (packageMaterial != null) {
            pi.material(packageMaterial);
        }
        PackageDetails details = market.getPackageDetails();
        boolean fe = false;

        if ((battleItems != null && !battleItems.isEmpty()) || !vanillaItems.isEmpty()) {
            pi.lore().add(details.getItemHeader());
            if (battleItems != null) {
                battleItems.forEach((type, _id) -> {
                    BattleItemModel bi = api.getItemModel(type, _id);
                    if (bi != null) {
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
            }
            for (PreparedItem i : vanillaItems) {
                String n = i.name();
                if (n == null) {
                    ItemMeta meta = i.build().getItemMeta();
                    if (meta != null) {
                        n = meta.getLocalizedName();
                    }
                }
                if (n == null || n.isEmpty()) {
                    String k = "item.minecraft." + i.material().name().toLowerCase();
                    if (api.getMinecraftLocale().has(k)) {
                        n = api.getMinecraftLocale().get(k).getAsString();
                    }
                }
                pi.lore().add(new InfoHolder("")
                        .inform("name", n == null ? "" : n)
                        .inform("amount", i.amount())
                        .compile().replace(details.getVanillaItemFormat()));
            }
            fe = true;
        }

        if (!perks.isEmpty()) {
            if (fe && details.shouldSeparatedPartByNewLine()) {
                pi.lore().add(ChatColor.WHITE.toString());
            }
            pi.lore().add(details.getPerkHeader());
            for (String perk : perks) {
                Perk p = api.getPerk(perk);
                if (p == null) {
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

        if (!boosters.isEmpty()) {
            if (fe && details.shouldSeparatedPartByNewLine()) {
                pi.lore().add(ChatColor.WHITE.toString());
            }
            pi.lore().add(details.getBoosterHeader());
            for (String booster : boosters) {
                Booster b = api.getBooster(booster);
                if (b == null) {
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

        if (battleExp > 0 || vanillaExp > 0) {
            if (fe && details.shouldSeparatedPartByNewLine()) {
                pi.lore().add(ChatColor.WHITE.toString());
            }
            pi.lore().add(details.getExpHeader());
            if (battleExp > 0) {
                pi.lore().add(new InfoHolder("")
                        .inform("amount", battleExp)
                        .compile().replace(details.getBattleExpFormat()));
            }
            if (vanillaExp > 0) {
                pi.lore().add(new InfoHolder("")
                        .inform("amount", vanillaExp)
                        .compile().replace(details.getVanillaExpFormat()));
            }
        }
        return icon = pi;
    }

    @Override
    public void inform(@NotNull InfoHolder holder) {
        holder.inform("id", id)
                .inform("price", price)
                .inform("currency", currency.name().toLowerCase())
                .inform("command_count", getCommands().size())
                .inform("perk_count", getPerks().size())
                .inform("vanilla_item_count", getVanillaItems().size())
                .inform("vanilla_exp_count", vanillaExp)
                .inform("battle_item_count", battleItems == null ? 0 : battleItems.size())
                .inform("battle_exp_count", battleExp);
    }
}
