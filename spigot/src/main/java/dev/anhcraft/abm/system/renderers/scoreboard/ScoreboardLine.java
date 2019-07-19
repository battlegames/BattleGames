package dev.anhcraft.abm.system.renderers.scoreboard;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.scoreboard.Team;

public class ScoreboardLine {
    private Team team;
    private ChatColor color;
    private String content;

    public ScoreboardLine(Team team, ChatColor color, String content) {
        this.team = team;
        this.color = color;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ChatColor getColor() {
        return color;
    }

    public Team getTeam() {
        return team;
    }
}
