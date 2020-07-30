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
package dev.anhcraft.battle.api.arena.game;

import com.google.common.collect.ImmutableList;
import dev.anhcraft.battle.api.arena.game.controllers.GameController;
import dev.anhcraft.battle.api.arena.game.options.*;
import dev.anhcraft.battle.api.chat.BattleChat;
import dev.anhcraft.battle.api.BattleScoreboard;
import dev.anhcraft.battle.impl.Informative;
import dev.anhcraft.battle.utils.ConfigurableObject;
import dev.anhcraft.battle.utils.info.InfoHolder;
import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.annotation.Explanation;
import dev.anhcraft.confighelper.annotation.Key;
import dev.anhcraft.confighelper.annotation.Schema;
import dev.anhcraft.confighelper.annotation.Validation;
import dev.anhcraft.jvmkit.utils.Condition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Schema
public class Mode extends ConfigurableObject implements Informative {
    public static final ConfigSchema<Mode> SCHEMA = ConfigSchema.of(Mode.class);
    private static final Map<String, Mode> REGISTRY = new HashMap<>();

    @NotNull
    public static Mode register(@NotNull Mode mode){
        Condition.argNotNull("mode", mode);
        if(REGISTRY.containsKey(mode.id)) throw new IllegalStateException("Mode is already registered");
        REGISTRY.put(mode.id, mode);
        return mode;
    }

    public static boolean unregister(@NotNull String id){
        return REGISTRY.remove(id) != null;
    }

    @NotNull
    public static List<Mode> list(){
        return ImmutableList.copyOf(REGISTRY.values());
    }

    public static void list(@NotNull Consumer<Mode> consumer){
        REGISTRY.values().forEach(consumer);
    }

    @Nullable
    public static Mode get(@NotNull String id){
        Condition.argNotNull("id", id);
        return REGISTRY.get(id.toLowerCase());
    }

    public static void get(@NotNull String id, @NotNull Consumer<Mode> consumer){
        Condition.argNotNull("id", id);
        Condition.argNotNull("consumer", consumer);
        Mode m = REGISTRY.get(id);
        if(m != null) consumer.accept(m);
    }

    /**
     * ID: <b>dm</b>
     */
    public static final Mode DEATHMATCH = register(new Mode("dm", DeathmatchOptions.SCHEMA));

    /**
     * ID: <b>tdm</b>
     */
    public static final Mode TEAM_DEATHMATCH = register(new Mode("tdm", TeamDeathmatchOptions.SCHEMA));

    /**
     * ID: <b>ctf</b>
     */
    public static final Mode CTF = register(new Mode("ctf", CaptureTheFlagOptions.SCHEMA));

    /**
     * ID: <b>bw</b>
     */
    public static final Mode BEDWAR = register(new Mode("bw", BedWarOptions.SCHEMA));

    /**
     * ID: <b>mr</b>
     */
    public static final Mode MOB_RESCUE = register(new Mode("mr", MobRescueOptions.SCHEMA));

    private final ConfigSchema<?> optionSchema;
    private final String id;
    private GameController controller;

    @Key("name")
    @Validation(notNull = true)
    @Explanation("A nice name for the game mode")
    private String name;

    @Key("waiting_chat")
    @Validation(notNull = true)
    @Explanation("Chat configuration (during waiting phase)")
    private BattleChat waitingChat;

    @Key("playing_chat")
    @Validation(notNull = true)
    @Explanation("Chat configuration (during playing phase)")
    private BattleChat playingChat;

    @Key("waiting_scoreboard")
    @Validation(notNull = true)
    @Explanation("Scoreboard configuration (during waiting phase)")
    private BattleScoreboard waitingScoreboard;

    @Key("playing_scoreboard")
    @Validation(notNull = true)
    @Explanation("Scoreboard configuration (during playing phase)")
    private BattleScoreboard playingScoreboard;

    public Mode(@NotNull String id, @NotNull ConfigSchema<?> optionSchema){
        Condition.argNotNull("id", id);
        this.id = id.toLowerCase();
        this.optionSchema = optionSchema;
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
    public BattleChat getWaitingChat() {
        return waitingChat;
    }

    @NotNull
    public BattleChat getPlayingChat() {
        return playingChat;
    }

    @NotNull
    public BattleScoreboard getWaitingScoreboard() {
        return waitingScoreboard;
    }

    @NotNull
    public BattleScoreboard getPlayingScoreboard() {
        return playingScoreboard;
    }

    @NotNull
    public ConfigSchema<?> getOptionSchema() {
        return optionSchema;
    }

    @Nullable
    public GameController getController() {
        return controller;
    }

    public void getController(Consumer<GameController> consumer) {
        if(controller != null && consumer != null) consumer.accept(controller);
    }

    public void setController(@Nullable GameController controller) {
        this.controller = controller;
    }

    @Override
    public void inform(@NotNull InfoHolder holder) {
        holder.inform("id", id).inform("name", name);
    }
}
