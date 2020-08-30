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

import dev.anhcraft.craftkit.abif.PreparedItem;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface ItemManager {
    /**
     * Makes a new item from {@link BattleItem}
     *
     * @param battleItem the Battle item
     * @param <R>        the item's model type
     * @return {@link PreparedItem} if created successfully or null if not
     */
    @Nullable <R extends BattleItemModel> PreparedItem make(@Nullable BattleItem<R> battleItem);

    /**
     * Makes a new item from {@link BattleItem}
     *
     * @param battleItem the Battle item
     * @param addition   additional information map for item's name and its lore
     * @param <R>        the item's model type
     * @return {@link PreparedItem} if created successfully or null if not
     */
    @Nullable <R extends BattleItemModel> PreparedItem make(@Nullable BattleItem<R> battleItem, @Nullable Map<String, String> addition);

    /**
     * Makes a new item from {@link BattleItemModel}
     *
     * @param bim the item model
     * @return {@link PreparedItem} if created successfully or null if not
     */
    @Nullable
    PreparedItem make(@Nullable BattleItemModel bim);

    /**
     * Makes a new item from {@link BattleItemModel}
     *
     * @param bim      the item model
     * @param addition additional information map for item's name and its lore
     * @return {@link PreparedItem} if created successfully or null if not
     */
    @Nullable
    PreparedItem make(@Nullable BattleItemModel bim, @Nullable Map<String, String> addition);

    /**
     * Reads the data of the given item and receives {@link BattleItem}.
     *
     * @param itemStack the item stack
     * @return {@link BattleItem} if read successfully or null if not
     */
    @Nullable
    BattleItem read(@Nullable ItemStack itemStack);

    /**
     * Writes the data of {@link BattleItem} to the given item stack.
     *
     * @param itemStack  the item stack
     * @param battleItem the Battle item
     * @return {@link ItemStack} if wrote successfully or null if not
     */
    @Nullable
    ItemStack write(@Nullable ItemStack itemStack, @Nullable BattleItem<?> battleItem);
}
