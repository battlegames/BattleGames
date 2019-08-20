package dev.anhcraft.abm.api.inventory.items;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ItemTag {
    public static NamespacedKey ITEM_TYPE;
    public static NamespacedKey AMMO_ID;
    public static NamespacedKey GUN_ID;
    public static NamespacedKey GUN_MAGAZINE;
    public static NamespacedKey MAGAZINE_ID;
    public static NamespacedKey MAGAZINE_AMMO_COUNT;
    public static NamespacedKey MAGAZINE_AMMO;

    private static boolean init;

    public static void init(Plugin plugin){
        if(init) return;
        Field[] fields = ItemTag.class.getDeclaredFields();
        for(Field field : fields){
            if(Modifier.isStatic(field.getModifiers())) {
                try {
                    field.set(null, new NamespacedKey(plugin, field.getName().toLowerCase()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        init = true;
    }
}
