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
package dev.anhcraft.battle.api.arena;

import dev.anhcraft.battle.ApiProvider;
import dev.anhcraft.battle.api.arena.game.GamePlayer;
import dev.anhcraft.battle.api.effect.firework.BattleFirework;
import dev.anhcraft.battle.utils.ConfigurableObject;
import dev.anhcraft.battle.api.misc.Rollback;
import dev.anhcraft.battle.api.arena.mode.Mode;
import dev.anhcraft.battle.utils.info.InfoHolder;
import dev.anhcraft.battle.impl.Informative;
import dev.anhcraft.battle.utils.info.State;
import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.annotation.*;
import dev.anhcraft.craftkit.abif.PreparedItem;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Schema
public class Arena extends ConfigurableObject implements Informative {
    public static final ConfigSchema<Arena> SCHEMA = ConfigSchema.of(Arena.class);
    private String id;
    private String finalExpExpression;
    private String finalMoneyExpression;

    @Key("name")
    @Explanation("The name of this arena")
    @IgnoreValue(ifNull = true)
    private String name;

    @Key("mode")
    @Explanation("The game mode")
    @Validation(notNull = true)
    private Mode mode;

    @Key("icon")
    @Explanation("The icon of this arena")
    @Validation(notNull = true)
    private PreparedItem icon;

    @Key("max_time")
    @Explanation("The maximum playing time in this arena")
    private long maxTime;

    @Key("max_players")
    @Explanation("The maximum players in this arena")
    private int maxPlayers;

    @Key("final_exp_formula")
    @Explanation({
            "The formula for calculating the final exp",
            "- a: number of headshots",
            "- b: number of kills",
            "- c: number of deaths",
            "- d: <b>1</b> if won or <b>0</b> if lost"
    })
    @Validation(notNull = true)
    private Expression finalExpCalculator;

    @Key("final_money_formula")
    @Explanation({
            "The formula for calculating the final money",
            "- a: number of headshots",
            "- b: number of kills",
            "- c: number of deaths",
            "- d: <b>1</b> if won or <b>0</b> if lost"
    })
    @Validation(notNull = true)
    private Expression finalMoneyCalculator;

    @Key("attr")
    @Explanation({
            "The attributes of this arena",
            "Attributes are settings for the mode"
    })
    @IgnoreValue(ifNull = true)
    private ConfigurationSection attrSection = new YamlConfiguration();

    @Key("end_commands.winners")
    @Explanation({
            "Commands to be executed by the console at the end",
            "Placeholders can be used within the command with",
            "values parsed from each <b>winner</b>"
    })
    @IgnoreValue(ifNull = true)
    private List<String> endCommandWinners = new ArrayList<>();

    @Key("end_commands.losers")
    @Explanation({
            "Commands to be executed by the console at the end",
            "Placeholders can be used within the command with",
            "values parsed from each <b>loser</b>"
    })
    @IgnoreValue(ifNull = true)
    private List<String> endCommandLosers = new ArrayList<>();

    @Key("render_gui_on_death")
    @Explanation({
            "Re-renders the GUI on death",
            "This option can prevent players from reusing old items"
    })
    private boolean renderGuiOnDeath = true;

    @Key("bungeecord.enabled")
    @Explanation({
            "Enable the Bungeecord support for this arena",
            "<b>Note: You must enable the Bungeecord support for",
            "the whole plugin first (see general.yml)</b>"
    })
    private boolean bungeeSupport;

    @Key("bungeecord.remote_servers")
    @Explanation({
            "List of remote servers",
            "Remote servers are places where the game happens"
    })
    @IgnoreValue(ifNull = true)
    private List<String> remoteServers = new ArrayList<>();

    @Key("end_firework")
    @Explanation("The firework to be spawned when the game ends")
    private BattleFirework endFirework;

    @Key("end_delay")
    @Explanation("The delay time before the game actually ends")
    private long endDelay = 60;

    @Key("result_broadcast.won")
    @Explanation("The message to be sent to the winners")
    private List<String> wonReport = new ArrayList<>();

    @Key("result_broadcast.lost")
    @Explanation("The message to be sent to the winners")
    private List<String> lostReport = new ArrayList<>();

    @Key("rollback")
    @Explanation("Rollback settings")
    private Rollback rollback;

    public Arena(@NotNull String id) {
        Validate.notNull(id, "Id must be non-null");
        this.id = id;
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
    public Mode getMode() {
        if(mode == null)
            throw new UnsupportedOperationException("Mode is not present");
        return mode;
    }

    @NotNull
    public PreparedItem getIcon() {
        return icon;
    }

    public long getMaxTime() {
        return maxTime;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public double calculateFinalMoney(@NotNull GamePlayer player){
        Validate.notNull(player, "Player must be non-null");
        return finalMoneyCalculator
                .setVariable("a", player.getHeadshotCounter().get())
                .setVariable("b", player.getKillCounter().get())
                .setVariable("c", player.getDeathCounter().get())
                .setVariable("d", player.isWinner() ? 1 : 0)
                .evaluate();
    }

    public long calculateFinalExp(@NotNull GamePlayer player){
        Validate.notNull(player, "Player must be non-null");
        return (long) finalExpCalculator
                .setVariable("a", player.getHeadshotCounter().get())
                .setVariable("b", player.getKillCounter().get())
                .setVariable("c", player.getDeathCounter().get())
                .setVariable("d", player.isWinner() ? 1 : 0)
                .evaluate();
    }

    @NotNull
    public ConfigurationSection getAttributes() {
        return attrSection;
    }

    @NotNull
    public List<String> getEndCommandWinners() {
        return endCommandWinners;
    }

    @NotNull
    public List<String> getEndCommandLosers() {
        return endCommandLosers;
    }

    public boolean isRenderGuiOnDeath() {
        return renderGuiOnDeath;
    }

    @NotNull
    public List<String> getRemoteServers(){
        return remoteServers;
    }

    public boolean hasBungeecordSupport(){
        return bungeeSupport;
    }

    @Nullable
    public BattleFirework getEndFirework() {
        return endFirework;
    }

    public long getEndDelay() {
        return endDelay;
    }

    @Nullable
    public Rollback getRollback() {
        return rollback;
    }

    @NotNull
    public List<String> getWonReport() {
        return wonReport;
    }

    @NotNull
    public List<String> getLostReport() {
        return lostReport;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Arena arena = (Arena) o;
        return id.equals(arena.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public void inform(@NotNull InfoHolder holder) {
        InfoHolder modeInfo = new InfoHolder("mode_");
        getMode().inform(modeInfo);
        holder.inform("id", id)
                .inform("name", name)
                .inform("max_time", maxTime)
                .inform("max_players", maxPlayers)
                .inform("endDelay", endDelay)
                .inform("bungeecord", State.ENABLED.inCaseOf(bungeeSupport))
                .link(modeInfo);
    }

    @Nullable
    protected Object conf2schema(@Nullable Object value, ConfigSchema.Entry entry){
        if(value != null) {
            switch (entry.getKey()) {
                case "mode": {
                    return Mode.getMode((String) value);
                }
                case "final_exp_formula": {
                    finalExpExpression = (String) value;
                    return new ExpressionBuilder(finalExpExpression).variables("a", "b", "c", "d").build();
                }
                case "final_money_formula": {
                    finalMoneyExpression = (String) value;
                    return new ExpressionBuilder(finalMoneyExpression).variables("a", "b", "c", "d").build();
                }
                case "bungeecord.enabled": {
                    boolean b = (Boolean) value;
                    if(b && !ApiProvider.consume().hasBungeecordSupport()){
                        Bukkit.getLogger().warning(String.format("Looks like you have enabled Bungeecord support for arena `%s`. But please also enable it in general.yml as well. The option is now skipped for safe!", id));
                        return false;
                    }
                }
            }
        }
        return value;
    }

    @Nullable
    protected Object schema2conf(@Nullable Object value, ConfigSchema.Entry entry){
        if(value != null) {
            switch (entry.getKey()) {
                case "mode": {
                    return ((Mode) value).getId();
                }
                case "final_exp_formula": {
                    return finalExpExpression;
                }
                case "final_money_formula": {
                    return finalMoneyExpression;
                }
            }
        }
        return value;
    }
}
