package dev.anhcraft.abm.system.managers;

import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.BattleComponent;
import dev.anhcraft.abm.utils.PlaceholderUtils;
import org.bukkit.entity.Player;

import java.util.function.Function;
import java.util.function.UnaryOperator;

public class TitleManager extends BattleComponent {
    public TitleManager(BattlePlugin plugin) {
        super(plugin);
    }

    public void send(Player target, String titleLocalePath, String subTitleLocalePath){
        send(target, titleLocalePath, subTitleLocalePath, UnaryOperator.identity());
    }

    public void send(Player target, String titleLocalePath, String subTitleLocalePath, Function<String, String> x){
        String s1 = x.apply(PlaceholderUtils.formatPAPI(target, plugin.getLocaleConf().getString(titleLocalePath)));
        String s2 = x.apply(PlaceholderUtils.formatPAPI(target, plugin.getLocaleConf().getString(subTitleLocalePath)));
        target.sendTitle(s1, s2, 10, 70, 20);
    }
}
