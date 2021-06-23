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
import dev.anhcraft.jvmkit.utils.Condition;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("FieldMayBeFinal")
@Configurable
public class Category {
    private static final PreparedItem DEFAULT_ICON = new PreparedItem();

    static {
        DEFAULT_ICON.material(Material.STONE);
    }

    private final String id;

    @Setting
    @Description("Category's icon")
    @Validation(notNull = true, silent = true)
    private PreparedItem icon = DEFAULT_ICON.duplicate();

    @Setting
    @Path("in_game_only")
    @Description("Make this category only available during the game")
    private boolean inGameOnly;

    @Setting
    @Path("reserved_game_modes")
    @Description({
            "Make this category only available during certain game modes",
            "This option only takes effect if <b>in_game_only</b> set to <i>true</i>",
            "All game modes are non-case-sensitive"
    })
    private List<String> gameModeReserved;

    @Setting
    @Description("Products in this category")
    @Example({
            "products:",
            "  '1':",
            "    icon:",
            "    name: \"&c&lApple\"",
            "    material: apple"
    })
    @Consistent
    private Map<String, Product> products = new HashMap<>();

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
    public Collection<Product> getProducts() {
        return products.values();
    }

    public boolean isInGameOnly() {
        return inGameOnly;
    }

    public void setInGameOnly(boolean inGameOnly) {
        this.inGameOnly = inGameOnly;
    }

    @Nullable
    public List<String> getGameModeReserved() {
        return gameModeReserved;
    }

    @PostHandler
    private void handle(ConfigDeserializer deserializer, ConfigSchema schema, ConfigSection section){
        try {
            ConfigSection cs = section.get("products").asSection();
            for(String s : cs.getKeys(false)){
                products.put(s, deserializer.transformConfig(SchemaScanner.scanConfig(Product.class), cs.get(s).asSection(), new Product(s)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
