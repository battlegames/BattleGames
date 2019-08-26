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
        mode = Mode.valueOf(m.toUpperCase());
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
        renderGuiOnDeath = conf.getBoolean("render_gui_on_death");
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
        mode.inform(modeInfo);
        holder.inform("id", id)
                .inform("name", name)
                .inform("max_time", maxTime)
                .inform("max_players", maxPlayers)
                .link(modeInfo);
    }
}
