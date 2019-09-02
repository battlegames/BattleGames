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
package dev.anhcraft.abm.api.gui;

import dev.anhcraft.abm.api.misc.SoundRecord;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Gui {
    private String title;
    private int size;
    private GuiSlot[] slots;
    private Pagination pagination;
    private SoundRecord sound;

    public Gui(ConfigurationSection conf){
        title = conf.getString("title");
        size = conf.getInt("size", 9);
        String snd = conf.getString("sound");
        sound = new SoundRecord(snd == null ? "$block_chest_open" : snd.toUpperCase());

        slots = new GuiSlot[size];
        ConfigurationSection sc = conf.getConfigurationSection("slots");
        if(sc != null){
            Set<String> keys = sc.getKeys(false);
            for(String k : keys){
                Object col = sc.get(k+".column");
                int cl;
                if(col instanceof String) cl = Objects.requireNonNull(conf.getParent())
                        .getInt("$center_items."+col);
                else cl = (int) col;

                int pos = sc.getInt(k+".row") * 9 + cl - 10;
                ConfigurationSection item = sc.getConfigurationSection(k+".item");
                List<String> handlers = sc.getStringList(k+".handlers");
                slots[pos] = new GuiSlot(item, handlers, false);
            }
        }

        ConfigurationSection pg = conf.getConfigurationSection("pagination");
        if(pg != null){
            String handlerId = pg.getString("handler");
            if(handlerId == null) throw new NullPointerException("Pagination handler must be specified");

            int minX = pg.getInt("region.left_column");
            int maxX = pg.getInt("region.right_column");
            int minY = pg.getInt("region.top_row");
            int maxY = pg.getInt("region.bottom_row");
            int lenX = Math.min(9, Math.max(0, maxX - minX + 1));
            int lenY = Math.min(9, Math.max(0, maxY - minY + 1));
            int[] ps = new int[lenX * lenY];
            int in = 0;
            for(int i = minY; i <= maxY; i++){
                for(int j = minX; j <= maxX; j++){
                    int pos = i * 9 + j - 10;
                    ps[in++] = pos;
                    slots[pos] = new GuiSlot(null, new ArrayList<>(), true);
                }
            }
            pagination = new Pagination(ps, handlerId);
        }

        ConfigurationSection bgIcon = conf.getConfigurationSection("background.item");
        List<String> bgHandlers = conf.getStringList("background.handlers");
        for(int i = 0; i < slots.length; i++){
            if(slots[i] == null) slots[i] = new GuiSlot(bgIcon, bgHandlers, false);
        }
    }

    @Nullable
    public String getTitle() {
        return title;
    }

    public int getSize() {
        return size;
    }

    @NotNull
    public GuiSlot[] getSlots() {
        return slots;
    }

    @Nullable
    public Pagination getPagination() {
        return pagination;
    }

    @NotNull
    public SoundRecord getSound() {
        return sound;
    }
}
