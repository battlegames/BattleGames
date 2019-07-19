package dev.anhcraft.abm.api.objects;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Skin {
    private Material material;
    private int damage;

    public Skin(@Nullable Material material, int damage) {
        this.material = (material == null ? Material.AIR : material);
        this.damage = damage;
    }

    @NotNull
    public Material getMaterial() {
        return material;
    }

    public int getDamage() {
        return damage;
    }

    @NotNull
    public ItemStack getItem(int amount){
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta;
        if((meta = item.getItemMeta()) instanceof Damageable) {
            ((Damageable) meta).setDamage(damage);
            meta.setUnbreakable(true);
        }
        item.setItemMeta(meta);
        return item;
    }
}
