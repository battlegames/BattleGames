package dev.anhcraft.abm.api;

import dev.anhcraft.abif.PreparedItem;
import dev.anhcraft.abm.api.inventory.items.BattleItem;
import dev.anhcraft.abm.api.inventory.items.BattleItemModel;
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
