package dev.anhcraft.abm.api.game;

import dev.anhcraft.abm.api.BattleModeController;
import dev.anhcraft.abm.api.misc.info.InfoHolder;
import dev.anhcraft.abm.api.misc.info.Informative;
import dev.anhcraft.jvmkit.utils.Condition;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class Mode implements Informative {
    private static final Map<String, Mode> MODE_REGISTRY = new ConcurrentHashMap<>();

    @Contract("!null -> param1")
    @NotNull
    public static Mode registerMode(@NotNull Mode mode){
        Condition.argNotNull("mode", mode);
        String k = mode.id.toLowerCase();
        if(MODE_REGISTRY.containsKey(k)) throw new IllegalStateException("Mode is already registered");
        MODE_REGISTRY.put(k, mode);
        return mode;
    }

    @NotNull
    public Collection<Mode> listModes(){
        return Collections.unmodifiableCollection(MODE_REGISTRY.values());
    }

    @Nullable
    public static Mode getMode(@NotNull String id){
        Condition.argNotNull("id", id);
        return MODE_REGISTRY.get(id.toLowerCase());
    }

    public static void getMode(@NotNull String id, @NotNull Consumer<Mode> consumer){
        Condition.argNotNull("id", id);
        Condition.argNotNull("consumer", consumer);
        Mode m = MODE_REGISTRY.get(id);
        if(m != null) consumer.accept(m);
    }

    public static final Mode DEATHMATCH = registerMode(new Mode("deathmatch"));
    public static final Mode TEAM_DEATHMATCH = registerMode(new Mode("team_deathmatch"));

    private String id;
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
    private BattleModeController controller;

    public Mode(@NotNull String id){
        Condition.argNotNull("id", id);
        this.id = id;
    }

    public void init(@NotNull ConfigurationSection conf){
        Condition.argNotNull("conf", conf);

        config = conf;
        name = conf.getString("name");
        if(name == null) throw new NullPointerException("Name must be specified");
        description = conf.getString("description");
        if(description == null) throw new NullPointerException("Description must be specified");
        waitingChatEnabled = conf.getBoolean("waiting_chat.enabled");
        waitingChatFormat = conf.getString("waiting_chat.format", "");
        playingChatEnabled = conf.getBoolean("playing_chat.enabled");
        playingChatFormat = conf.getString("playing_chat.format", "");
        waitingScoreboardEnabled = conf.getBoolean("waiting_scoreboard.enabled");
        waitingScoreboardTitle = conf.getString("waiting_scoreboard.title", "");
        waitingScoreboardContent = conf.getStringList("waiting_scoreboard.content");
        playingScoreboardEnabled = conf.getBoolean("playing_scoreboard.enabled");
        playingScoreboardTitle = conf.getString("playing_scoreboard.title", "");
        playingScoreboardContent = conf.getStringList("playing_scoreboard.content");
    }

    @NotNull
    public String getId() {
        return id;
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
    public BattleModeController getController() {
        return controller;
    }

    public void getController(Consumer<BattleModeController> consumer) {
        if(controller != null && consumer != null) consumer.accept(controller);
    }

    public void setController(@Nullable BattleModeController controller) {
        this.controller = controller;
    }

    @Override
    public void inform(@NotNull InfoHolder holder) {
        holder.inform("id", id)
                .inform("name", name)
                .inform("description", description);
    }
}
