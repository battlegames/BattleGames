package dev.anhcraft.abm.system.providers;

import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.ext.BattleComponent;
import dev.anhcraft.abm.utils.StringUtil;
import org.bukkit.entity.Player;

import java.util.function.Function;

public class TitleProvider extends BattleComponent {
    public TitleProvider(BattlePlugin plugin) {
        super(plugin);
    }

    public void send(Player target, String titleLocalePath, String subTitleLocalePath){
        String s1 = StringUtil.formatPlaceholders(target, plugin.getLocaleConf().getString(titleLocalePath));
        String s2 = StringUtil.formatPlaceholders(target, plugin.getLocaleConf().getString(subTitleLocalePath));
        target.sendTitle(s1, s2, 10, 70, 20);
    }

    public void send(Player target, String titleLocalePath, String subTitleLocalePath, Function<String, String> x){
        String s1 = x.apply(StringUtil.formatPlaceholders(target, plugin.getLocaleConf().getString(titleLocalePath)));
        String s2 = x.apply(StringUtil.formatPlaceholders(target, plugin.getLocaleConf().getString(subTitleLocalePath)));
        target.sendTitle(s1, s2, 10, 70, 20);
    }
}
