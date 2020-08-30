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

import dev.anhcraft.battle.impl.Informative;
import dev.anhcraft.battle.utils.ConfigurableObject;
import dev.anhcraft.battle.utils.info.InfoHolder;
import dev.anhcraft.confighelper.annotation.Explanation;
import dev.anhcraft.confighelper.annotation.IgnoreValue;
import dev.anhcraft.confighelper.annotation.Key;
import dev.anhcraft.confighelper.annotation.Schema;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("FieldMayBeFinal")
@Schema
public abstract class BattleItemModel extends ConfigurableObject implements Informative {
    private final String id;

    @Key("name")
    @Explanation("Give a name for the item")
    @IgnoreValue(ifNull = true)
    private String name;

    protected BattleItemModel(@NotNull String id) {
        Validate.notNull(id, "Id must be non-null");
        Validate.isTrue(id.matches("[A-Za-z0-9_]+"), "Id must only contains A-Z,a-z, 0-9 and underscore only");
        this.id = id;
        name = id;
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
    public void inform(@NotNull InfoHolder holder) {
        holder.inform("id", id).inform("name", name);
    }

    @NotNull
    public InfoHolder collectInfo(@Nullable String prefix) {
        InfoHolder ih = new InfoHolder((prefix == null ? "" : prefix) + getItemType().name().toLowerCase() + "_");
        inform(ih);
        return ih;
    }
}
