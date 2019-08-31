package dev.anhcraft.abm.api.inventory.items;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ItemTag {
    public static String ITEM_TYPE;
    public static String AMMO_ID;
    public static String GUN_ID;
    public static String GUN_MAGAZINE;
    public static String GUN_SCOPE;
    public static String MAGAZINE_ID;
    public static String MAGAZINE_AMMO_COUNT;
    public static String MAGAZINE_AMMO;
    public static String SCOPE_ID;
    public static String SCOPE_NEXT_ZOOM_LEVEL;

    static {
        Field[] fields = ItemTag.class.getDeclaredFields();
        for(Field field : fields){
            if(Modifier.isStatic(field.getModifiers()) && field.getType() == String.class) {
                try {
                    field.set(null, field.getName().toLowerCase());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
