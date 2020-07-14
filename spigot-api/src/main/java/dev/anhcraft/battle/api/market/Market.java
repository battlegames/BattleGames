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

import dev.anhcraft.battle.utils.ConfigurableObject;
import dev.anhcraft.confighelper.ConfigHelper;
import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.annotation.Explanation;
import dev.anhcraft.confighelper.annotation.IgnoreValue;
import dev.anhcraft.confighelper.annotation.Key;
import dev.anhcraft.confighelper.annotation.Schema;
import dev.anhcraft.confighelper.exception.InvalidValueException;
import dev.anhcraft.craftkit.abif.PreparedItem;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldMayBeFinal")
@Schema
public class Market extends ConfigurableObject {
    public static final ConfigSchema<Market> SCHEMA = ConfigSchema.of(Market.class);

    @Key("options.log_transactions")
    private boolean logTransactions;

    @Key("options.summary_product_info.enabled")
    @Explanation("Should we summarize the details of each product")
    private boolean productLoreFooterEnabled;

    @Key("options.summary_product_info.lore")
    @Explanation("Additional lore that contains common stuff about the product (e.g: price)")
    private List<String> productLoreFooter;

    @Key("options.transaction_item")
    @Explanation("The item to be displayed for each transaction in the transaction menu")
    private PreparedItem transactionItem;

    @Key("categories")
    @Explanation("All categories")
    @IgnoreValue(ifNull = true)
    private List<Category> categories = new ArrayList<>();

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
    public List<String> getProductLoreFooter() {
        return productLoreFooter;
    }

    @NotNull
    public List<Category> getCategories() {
        return categories;
    }

    @Nullable
    public PreparedItem getTransactionItem() {
        return transactionItem;
    }

    @Override
    protected @Nullable Object conf2schema(@Nullable Object value, ConfigSchema.Entry entry) {
        if(value != null && entry.getKey().equals("categories")){
            ConfigurationSection cs = (ConfigurationSection) value;
            List<Category> ctgs = new ArrayList<>();
            for(String s : cs.getKeys(false)){
                try {
                    ConfigurationSection scs = cs.getConfigurationSection(s);
                    Category ctg = ConfigHelper.readConfig(scs, Category.SCHEMA, new Category(s));
                    ctgs.add(ctg);
                } catch (InvalidValueException e) {
                    e.printStackTrace();
                }
            }
            return ctgs;
        }
        return value;
    }

    @Override
    protected @Nullable Object schema2conf(@Nullable Object value, ConfigSchema.Entry entry) {
        if(value != null && entry.getKey().equals("categories")){
            ConfigurationSection parent = new YamlConfiguration();
            for(Category ctg : (List<Category>) value){
                YamlConfiguration c = new YamlConfiguration();
                ConfigHelper.writeConfig(c, Category.SCHEMA, ctg);
                parent.set(ctg.getId(), c);
            }
            return parent;
        }
        return value;
    }
}
