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

import dev.anhcraft.abm.api.misc.info.Informative;
import dev.anhcraft.abm.api.misc.info.InfoHolder;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BattleItemModel implements Informative {
    private String id;
    private String name;
    private InfoHolder cachedInfoHolder;

    protected BattleItemModel(@NotNull String id, @NotNull ConfigurationSection conf) {
        Validate.notNull(id, "Id must be non-null");
        Validate.isTrue(id.matches("[A-Za-z0-9_]+"), "Id must only contains A-Z,a-z, 0-9 and underscore only");
        Validate.notNull(conf, "Conf must be non-null");

        this.id = id;
        this.name = conf.getString("name", id);
    }

    @NotNull
    public abstract ItemType getItemType();

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @Override
    public void inform(@NotNull InfoHolder holder){
        holder.inform("id", id).inform("name", name);
    }

    public synchronized InfoHolder collectInfo(@Nullable String prefix) {
        if(cachedInfoHolder != null) return cachedInfoHolder;
        inform(cachedInfoHolder = new InfoHolder((prefix == null ? "" : prefix) +
                getItemType().name().toLowerCase() + "_"));
        return cachedInfoHolder;
    }
}
