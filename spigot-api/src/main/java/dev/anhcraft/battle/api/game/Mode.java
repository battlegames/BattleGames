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
package dev.anhcraft.battle.api.game;

import com.google.common.collect.ImmutableList;
import dev.anhcraft.battle.api.BattleModeController;
import dev.anhcraft.battle.utils.info.InfoHolder;
import dev.anhcraft.battle.utils.info.Informative;
import dev.anhcraft.jvmkit.utils.Condition;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    public static List<Mode> listModes(){
        return ImmutableList.copyOf(MODE_REGISTRY.values());
    }

    public static void listModes(@NotNull Consumer<Mode> consumer){
        MODE_REGISTRY.values().forEach(consumer);
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

    /**
     * ID: <b>dm</b>
     */
    public static final Mode DEATHMATCH = registerMode(new Mode("dm"));

    /**
     * ID: <b>tdm</b>
     */
    public static final Mode TEAM_DEATHMATCH = registerMode(new Mode("tdm"));

    /**
     * ID: <b>ctf</b>
     */
    public static final Mode CTF = registerMode(new Mode("ctf"));

    /**
     * ID: <b>bw</b>
     */
    public static final Mode BEDWAR = registerMode(new Mode("bw"));

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
    private int waitingScoreboardFixedLength;
    private boolean playingScoreboardEnabled;
    private String playingScoreboardTitle;
    private List<String> playingScoreboardContent;
    private int playingScoreboardFixedLength;
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
        waitingScoreboardFixedLength = conf.getInt("waiting_scoreboard.fixed_length");
        playingScoreboardEnabled = conf.getBoolean("playing_scoreboard.enabled");
        playingScoreboardTitle = conf.getString("playing_scoreboard.title", "");
        playingScoreboardContent = conf.getStringList("playing_scoreboard.content");
        playingScoreboardFixedLength = conf.getInt("playing_scoreboard.fixed_length");
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

    public int isWaitingScoreboardFixedLength() {
        return waitingScoreboardFixedLength;
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

    public int isPlayingScoreboardFixedLength() {
        return playingScoreboardFixedLength;
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
