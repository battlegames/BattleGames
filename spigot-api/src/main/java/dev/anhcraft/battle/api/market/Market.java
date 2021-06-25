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

import dev.anhcraft.battle.utils.PreparedItem;
import dev.anhcraft.config.ConfigDeserializer;
import dev.anhcraft.config.annotations.*;
import dev.anhcraft.config.schema.ConfigSchema;
import dev.anhcraft.config.schema.SchemaScanner;
import dev.anhcraft.config.struct.ConfigSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("FieldMayBeFinal")
@Configurable
public class Market {
    @Setting
    @Path("log_transactions")
    @Description("Should we log the transactions made by players")
    private boolean logTransactions;

    @Setting
    @Path("product_lore_footer.enabled")
    @Description("Should we add a nice footer to the lore of every product?")
    private boolean productLoreFooterEnabled;

    @Setting
    @Path("product_lore_footer.content")
    @Description("Content of the footer")
    private List<String> productLoreFooterContent;

    @Setting
    @Path("transaction_item")
    @Description("The item to be displayed for each transaction in the transaction menu")
    private PreparedItem transactionItem;

    @Setting
    @Path("default_product_icon.empty")
    @Description({
            "<b>Default</b> icon for empty products",
            "A product is considered as <i>'empty'</i> when it has nothing to benefit the player",
            "in other words: <b>no exp points, no items are given, no perks and no boosters.</b>"
    })
    @Validation(notNull = true)
    private PreparedItem defaultIconForEmptyProduct;

    @Setting
    @Path("default_product_icon.package")
    @Description({
            "<b>Default</b> icon for packages",
            "A package is a product that gives player booster, perks, exp or items",
            "(for items, <b>requires the amount of two or more</b>)",
            "<i>To change the package name, you can set the option `package_name` that",
            "can be seen more in the Product schema.</i>",
            "A product with one item only will not be considered as a package, the default",
            "icon for that product is the item. However, you can change that by looking at",
            "the below option ('default_product_icon.single_item_package')"
    })
    @Validation(notNull = true)
    private PreparedItem defaultIconForPackage;

    @Setting
    @Path("default_product_icon.package_details")
    @Description("Details for the default icon for packages")
    @Validation(notNull = true)
    private PackageDetails packageDetails;

    @Setting
    @Path("default_product_icon.single_item_package")
    @Description({
            "Should we treat single item as 'package'",
            "As explained above, if a product only gives",
            "the player one item, the default icon will be",
            "that item. Set this option to true will make",
            "it rendered as a package."
    })
    private boolean treatSingleItemAsPackage;

    @Setting
    @Description("All categories")
    @Validation(notNull = true, silent = true)
    @Consistent
    @Example({
            "categories:",
            "  perks:",
            "    icon:",
            "      material: emerald",
            "      name: \"&b&lPerk shop\"",
            "      lore:",
            "        - \"&fHere you can buy perks with&a cheap price\"",
            "      enchant:",
            "        unbreaking: 1",
            "      flag:",
            "        - hide_enchants",
            "    products:",
            "      '1':",
            "        package_name: \"&cHealth boost\"",
            "        package_material: redstone",
            "        currency: vault",
            "        price: 75",
            "        executions:",
            "          give_perks:",
            "            - health_boost"
    })
    private Map<String, Category> categories = new HashMap<>();

    public boolean shouldLogTransactions() {
        return logTransactions;
    }

    public void setLogTransactions(boolean logTransactions) {
        this.logTransactions = logTransactions;
    }

    public boolean isProductLoreFooterEnabled() {
        return productLoreFooterEnabled;
    }

    public void setProductLoreFooterEnabled(boolean productLoreFooterEnabled) {
        this.productLoreFooterEnabled = productLoreFooterEnabled;
    }

    @Nullable
    public List<String> getProductLoreFooterContent() {
        return productLoreFooterContent;
    }

    @NotNull
    public Collection<Category> getCategories() {
        return categories.values();
    }

    @Nullable
    public PreparedItem getTransactionItem() {
        return transactionItem;
    }

    @NotNull
    public PreparedItem getDefaultIconForEmptyProduct() {
        return defaultIconForEmptyProduct;
    }

    @NotNull
    public PreparedItem getDefaultIconForPackage() {
        return defaultIconForPackage;
    }

    @NotNull
    public PackageDetails getPackageDetails() {
        return packageDetails;
    }

    public boolean shouldTreatSingleItemAsPackage() {
        return treatSingleItemAsPackage;
    }

    @PostHandler
    private void handle(ConfigDeserializer deserializer, ConfigSchema schema, ConfigSection section){
        try {
            ConfigSection cs = section.get("categories").asSection();
            for(String s : cs.getKeys(false)){
                categories.put(s, deserializer.transformConfig(SchemaScanner.scanConfig(Category.class), cs.get(s).asSection(), new Category(s)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
