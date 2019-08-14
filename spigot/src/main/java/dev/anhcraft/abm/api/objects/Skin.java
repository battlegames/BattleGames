package dev.anhcraft.abm.api.objects;

import dev.anhcraft.abif.PreparedItem;
import org.bukkit.Material;
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

    public PreparedItem transform(PreparedItem preparedItem){
        preparedItem.material(material);
        preparedItem.damage(damage);
        return preparedItem;
    }
}
