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
package dev.anhcraft.abm.api;

import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.UnaryOperator;

public interface BattleChatManager {
    default List<String> getFormattedMessages(String localePath){
        return getFormattedMessages(localePath, UnaryOperator.identity());
    }

    List<String> getFormattedMessages(String localePath, UnaryOperator<String> x);

    default List<String> getFormattedMessages(Player target, String localePath){
        return getFormattedMessages(target, localePath, UnaryOperator.identity());
    }

    List<String> getFormattedMessages(Player target, String localePath, UnaryOperator<String> x);

    default void send(CommandSender commandSender, String localePath){
        if(commandSender instanceof Player)
            sendPlayer((Player) commandSender, localePath, ChatMessageType.CHAT, UnaryOperator.identity());
        else
            sendConsole(localePath);
    }

    default void send(CommandSender commandSender, String localePath, UnaryOperator<String> x){
        if(commandSender instanceof Player)
            sendPlayer((Player) commandSender, localePath, ChatMessageType.CHAT, x);
        else
            sendConsole(localePath, x);
    }

    default void send(CommandSender commandSender, String localePath, ChatMessageType type){
        if(commandSender instanceof Player)
            sendPlayer((Player) commandSender, localePath, type, UnaryOperator.identity());
        else
            sendConsole(localePath);
    }

    default void send(CommandSender commandSender, String localePath, ChatMessageType type, UnaryOperator<String> x){
        if(commandSender instanceof Player)
            sendPlayer((Player) commandSender, localePath, type, x);
        else
            sendConsole(localePath, x);
    }

    default void sendPlayer(Player target, String localePath){
        sendPlayer(target, localePath, ChatMessageType.CHAT, UnaryOperator.identity());
    }

    default void sendPlayer(Player target, String localePath, ChatMessageType type){
        sendPlayer(target, localePath, type, UnaryOperator.identity());
    }

    default void sendPlayer(Player target, String localePath, UnaryOperator<String> x){
        sendPlayer(target, localePath, ChatMessageType.CHAT, x);
    }

    void sendPlayer(Player target, String localePath, ChatMessageType type, UnaryOperator<String> x);

    default void sendConsole(String localePath){
        sendConsole(localePath, UnaryOperator.identity());
    }

    void sendConsole(String localePath, UnaryOperator<String> x);
}
