package dev.anhcraft.abm.api.enums;

import dev.anhcraft.abm.system.controllers.ModeController;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public enum Mode {
    DEATHMATCH,
    TEAM_DEATHMATCH;

    private String name;
    private String description;
    private boolean waitingChatEnabled;
    private String waitingChatFormat;
    private boolean playingChatEnabled;
    private String playingChatFormat;
    private boolean waitingScoreboardEnabled;
    private String waitingScoreboardTitle;
    private List<String> waitingScoreboardContent;
    private boolean playingScoreboardEnabled;
    private String playingScoreboardTitle;
    private List<String> playingScoreboardContent;
    private ConfigurationSection config;
    private ModeController controller;

    public void init(@NotNull ConfigurationSection conf){
        Validate.notNull(conf, "Conf must be non-null");

        this.config = conf;
        name = conf.getString("name");
        description = conf.getString("description");
        waitingChatEnabled = conf.getBoolean("waiting_chat.enabled");
        waitingChatFormat = conf.getString("waiting_chat.format");
        playingChatEnabled = conf.getBoolean("playing_chat.enabled");
        playingChatFormat = conf.getString("playing_chat.format");
        waitingScoreboardEnabled = conf.getBoolean("waiting_scoreboard.enabled");
        waitingScoreboardTitle = conf.getString("waiting_scoreboard.title");
        waitingScoreboardContent = conf.getStringList("waiting_scoreboard.content");
        playingScoreboardEnabled = conf.getBoolean("playing_scoreboard.enabled");
        playingScoreboardTitle = conf.getString("playing_scoreboard.title");
        playingScoreboardContent = conf.getStringList("playing_scoreboard.content");
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public String getDescription() {
        return description;
    }

    public boolean isWaitingChatEnabled() {
        return waitingChatEnabled;
    }

    @NotNull
    public String getWaitingChatFormat() {
        return waitingChatFormat;
    }

    public boolean isPlayingChatEnabled() {
        return playingChatEnabled;
    }

    @NotNull
    public String getPlayingChatFormat() {
        return playingChatFormat;
    }

    public boolean isWaitingScoreboardEnabled() {
        return waitingScoreboardEnabled;
    }

    @NotNull
    public String getWaitingScoreboardTitle() {
        return waitingScoreboardTitle;
    }

    @NotNull
    public List<String> getWaitingScoreboardContent() {
        return waitingScoreboardContent;
    }

    public boolean isPlayingScoreboardEnabled() {
        return playingScoreboardEnabled;
    }

    @NotNull
    public String getPlayingScoreboardTitle() {
        return playingScoreboardTitle;
    }

    @NotNull
    public List<String> getPlayingScoreboardContent() {
        return playingScoreboardContent;
    }

    @NotNull
    public ConfigurationSection getConfig() {
        return config;
    }

    @Nullable
    public ModeController getController() {
        return controller;
    }

    public void setController(@Nullable ModeController controller) {
        this.controller = controller;
    }
}
