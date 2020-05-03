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

import dev.anhcraft.battle.ApiProvider;
import dev.anhcraft.battle.api.BattleApi;
import dev.anhcraft.battle.api.gui.GuiHandler;
import dev.anhcraft.battle.api.gui.GuiManager;
import dev.anhcraft.battle.api.gui.SlotReport;
import dev.anhcraft.battle.utils.ConfigurableObject;
import dev.anhcraft.battle.utils.functions.FunctionLinker;
import dev.anhcraft.battle.utils.functions.Instruction;
import dev.anhcraft.battle.utils.info.InfoReplacer;
import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.annotation.IgnoreValue;
import dev.anhcraft.confighelper.annotation.Key;
import dev.anhcraft.confighelper.annotation.Schema;
import dev.anhcraft.confighelper.annotation.Validation;
import dev.anhcraft.craftkit.abif.PreparedItem;
import dev.anhcraft.jvmkit.utils.Condition;
import org.apache.commons.lang.ArrayUtils;
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
    private List<FunctionLinker<SlotReport>> initFunctions;
    private List<FunctionLinker<SlotReport>> clickFunctions;

    @Key("positions")
    @Validation(notNull = true)
    private List<Integer> positions;

    @Key("item")
    @IgnoreValue(ifNull = true)
    private PreparedItem item = DEFAULT_ITEM;

    @Key("functions.on_init")
    @IgnoreValue(ifNull = true)
    private List<String> rawInitFunctions = new ArrayList<>();

    @Key("functions.on_click")
    @IgnoreValue(ifNull = true)
    private List<String> rawClickFunctions = new ArrayList<>();

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

    private void compileFunction(List<String> rawFunctions, List<FunctionLinker<SlotReport>> functions){
        if(!rawFunctions.isEmpty()){
            GuiManager bgm = ApiProvider.consume().getGuiManager();
            for (Iterator<String> it = rawFunctions.iterator(); it.hasNext(); ) {
                String str = it.next();
                Instruction fn = Instruction.parse(str);
                if(fn != null) {
                    GuiHandler gh = bgm.getGuiHandler(fn.getNamespace());
                    // if gui handler does not exist, don't remove immediately
                    // it may be available in the future
                    if (gh == null) continue;
                    functions.add(new FunctionLinker<>(
                            fn,
                            event -> {
                                BattleApi a = ApiProvider.consume();
                                InfoReplacer f = a.getGuiManager().collectInfo(event.getView()).compile();
                                String[] x = (String[]) ArrayUtils.clone(fn.getArgs());
                                if(!gh.fireEvent(fn.getTarget(), event, f.replace(x))){
                                    throw new IllegalStateException("Event fired failed");
                                }
                            })
                    );
                }
                it.remove();
            }
        }
    }

    @NotNull
    public synchronized List<FunctionLinker<SlotReport>> getClickFunctions() {
        if(clickFunctions == null) clickFunctions = new ArrayList<>();
        compileFunction(rawClickFunctions, clickFunctions);
        return clickFunctions;
    }

    @NotNull
    public synchronized List<FunctionLinker<SlotReport>> getInitFunctions() {
        if(initFunctions == null) initFunctions = new ArrayList<>();
        compileFunction(rawInitFunctions, initFunctions);
        return initFunctions;
    }

    @Nullable
    public String getPagination() {
        return pagination;
    }

    public void setPagination(@Nullable String pagination) {
        this.pagination = pagination;
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

    @Nullable
    protected Object schema2conf(@Nullable Object value, ConfigSchema.Entry entry){
        if(value != null){
            if(entry.getKey().equals("functions.on_init")) {
                List<String> strs = new ArrayList<>();
                // don't use "functions", call the getter to do some init works first
                for (FunctionLinker fc : getClickFunctions())
                    strs.add(fc.getInstruction().toString());
                return strs;
            } else if(entry.getKey().equals("functions.on_click")) {
                List<String> strs = new ArrayList<>();
                for (FunctionLinker fc : getInitFunctions())
                    strs.add(fc.getInstruction().toString());
                return strs;
            }
        }
        return value;
    }
}
