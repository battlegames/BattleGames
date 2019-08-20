package dev.anhcraft.abm.system.managers;

import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.BattleComponent;
import dev.anhcraft.abm.api.BattleChatManager;
import dev.anhcraft.abm.api.game.Game;
import dev.anhcraft.abm.utils.PlaceholderUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class ChatManager extends BattleComponent implements BattleChatManager {
    public ChatManager(BattlePlugin plugin) {
        super(plugin);
    }

    public boolean chat(Player player, String msg){
        Optional<Game> x = plugin.gameManager.getGame(player);
        if(x.isPresent()){
            Game g = x.get();
            switch (g.getPhase()){
                case WAITING:{
                    if(!g.getMode().isWaitingChatEnabled()) return false;
                    String q = PlaceholderUtils.formatPAPI(player, g.getArena()
                            .getMode().getWaitingChatFormat())
                            .replace("{__message__}", msg);
                    g.getPlayers().keySet().forEach(p -> p.sendMessage(q));
                    break;
                }
                default:{
                    if(!g.getMode().isPlayingChatEnabled()) return false;
                    String q = PlaceholderUtils.formatPAPI(player, g.getArena()
                            .getMode().getPlayingChatFormat())
                            .replace("{__message__}", msg);
                    g.getPlayers().keySet().forEach(p -> p.sendMessage(q));
                    break;
                }
            }
        } else {
            if(!plugin.getGeneralConf().getBoolean("default_chat.enabled")) return false;
            String q = Objects.requireNonNull(PlaceholderUtils.formatPAPI(player, plugin.getGeneralConf().getString("default_chat.format"))).replace("{__message__}", msg);
            Bukkit.getOnlinePlayers().stream()
                    .filter((Predicate<Player>) player1 -> !plugin.gameManager.getGame(player1).isPresent())
                    .forEach(p -> p.sendMessage(q));
        }
        return true;
    }

    @Override
    public String getFormattedMessage(Player target, String localePath){
        return Objects.requireNonNull(PlaceholderUtils.formatPAPI(target,
                plugin.getLocaleConf().getString(localePath)));
    }

    @Override
    public void sendPlayer(Player target, String localePath, ChatMessageType type){
        TextComponent c = new TextComponent(TextComponent.fromLegacyText(getFormattedMessage(target, localePath)));
        target.spigot().sendMessage(type, c);
    }

    @Override
    public void sendPlayer(Player target, String localePath, ChatMessageType type, Function<String, String> x){
        String s = x.apply(getFormattedMessage(target, localePath));
        TextComponent c = new TextComponent(TextComponent.fromLegacyText(s));
        target.spigot().sendMessage(type, c);
    }

    @Override
    public void sendConsole(String localePath, Function<String, String> x){
        Bukkit.getConsoleSender().sendMessage(Objects.requireNonNull(
                x.apply(plugin.getLocaleConf().getString(localePath))));
    }
}
