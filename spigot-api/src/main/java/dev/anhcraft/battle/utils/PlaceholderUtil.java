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
package dev.anhcraft.battle.utils;

import dev.anhcraft.jvmkit.utils.Condition;
import dev.anhcraft.jvmkit.utils.MathUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderUtil {
    public static final Pattern EXPRESSION_PLACEHOLDER_PATTERN = Pattern.compile("<\\?.+\\?>");
    public static final Pattern LOCALE_PLACEHOLDER_PATTERN = Pattern.compile("\\[([ A-Za-z0-9._-])+]");
    public static final Pattern INFO_PLACEHOLDER_PATTERN = Pattern.compile("<[a-zA-Z0-9:_]+>");

    @NotNull
    public static PreparedItem formatPAPI(@NotNull PreparedItem pi, @NotNull Player player) {
        Condition.notNull(pi);
        Condition.notNull(player);
        pi.name(formatPAPI(player, pi.name()));
        pi.lore().replaceAll(s -> formatPAPI(player, s));
        return pi;
    }

    @NotNull
    public static PreparedItem formatExpression(@NotNull PreparedItem pi) {
        Condition.notNull(pi);
        pi.name(formatExpression(pi.name()));
        pi.lore().replaceAll(PlaceholderUtil::formatExpression);
        return pi;
    }

    @NotNull
    public static PreparedItem formatTranslations(@NotNull PreparedItem pi, @Nullable ConfigurationSection localeConf) {
        Condition.notNull(pi);
        pi.name(localizeString(pi.name(), localeConf));
        pi.lore(localizeStrings(pi.lore(), localeConf));
        return pi;
    }

    @Contract("_, null -> null")
    public static String formatPAPI(@NotNull Player player, @Nullable String str) {
        if (str == null) return null;
        Condition.notNull(player);
        return PlaceholderAPI.setPlaceholders((OfflinePlayer) player, str);
    }

    @Contract("_, null -> null")
    public static List<String> formatPAPI(@NotNull Player player, @Nullable List<String> str) {
        Condition.notNull(player);
        if (str == null) return null;
        str.replaceAll(s -> formatPAPI(player, s)); // use #replaceAll is better than the provided PAPI method
        return str;
    }

    @Contract("null -> null")
    public static String formatExpression(@Nullable String str) {
        if (str == null) return null;
        Matcher m = EXPRESSION_PLACEHOLDER_PATTERN.matcher(str);
        StringBuffer sb = new StringBuffer(str.length());
        while (m.find()) {
            String p = m.group();
            String s = p.substring(2, p.length() - 2).trim();
            m.appendReplacement(sb, MathUtil.formatRound(new ExpressionBuilder(s).build().evaluate()));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    @Contract("null, _ -> null; _, null -> null")
    public static String localizeString(@Nullable String str, @Nullable ConfigurationSection localeConf) {
        if (str == null || localeConf == null) return null;
        Matcher m = LOCALE_PLACEHOLDER_PATTERN.matcher(str);
        StringBuffer sb = new StringBuffer(str.length());
        while (m.find()) {
            String p = m.group();
            String s = localeConf.getString(p.substring(1, p.length() - 1).trim());
            m.appendReplacement(sb, s == null ? p : s);
        }
        m.appendTail(sb);
        return sb.toString();
    }

    @NotNull
    public static List<String> localizeStrings(@Nullable List<String> strs, @Nullable ConfigurationSection localeConf) {
        if (strs == null || localeConf == null) return new ArrayList<>();
        List<String> out = new ArrayList<>();
        outer:
        for (String str : strs) {
            Matcher m = LOCALE_PLACEHOLDER_PATTERN.matcher(str);
            StringBuffer sb = new StringBuffer(str.length());
            while (m.find()) {
                String p = m.group();
                Object obj = localeConf.get(p.substring(1, p.length() - 1).trim());
                if (obj instanceof List) {
                    for (Object o : (List) obj)
                        out.add(o.toString());
                    continue outer;
                }
                m.appendReplacement(sb, obj == null ? p : obj.toString());
            }
            m.appendTail(sb);
            out.add(sb.toString());
        }
        return out;
    }
}
