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
    private static final int MAX_LINE_CHAR = NMSVersion.getNMSVersion().isNewerOrSame(NMSVersion.v1_13_R1) ? 64 : 16;
    private final List<String> ENTRIES = new ArrayList<>();
    private final Player player;
    private final ScoreboardLine[] lines;
    private final Objective objective;
    private final Scoreboard scoreboard;
    private final String title;

    public PlayerScoreboard(Player player, String title, List<String> lines) {
        this.player = player;
        this.lines = new ScoreboardLine[lines.size()];
        this.title = title;

        scoreboard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard();
        objective = scoreboard.registerNewObjective(StringUtil.cutString("abm." + player.getName(), 16), "dummy");
        objective.setDisplayName(PlaceholderUtils.formatPAPI(player, title));
        int i = 0;
        int max = Math.min(15, lines.size());
        while(i < max){
            ChatColor color = ChatColor.values()[i];
            String entry = color.toString()+ChatColor.RESET.toString();
            this.lines[i] = new ScoreboardLine(scoreboard.registerNewTeam(entry), color, lines.get(i));
            objective.getScore(entry).setScore(max - (++i));
        }
    }

    public void renderTitle(){
        objective.setDisplayName(PlaceholderUtils.formatPAPI(player, title));
    }

    public void renderLines(){
        int i = 0;
        int max = Math.min(15, lines.length);
        while(i < max) renderLine(i++);
    }

    public void renderLine(int index){
        ScoreboardLine line = lines[index];
        String str = PlaceholderUtils.formatPAPI(player, line.getContent());

        int mid = Math.min(str.length(), MAX_LINE_CHAR);
        String pre = str.substring(0, mid);
        line.getTeam().setPrefix(pre);

        if(str.length() > MAX_LINE_CHAR) {
            int max = Math.min(str.length(), MAX_LINE_CHAR * 2);
            String suf = str.substring(MAX_LINE_CHAR, max);
            suf = ChatColor.getLastColors(pre) + suf;
            line.getTeam().setSuffix(StringUtil.cutString(suf, MAX_LINE_CHAR));
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
