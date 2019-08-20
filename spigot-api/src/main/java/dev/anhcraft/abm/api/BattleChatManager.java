package dev.anhcraft.abm.api;

import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.entity.Player;

import java.util.function.Function;
import java.util.function.UnaryOperator;

public interface BattleChatManager {
    String getFormattedMessage(Player target, String localePath);

    default void sendPlayer(Player target, String localePath){
        sendPlayer(target, localePath, ChatMessageType.CHAT, UnaryOperator.identity());
    }

    default void sendPlayer(Player target, String localePath, ChatMessageType type){
        sendPlayer(target, localePath, type, UnaryOperator.identity());
    }

    default void sendPlayer(Player target, String localePath, Function<String, String> x){
        sendPlayer(target, localePath, ChatMessageType.CHAT, x);
    }

    void sendPlayer(Player target, String localePath, ChatMessageType type, Function<String, String> x);

    default void sendConsole(String localePath){
        sendConsole(localePath, UnaryOperator.identity());
    }

    void sendConsole(String localePath, Function<String, String> x);
}
