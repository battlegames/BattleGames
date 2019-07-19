package dev.anhcraft.abm.system.handlers;

import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.objects.Skin;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

abstract class ItemHandler extends Handler {
    ItemHandler(BattlePlugin plugin) {
        super(plugin);
    }

    @Nullable
    public ItemStack draw(Skin s, @Nullable ItemStack itemStack){
        if(itemStack == null) return null;
        itemStack.setType(s.getMaterial());
        ItemMeta meta = itemStack.getItemMeta();
        if(meta instanceof Damageable) {
            ((Damageable) meta).setDamage(s.getDamage());
            itemStack.setItemMeta(meta);
        }
        return itemStack;
    }

    @Nullable
    public ItemStack draw(Skin s, @Nullable ItemStack itemStack, List<String> headerLore, List<String> footerLore){
        if(itemStack == null) return null;
        itemStack.setType(s.getMaterial());
        ItemMeta meta = itemStack.getItemMeta();
        if(meta != null) {
            if(meta instanceof Damageable) ((Damageable) meta).setDamage(s.getDamage());
            List<String> lore = meta.getLore();
            if (lore == null) lore = new LinkedList<>();
            else lore = new LinkedList<>(lore);
            lore.addAll(0, headerLore);
            lore.addAll(footerLore);
            meta.setLore(lore);
            itemStack.setItemMeta(meta);
        }
        return itemStack;
    }
}
