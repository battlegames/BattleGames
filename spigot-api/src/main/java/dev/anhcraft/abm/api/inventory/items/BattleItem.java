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

import dev.anhcraft.abm.api.misc.info.InfoHolder;
import dev.anhcraft.abm.api.misc.info.Informative;
import dev.anhcraft.craftkit.cb_common.kits.nbt.CompoundTag;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class BattleItem<M extends BattleItemModel> implements Informative {
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<M> model = Optional.empty();

    public Optional<M> getModel() {
        return model;
    }

    public void setModel(@NotNull M model) {
        Validate.notNull(model, "Model must be non-null");
        this.model = Optional.of(model);
    }

    public abstract void save(CompoundTag compound);
    public abstract void load(CompoundTag compound);

    public InfoHolder collectInfo(@Nullable String prefix) {
        if(model.isPresent()) {
            M m = model.get();
            InfoHolder h = new InfoHolder((prefix == null ? "" : prefix) +
                    m.getItemType().name().toLowerCase() + "_")
                    .link(m.collectInfo(prefix));
            inform(h);
            return h;
        }
        return null;
    }
}
