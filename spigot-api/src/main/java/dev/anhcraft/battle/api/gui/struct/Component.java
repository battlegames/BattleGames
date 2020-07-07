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

package dev.anhcraft.battle.api.gui.struct;

import dev.anhcraft.battle.utils.ConfigurableObject;
import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.annotation.IgnoreValue;
import dev.anhcraft.confighelper.annotation.Key;
import dev.anhcraft.confighelper.annotation.Schema;
import dev.anhcraft.confighelper.annotation.Validation;
import dev.anhcraft.craftkit.abif.PreparedItem;
import dev.anhcraft.jvmkit.utils.Condition;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@SuppressWarnings("FieldMayBeFinal")
@Schema
public class Component extends ConfigurableObject {
    public static final ConfigSchema<Component> SCHEMA = ConfigSchema.of(Component.class);
    private static final Map<Integer, Integer> CENTER_SLOTS = new HashMap<>();
    private static final PreparedItem DEFAULT_ITEM = new PreparedItem();

    static {
        CENTER_SLOTS.put(Objects.hash(1, 1), 5);
        CENTER_SLOTS.put(Objects.hash(2, 1), 3);
        CENTER_SLOTS.put(Objects.hash(2, 2), 7);
        CENTER_SLOTS.put(Objects.hash(3, 1), 2);
        CENTER_SLOTS.put(Objects.hash(3, 2), 5);
        CENTER_SLOTS.put(Objects.hash(3, 3), 8);
        CENTER_SLOTS.put(Objects.hash(4, 1), 2);
        CENTER_SLOTS.put(Objects.hash(4, 2), 4);
        CENTER_SLOTS.put(Objects.hash(4, 3), 6);
        CENTER_SLOTS.put(Objects.hash(4, 4), 8);
        CENTER_SLOTS.put(Objects.hash(5, 1), 1);
        CENTER_SLOTS.put(Objects.hash(5, 2), 3);
        CENTER_SLOTS.put(Objects.hash(5, 3), 5);
        CENTER_SLOTS.put(Objects.hash(5, 4), 7);
        CENTER_SLOTS.put(Objects.hash(5, 5), 9);
    }

    private final String id;

    @Key("positions")
    @Validation(notNull = true)
    private List<Integer> positions;

    @Key("item")
    @IgnoreValue(ifNull = true)
    private PreparedItem item = DEFAULT_ITEM;

    @Key("functions.on_init")
    @IgnoreValue(ifNull = true)
    private List<String> initFunction = new ArrayList<>();

    @Key("functions.on_click")
    @IgnoreValue(ifNull = true)
    private List<String> clickFunction = new ArrayList<>();

    @Key("functions.on_render")
    @IgnoreValue(ifNull = true)
    private List<String> renderFunction = new ArrayList<>();

    @Key("pagination")
    private String pagination;

    public Component(@NotNull String id) {
        Condition.argNotNull("id", id);
        this.id = id;
    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public List<Integer> getSlots() {
        return positions;
    }

    @NotNull
    public PreparedItem getItem() {
        return item;
    }

    public void setItem(@NotNull PreparedItem item) {
        this.item = item;
    }

    @Nullable
    public String getPagination() {
        return pagination;
    }

    public void setPagination(@Nullable String pagination) {
        this.pagination = pagination;
    }

    @NotNull
    public List<String> getInitFunction() {
        return initFunction;
    }

    @NotNull
    public List<String> getClickFunction() {
        return clickFunction;
    }

    @NotNull
    public List<String> getRenderFunction() {
        return renderFunction;
    }

    private int parseNum(String s){
        String[] t = s.split("/");
        if(t.length == 1){
            return Integer.parseInt(s.trim());
        } else {
            int f1 = Integer.parseInt(t[0].trim());
            int f2 = Integer.parseInt(t[1].trim());
            return CENTER_SLOTS.getOrDefault(Objects.hash(f2, f1), 0);
        }
    }
    
    private void parsePos(List<Integer> holder, String str){
        String[] p = str.split(";");
        if(p.length == 2){
            String[] p1 = p[0].split("->");
            String[] p2 = p[1].split("->");
            int minX, maxX;
            if(p1.length == 1){
                minX = parseNum(p1[0]);
                maxX = minX;
            } else if(p1.length == 2){
                minX = parseNum(p1[0]);
                maxX = parseNum(p1[1]);
            } else {
                Bukkit.getLogger().warning("Invalid slot format: "+str);
                return;
            }
            int minY, maxY;
            if(p2.length == 1){
                minY = parseNum(p2[0]);
                maxY = minY;
            } else if(p2.length == 2){
                minY = parseNum(p2[0]);
                maxY = parseNum(p2[1]);
            } else {
                return;
            }
            for(int i = minY; i <= maxY; i++) {
                for (int j = minX; j <= maxX; j++) {
                    holder.add(i * 9 + j - 10);
                }
            }
        } else {
            Bukkit.getLogger().warning("Invalid slot format: "+str);
        }
    }

    @Override
    protected @Nullable Object conf2schema(@Nullable Object value, ConfigSchema.Entry entry) {
        if(value != null && entry.getKey().equals("positions")){
            List<Integer> ints = new ArrayList<>();
            if(value instanceof String){
                parsePos(ints, (String) value);
            } else if(value instanceof List){
                List<String> list = (List<String>) value;
                for(String s : list) parsePos(ints, s);
            }
            return ints;
        }
        return value;
    }
}
