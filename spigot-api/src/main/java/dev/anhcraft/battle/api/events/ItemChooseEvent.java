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
package dev.anhcraft.battle.api.events;

import dev.anhcraft.battle.api.gui.SlotReport;
import dev.anhcraft.battle.api.inventory.item.BattleItemModel;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemChooseEvent extends PlayerEvent {
    public static final HandlerList handlers = new HandlerList();
    private final SlotReport slotReport;
    private final ItemStack itemStack;
    private final BattleItemModel itemModel;

    public ItemChooseEvent(@NotNull Player player, @NotNull SlotReport slotReport, @NotNull ItemStack itemStack, @NotNull BattleItemModel itemModel) {
        super(player);
        this.slotReport = slotReport;
        this.player = player;
        this.itemStack = itemStack;
        this.itemModel = itemModel;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @NotNull
    public SlotReport getSlotReport() {
        return slotReport;
    }

    @NotNull
    public ItemStack getItemStack() {
        return itemStack;
    }

    @NotNull
    public BattleItemModel getItemModel() {
        return itemModel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
