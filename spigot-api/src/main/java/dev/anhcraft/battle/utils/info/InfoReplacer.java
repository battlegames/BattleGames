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

package dev.anhcraft.battle.utils.info;

import dev.anhcraft.craftkit.abif.PreparedItem;
import dev.anhcraft.craftkit.common.utils.ChatUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import static dev.anhcraft.battle.utils.PlaceholderUtil.INFO_PLACEHOLDER_PATTERN;

public class InfoReplacer {
    private Map<String, String> map;

    InfoReplacer(InfoHolder holder) {
        this.map = holder.mapInfo();
        map.replaceAll((s, s2) -> ChatUtil.formatColorCodes(s2));
    }

    @NotNull
    public String replace(@NotNull String str){
        Matcher m = INFO_PLACEHOLDER_PATTERN.matcher(str);
        StringBuffer sb = new StringBuffer(str.length());
        while(m.find()){
            String p = m.group();
            String s = p.substring(1, p.length()-1).trim();
            String[] f = s.split(":");
            if(f.length == 2) {
                s = f[0];
                p = f[1];
            }
            m.appendReplacement(sb, "");
            sb.append(map.getOrDefault(s, p));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    @NotNull
    public List<String> replace(@NotNull List<String> strs){
        strs.replaceAll(this::replace);
        return strs;
    }

    @NotNull
    public String[] replace(@NotNull String[] strs){
        for(int i = 0; i < strs.length; i++){
            strs[i] = replace(strs[i]);
        }
        return strs;
    }

    @NotNull
    public PreparedItem replace(@NotNull PreparedItem item){
        String n = item.name();
        if(n != null) {
            item.name(replace(n));
        }
        replace(item.lore());
        return item;
    }

    @NotNull
    public Map<String, String> getMap(){
        return map;
    }
}
