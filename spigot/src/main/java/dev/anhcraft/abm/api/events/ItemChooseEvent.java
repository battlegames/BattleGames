package dev.anhcraft.abm.api.events;

import dev.anhcraft.abm.api.ext.BattleItemModel;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemChooseEvent extends Event {
    public static final HandlerList handlers = new HandlerList();
    private Player player;
    private ItemStack itemStack;
    private BattleItemModel itemModel;

    public ItemChooseEvent(Player player, ItemStack itemStack, BattleItemModel itemModel) {
        this.player = player;
        this.itemStack = itemStack;
        this.itemModel = itemModel;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public BattleItemModel getItemModel() {
        return itemModel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
