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
package dev.anhcraft.abm.api;

import dev.anhcraft.abm.api.inventory.items.BattleItem;
import dev.anhcraft.abm.api.inventory.items.BattleItemModel;
import dev.anhcraft.craftkit.kits.abif.PreparedItem;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface BattleItemManager {
    @Nullable <R extends BattleItemModel> PreparedItem make(@Nullable BattleItem<R> battleItem);
    @Nullable <R extends BattleItemModel> PreparedItem make(@Nullable BattleItem<R> battleItem, @Nullable Map<String, String> addition);
    @Nullable PreparedItem make(@Nullable BattleItemModel bim);
    @Nullable PreparedItem make(@Nullable BattleItemModel bim, @Nullable Map<String, String> addition);
    @Nullable BattleItem read(@Nullable ItemStack itemStack);
    @Nullable ItemStack write(@Nullable ItemStack itemStack, @Nullable BattleItem<?> battleItem);
}
