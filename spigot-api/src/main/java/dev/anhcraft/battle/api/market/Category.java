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

import dev.anhcraft.battle.api.misc.ConfigurableObject;
import dev.anhcraft.confighelper.ConfigHelper;
import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.annotation.Explanation;
import dev.anhcraft.confighelper.annotation.IgnoreValue;
import dev.anhcraft.confighelper.annotation.Key;
import dev.anhcraft.confighelper.annotation.Schema;
import dev.anhcraft.confighelper.exception.InvalidValueException;
import dev.anhcraft.craftkit.abif.PreparedItem;
import dev.anhcraft.jvmkit.utils.Condition;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Schema
public class Category extends ConfigurableObject {
    private static final PreparedItem DEFAULT_ICON = new PreparedItem();
    public static final ConfigSchema<Category> SCHEMA = ConfigSchema.of(Category.class);

    static {
        DEFAULT_ICON.material(Material.STONE);
    }

    private String id;

    @Key("icon")
    @Explanation("Category's icon")
    @IgnoreValue(ifNull = true)
    private PreparedItem icon = DEFAULT_ICON.duplicate();

    @Key("in_game_only")
    @Explanation("Make this category only available during the game")
    private boolean inGameOnly;

    @Key("products")
    @Explanation("Products in this category")
    private List<Product> products = new ArrayList<>();

    public Category(@NotNull String id) {
        Condition.argNotNull("id", id);
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

    public void setIcon(@NotNull PreparedItem icon) {
        Condition.argNotNull("icon", icon);
        this.icon = icon;
    }

    @NotNull
    public List<Product> getProducts() {
        return products;
    }

    public boolean isInGameOnly() {
        return inGameOnly;
    }

    public void setInGameOnly(boolean inGameOnly) {
        this.inGameOnly = inGameOnly;
    }

    @Override
    protected @Nullable Object conf2schema(@Nullable Object value, ConfigSchema.Entry entry) {
        if(value != null && entry.getKey().equals("products")){
            ConfigurationSection cs = (ConfigurationSection) value;
            List<Product> products = new ArrayList<>();
            for(String s : cs.getKeys(false)){
                try {
                    ConfigurationSection scs = cs.getConfigurationSection(s);
                    Product p = ConfigHelper.readConfig(scs, Product.SCHEMA, new Product(s));
                    products.add(p);
                } catch (InvalidValueException e) {
                    e.printStackTrace();
                }
            }
            return products;
        }
        return value;
    }

    @Override
    protected @Nullable Object schema2conf(@Nullable Object value, ConfigSchema.Entry entry) {
        if(value != null && entry.getKey().equals("products")){
            ConfigurationSection parent = new YamlConfiguration();
            for(Product p : (List<Product>) value){
                YamlConfiguration c = new YamlConfiguration();
                ConfigHelper.writeConfig(c, Product.SCHEMA, p);
                parent.set(p.getId(), c);
            }
            return parent;
        }
        return value;
    }
}
