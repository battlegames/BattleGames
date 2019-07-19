package dev.anhcraft.abm.system.providers;

import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.ext.BattleComponent;
import dev.anhcraft.abm.api.objects.Game;
import dev.anhcraft.abm.utils.StringUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class ChatProvider extends BattleComponent {
    public ChatProvider(BattlePlugin plugin) {
        super(plugin);
    }

    public boolean chat(Player player, String msg){
        Optional<Game> x = plugin.gameManager.getGame(player);
        if(x.isPresent()){
            Game g = x.get();
            switch (g.getPhase()){
                case WAITING:{
                    if(!g.getMode().isWaitingChatEnabled()) return false;
                    String q = StringUtil.formatPlaceholders(player, g.getArena()
                            .getMode().getWaitingChatFormat())
                            .replace("{__message__}", msg);
                    g.getPlayers().keySet().forEach(p -> p.sendMessage(q));
                    break;
                }
                default:{
                    if(!g.getMode().isPlayingChatEnabled()) return false;
                    String q = StringUtil.formatPlaceholders(player, g.getArena()
                            .getMode().getPlayingChatFormat())
                            .replace("{__message__}", msg);
                    g.getPlayers().keySet().forEach(p -> p.sendMessage(q));
                    break;
                }
            }
        } else {
            if(!plugin.getGeneralConf().getBoolean("default_chat.enabled")) return false;
            String q = Objects.requireNonNull(StringUtil.formatPlaceholders(player, plugin.getGeneralConf().getString("default_chat.format"))).replace("{__message__}", msg);
            Bukkit.getOnlinePlayers().stream()
                    .filter((Predicate<Player>) player1 -> !plugin.gameManager.getGame(player1).isPresent())
                    .forEach(p -> p.sendMessage(q));
        }
        return true;
    }

    public String getFormattedMessage(Player target, String localePath){
        return Objects.requireNonNull(StringUtil.formatPlaceholders(target,
                plugin.getLocaleConf().getString(localePath)));
    }

    public void sendPlayer(Player target, String localePath){
        target.sendMessage(getFormattedMessage(target, localePath));
    }

    public void sendPlayer(Player target, String localePath, ChatMessageType type){
        TextComponent c = new TextComponent(TextComponent.fromLegacyText(getFormattedMessage(target, localePath)));
        target.spigot().sendMessage(type, c);
    }

    public void sendPlayer(Player target, String localePath, Function<String, String> x) {
        target.sendMessage(x.apply(getFormattedMessage(target, localePath)));
    }

    public void sendPlayer(Player target, String localePath, ChatMessageType type, Function<String, String> x){
        String s = x.apply(getFormattedMessage(target, localePath));
        TextComponent c = new TextComponent(TextComponent.fromLegacyText(s));
        target.spigot().sendMessage(type, c);
    }

    public void sendConsole(String localePath){
        Bukkit.getConsoleSender().sendMessage(Objects.requireNonNull(
                plugin.getLocaleConf().getString(localePath)));
    }

    public void sendConsole(String localePath, Function<String, String> x){
        Bukkit.getConsoleSender().sendMessage(Objects.requireNonNull(
                x.apply(plugin.getLocaleConf().getString(localePath))));
    }
}
