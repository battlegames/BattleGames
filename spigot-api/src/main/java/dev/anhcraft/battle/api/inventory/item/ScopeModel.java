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
package dev.anhcraft.battle.api.inventory.item;

import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.annotation.*;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Schema
public class ScopeModel extends SingleSkinItem implements Attachable {
    public static final ConfigSchema<ScopeModel> SCHEMA = ConfigSchema.of(ScopeModel.class);

    @Key("zoom_levels")
    @Explanation({
            "Set the zoom levels",
            "The zoom level must be between 1 and 255",
            "Players can turn to the next level by",
            "click the zoom button, when none level",
            "remains, the view returns to normal"
    })
    @IgnoreValue(ifNull = true)
    private List<Integer> zoomLevels = new ArrayList<>();

    public ScopeModel(@NotNull String id) {
        super(id);
    }

    @NotNull
    public List<Integer> getZoomLevels() {
        return zoomLevels;
    }

    @Override
    public ItemType[] getHolderTypes() {
        return new ItemType[]{
                ItemType.GUN
        };
    }

    @Override
    public @NotNull ItemType getItemType() {
        return ItemType.SCOPE;
    }

    @Middleware(Middleware.Direction.CONFIG_TO_SCHEMA)
    private Object handle(ConfigSchema.Entry entry, Object value){
        if(value != null && entry.getKey().equals("zoom_levels")){
            List<Integer> x = (List<Integer>) value;
            x.removeIf(integer -> {
                boolean b = integer < 1 || integer > 255;
                if(b) Bukkit.getLogger().warning(String.format("Removed invalid zoom level `%s` in scope `%s`", integer, getId()));
                return b;
            });
            return x;
        }
        return value;
    }
}
