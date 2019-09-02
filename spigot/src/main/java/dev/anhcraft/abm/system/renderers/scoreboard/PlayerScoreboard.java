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
package dev.anhcraft.abm.system.renderers.scoreboard;

import dev.anhcraft.abm.utils.PlaceholderUtils;
import dev.anhcraft.craftkit.cb_common.lang.enumeration.NMSVersion;
import dev.anhcraft.jvmkit.utils.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlayerScoreboard {
    private static final int MAX_CHAR = NMSVersion.getNMSVersion().isNewerOrSame(NMSVersion.v1_13_R1) ? 64 : 16;
    private final List<String> ENTRIES = new ArrayList<>();
    private final Player player;
    private final ScoreboardLine[] lines;
    private final Objective objective;
    private final Scoreboard scoreboard;
    private final String title;
    private final boolean fixedLength;

    public PlayerScoreboard(Player player, String title, List<String> lines, boolean fixedLength) {
        this.player = player;
        int maxLines = Math.min(15, lines.size());
        this.lines = new ScoreboardLine[maxLines];
        this.title = title;
        this.fixedLength = fixedLength;

        scoreboard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard();
        objective = scoreboard.registerNewObjective(StringUtil.cutString("abm." + player.getName(), 16), "dummy");
        renderTitle();
        int i = 0;
        while(i < maxLines){
            ChatColor color = ChatColor.values()[i];
            String entry = color.toString() + ChatColor.RESET.toString();
            this.lines[i] = new ScoreboardLine(scoreboard.registerNewTeam(entry), color, lines.get(i));
            objective.getScore(entry).setScore(maxLines - (++i));
        }
    }

    public void renderTitle(){
        objective.setDisplayName(StringUtil.cutString(PlaceholderUtils.formatPAPI(player, title), 2 * MAX_CHAR));
    }

    public void renderLines(){
        int i = 0;
        while(i < lines.length)
            renderLine(i++);
    }

    public void renderLine(int index){
        ScoreboardLine line = lines[index];
        String content = PlaceholderUtils.formatPAPI(player, line.getContent());

        StringBuilder prefix = new StringBuilder(StringUtil.cutString(content, MAX_CHAR));
        // if the prefix ends with colors, remove it!
        if (prefix.charAt(prefix.length() - 2) == ChatColor.COLOR_CHAR)
            prefix.delete(prefix.length() - 2, prefix.length());
        if(prefix.length() == 0) return;

        boolean rendered = false;

        // only change the suffix if needed
        if(content.length() > MAX_CHAR) {
            // sometimes, dividing prefix and suffix causes the prefix to be ended with a color sign
            // we can remove it here
            if (prefix.charAt(prefix.length() - 1) == ChatColor.COLOR_CHAR)
                prefix.deleteCharAt(prefix.length() - 1);
            if(prefix.length() == 0) return;
            String preStr = prefix.toString();

            StringBuilder suffix = new StringBuilder();
            int maxSufLen = Math.min(prefix.length() + MAX_CHAR - suffix.length(), content.length());
            String s = content.substring(prefix.length(), maxSufLen);
            if(s.length() == 0) return;
            suffix.append(s);

            // if the beginning of the suffix did not have colors, we will try to add
            // color codes from the end of the prefix
            if(s.charAt(0) != ChatColor.COLOR_CHAR) {
                String lc = ChatColor.getLastColors(preStr);
                if(suffix.length() + lc.length() <= MAX_CHAR) suffix.insert(0, lc);
            }

            // if the suffix ends with colors, remove it!
            if(suffix.charAt(suffix.length() - 2) == ChatColor.COLOR_CHAR)
                suffix.delete(suffix.length() - 2, suffix.length());
            if(suffix.length() == 0) return;

            // sometimes, dividing suffix and the rest causes the suffix to be ended with a color sign
            // we can remove it here
            if (suffix.charAt(suffix.length() - 1) == ChatColor.COLOR_CHAR)
                suffix.deleteCharAt(suffix.length() - 1);
            if(suffix.length() == 0) return;

            // add spaces to the end if needed
            while (fixedLength && suffix.length() < MAX_CHAR) suffix.append(" ");
            line.getTeam().setPrefix(preStr);
            line.getTeam().setSuffix(suffix.toString());
            rendered = true;
        }
        if(!rendered) {
            // add spaces to the end if needed
            while (fixedLength && prefix.length() < MAX_CHAR) prefix.append(" ");
            line.getTeam().setPrefix(prefix.toString());
            line.getTeam().setSuffix("");
        }
        line.getTeam().addEntry(line.getTeam().getName());
        ENTRIES.forEach(line.getTeam()::addEntry);
    }

    public List<String> getEntries() {
        return ENTRIES;
    }

    public Player getPlayer() {
        return player;
    }

    public ScoreboardLine[] getLines() {
        return lines;
    }

    void show(){
        renderLines();
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        player.setScoreboard(scoreboard);
    }

    void remove(){
        objective.unregister();
        scoreboard.clearSlot(DisplaySlot.SIDEBAR);
    }

    void render() {
        renderTitle();
        renderLines();
    }
}
