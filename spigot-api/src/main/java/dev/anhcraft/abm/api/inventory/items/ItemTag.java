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
package dev.anhcraft.abm.api.inventory.items;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ItemTag {
    public static String ITEM_TYPE;
    public static String AMMO_ID;
    public static String GUN_ID;
    public static String GUN_MAGAZINE;
    public static String GUN_SCOPE;
    public static String GUN_NEXT_SPRAY;
    public static String GUN_LAST_SPRAY_TIME;
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
