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
package dev.anhcraft.abm.api.game;

import dev.anhcraft.craftkit.kits.abif.ABIF;
import dev.anhcraft.craftkit.kits.abif.PreparedItem;
import dev.anhcraft.abm.api.misc.info.InfoHolder;
import dev.anhcraft.abm.api.misc.info.Informative;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class Arena implements Informative {
    private String id;
    private String name;
    private Mode mode;
    private PreparedItem icon;
    private long maxTime;
    private int maxPlayers;
    private Expression finalExpCalculator;
    private Expression finalMoneyCalculator;
    private ConfigurationSection attrSection;
    private List<String> endCommandWinners;
    private List<String> endCommandLosers;
    private boolean renderGuiOnDeath;

    public Arena(@NotNull String id, @NotNull ConfigurationSection conf) {
        Validate.notNull(id, "Id must be non-null");
        Validate.notNull(conf, "Conf must be non-null");

        this.id = id;
        name = conf.getString("name");
        if(name == null) throw new NullPointerException("Name must be specified");
        String m = conf.getString("mode");
        if(m == null) throw new NullPointerException("Mode must be specified");
        mode = Mode.getMode(m);
        String fec = conf.getString("final_exp_formula");
        if(fec == null) throw new NullPointerException("Final experience formula must be specified");
        else finalExpCalculator = new ExpressionBuilder(fec).variables("a", "b", "c", "d").build();
        String fmc = conf.getString("final_money_formula");
        if(fmc == null) throw new NullPointerException("Final money formula must be specified");
        else finalMoneyCalculator = new ExpressionBuilder(fmc).variables("a", "b", "c", "d").build();

        ConfigurationSection ic = conf.getConfigurationSection("icon");
        if(ic == null) throw new NullPointerException("Icon must be specified");
        icon = ABIF.read(ic);
        maxTime = conf.getLong("max_time");
        maxPlayers = conf.getInt("max_players");

        attrSection = conf.getConfigurationSection("attr");
        if(attrSection == null) attrSection = new YamlConfiguration();
        endCommandWinners = conf.getStringList("end_commands.winners");
        endCommandLosers = conf.getStringList("end_commands.losers");
        renderGuiOnDeath = conf.getBoolean("render_gui_on_death", true);
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
        return icon.duplicate();
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
                .link(modeInfo);
    }
}
