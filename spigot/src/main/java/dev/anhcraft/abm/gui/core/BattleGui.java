package dev.anhcraft.abm.gui.core;

import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class BattleGui {
    private String title;
    private int size;
    private ConfigurationSection background;
    private BattleSlot[] slots;
    private BattlePagination pagination;
    private Sound sound;

    public BattleGui(ConfigurationSection conf){
        title = conf.getString("title");
        size = conf.getInt("size", 9);
        background = conf.getConfigurationSection("background");
        String snd = conf.getString("sound");
        sound = (snd == null ? null : Sound.valueOf(snd.toUpperCase()));

        slots = new BattleSlot[size];
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
                String handler = sc.getString(k+".handler");
                slots[pos] = new BattleSlot(item, handler);
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
                    slots[pos] = new BattleSlot(null, handlerId+"::"+SlotClickHandler.PAGINATION);
                }
            }
            List<String> hl = pg.getStringList("item_lore.header");
            List<String> fl = pg.getStringList("item_lore.footer");
            pagination = new BattlePagination(hl, fl, ps, handlerId);
        }
    }

    @Nullable
    public String getTitle() {
        return title;
    }

    public int getSize() {
        return size;
    }

    @Nullable
    public ConfigurationSection getBackground() {
        return background;
    }

    @NotNull
    public BattleSlot[] getSlots() {
        return slots;
    }

    @Nullable
    public BattlePagination getPagination() {
        return pagination;
    }

    @Nullable
    public Sound getSound() {
        return sound;
    }
}
