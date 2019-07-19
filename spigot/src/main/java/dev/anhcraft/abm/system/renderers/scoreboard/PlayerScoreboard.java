package dev.anhcraft.abm.system.renderers.scoreboard;

import dev.anhcraft.abm.utils.StringUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlayerScoreboard {
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
        objective = scoreboard.registerNewObjective(StringUtil.subStr("abm." + player.getName(), 16), "dummy", StringUtil.formatPlaceholders(player, title));
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
        objective.setDisplayName(StringUtil.formatPlaceholders(player, title));
    }

    public void renderLines(){
        int i = 0;
        int max = Math.min(15, lines.length);
        while(i < max) renderLine(i++);
    }

    public void renderLine(int index){
        ScoreboardLine line = lines[index];
        String str = StringUtil.formatPlaceholders(player, line.getContent());
        line.getTeam().setPrefix(str.substring(0, Math.min(str.length(), 64)));
        if(str.length() > 64) line.getTeam().setSuffix(str.substring(65));
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
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        player.setScoreboard(scoreboard);
        renderLines();
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
