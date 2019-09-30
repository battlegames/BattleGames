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
package dev.anhcraft.abm.api.inventory.items;

import dev.anhcraft.abm.api.misc.Skin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class ScopeModel extends BattleItemModel implements Attachable {
    private Skin skin;
    private List<Integer> zoomLevels;

    public ScopeModel(@NotNull String id, @NotNull ConfigurationSection conf) {
        super(id, conf);

        skin = new Skin(conf.getConfigurationSection("skin"));
        zoomLevels = conf.getIntegerList("zoom_levels");
        zoomLevels.removeIf(integer -> {
            boolean b = integer < 1 || integer > 255;
            if(b) Bukkit.getLogger().warning(String.format("Removed invalid zoom level `%s` in scope `%s`", integer, id));
            return b;
        });
        zoomLevels = Collections.unmodifiableList(zoomLevels);
    }

    @NotNull
    public Skin getSkin() {
        return skin;
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
}
