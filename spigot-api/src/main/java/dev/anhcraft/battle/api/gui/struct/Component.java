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

import dev.anhcraft.battle.utils.PreparedItem;
import dev.anhcraft.config.annotations.*;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@SuppressWarnings("FieldMayBeFinal")
@Configurable
public class Component {
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

    @Setting
    @Description("List of slots belong to this component")
    @Example("positions: 1 -> 9; 1 -> 3")
    @Example("positions: 3 -> 5; 4")
    @Example({
            "positions:",
            " - 1 ; 2",
            " - 1 ; 2 -> 4"
    })
    @Validation(notNull = true)
    private List<String> positions;

    @Setting
    @Description({
            "The item to set in the GUI",
            "All slots belong to this component will have the same item"
    })
    @Validation(notNull = true, silent = true)
    private PreparedItem item = DEFAULT_ITEM;

    @Setting
    @Path("functions.on_init")
    @Description({
            "The function that gets called when one slot of this component is initialized",
            "Read more: <a href=\"https://github.com/battlegames/Battle-Hub/wiki/GUI\">https://github.com/battlegames/Battle-Hub/wiki/GUI</a>"
    })
    @Example({
            "on_init:",
            " - Common:CopyCurrentSlot(\"window\" \"positionOfThisSlot\")"
    })
    @Validation(notNull = true, silent = true)
    private List<String> initFunction = new ArrayList<>();

    @Setting
    @Path("functions.on_click")
    @Description({
            "The function that gets called when one slot of this component is clicked",
            "Read more: <a href=\"https://github.com/battlegames/Battle-Hub/wiki/GUI\">https://github.com/battlegames/Battle-Hub/wiki/GUI</a>"
    })
    @Example({
            "on_click:",
            "  - Common:CancelEvent() # Prevent steal items"
    })
    @Validation(notNull = true, silent = true)
    private List<String> clickFunction = new ArrayList<>();

    @Setting
    @Path("functions.on_render")
    @Description({
            "The function that gets called when one slot of this component is going to be rendered",
            "Set &#36;cancel_render to `true` to prevent this action; `false` is set by default",
            "It is not recommended to do any modifications to the item on that slot, you would be better",
            "to look at on_rendered, which is called <b>after</b> the rendering has done.",
            "Read more: <a href=\"https://github.com/battlegames/Battle-Hub/wiki/GUI\">https://github.com/battlegames/Battle-Hub/wiki/GUI</a>"
    })
    @Example({
            "on_render:",
            "  - Cache:SetBool(\"_cancel_render_\" true) ? _slot_ == 27"
    })
    private List<String> renderFunction;

    @Setting
    @Path("functions.on_rendered")
    @Description({
            "The function that gets called when one slot of this component was rendered successfully",
            "Read more: <a href=\"https://github.com/battlegames/Battle-Hub/wiki/GUI\">https://github.com/battlegames/Battle-Hub/wiki/GUI</a>"
    })
    @Example({
            "on_rendered:",
            "  - Common:SetItemFromData(\"window\" \"value\" &#36;_window_positionOfThisSlot \"not-null\")"
    })
    private List<String> renderedFunction;

    @Setting
    @Description({
            "Pagination that support multiple items to display in this component",
            "Read more: <a href=\"https://github.com/battlegames/Battle-Hub/wiki/GUI\">https://github.com/battlegames/Battle-Hub/wiki/GUI</a>"
    })
    private String pagination;

    private List<Integer> compiledPositions = new ArrayList<>();

    @NotNull
    public List<Integer> getSlots() {
        return compiledPositions;
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

    @Nullable
    public List<String> getRenderFunction() {
        return renderFunction;
    }

    @Nullable
    public List<String> getRenderedFunction() {
        return renderedFunction;
    }

    private int parseNum(String s) {
        String[] t = s.split("/");
        if (t.length == 1) {
            return Integer.parseInt(s.trim());
        } else {
            int f1 = Integer.parseInt(t[0].trim());
            int f2 = Integer.parseInt(t[1].trim());
            return CENTER_SLOTS.getOrDefault(Objects.hash(f2, f1), 0);
        }
    }

    private void parsePos(List<Integer> holder, String str) {
        String[] p = str.split(";");
        if (p.length == 2) {
            String[] p1 = p[0].split("->");
            String[] p2 = p[1].split("->");
            int minX, maxX;
            if (p1.length == 1) {
                minX = parseNum(p1[0]);
                maxX = minX;
            } else if (p1.length == 2) {
                minX = parseNum(p1[0]);
                maxX = parseNum(p1[1]);
            } else {
                Bukkit.getLogger().warning("Invalid slot format: " + str);
                return;
            }
            int minY, maxY;
            if (p2.length == 1) {
                minY = parseNum(p2[0]);
                maxY = minY;
            } else if (p2.length == 2) {
                minY = parseNum(p2[0]);
                maxY = parseNum(p2[1]);
            } else {
                return;
            }
            for (int i = minY; i <= maxY; i++) {
                for (int j = minX; j <= maxX; j++) {
                    holder.add(i * 9 + j - 10);
                }
            }
        } else {
            Bukkit.getLogger().warning("Invalid slot format: " + str);
        }
    }

    @PostHandler
    private void handle(){
        for(String s : positions) {
            parsePos(compiledPositions, s);
        }
    }
}
