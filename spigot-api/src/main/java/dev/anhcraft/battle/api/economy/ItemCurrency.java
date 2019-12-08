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

package dev.anhcraft.battle.api.economy;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

public abstract class ItemCurrency implements Currency {
    @NotNull
    public abstract Material getItemType();

    @Override
    public double getBalance(@NotNull Player player) {
        return Arrays.stream(player.getInventory().getContents()).filter(Objects::nonNull).filter(i -> i.getType() == getItemType()).mapToInt(ItemStack::getAmount).sum();
    }

    @Override
    public boolean withdraw(@NotNull Player player, double delta) {
        int remain = (int) delta;
        int index = 0;
        while (player.getInventory().getSize() > index){
            ItemStack item = player.getInventory().getItem(index++);
            if(item != null && item.getType() == getItemType()){
                int current = item.getAmount();
                int del = Math.min(current, remain);
                item.setAmount(current - del);
                remain -= del;
                if(remain == 0) break;
            }
        }
        return remain == 0;
    }

    @Override
    public boolean deposit(@NotNull Player player, double delta) {
        int remain = (int) delta;
        while (remain > 0){
            int st = Math.min(remain, getItemType().getMaxStackSize());
            player.getInventory().addItem(new ItemStack(getItemType(), st));
            remain -= st;
        }
        return true;
    }
}
