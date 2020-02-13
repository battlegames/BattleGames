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
package dev.anhcraft.battle.api.chat;

import dev.anhcraft.battle.utils.info.InfoReplacer;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface ChatManager {
    boolean chat(@NotNull Player player, @NotNull String message);

    default void sendPlayer(@NotNull Player player, @NotNull String localePath){
        sendPlayer(player, localePath, ChatMessageType.CHAT, null);
    }

    default void sendPlayer(@NotNull Player player, @NotNull String localePath, @Nullable InfoReplacer infoReplacer){
        sendPlayer(player, localePath, ChatMessageType.CHAT, infoReplacer);
    }

    void sendPlayer(@NotNull Player player, @NotNull String localePath, @NotNull ChatMessageType type, @Nullable InfoReplacer infoReplacer);

    default void sendPlayers(@NotNull Collection<Player> players, @NotNull String localePath){
        sendPlayer(players, localePath, ChatMessageType.CHAT, null);
    }

    void sendPlayer(@NotNull Collection<Player> players, @NotNull String localePath, @NotNull ChatMessageType type, @Nullable InfoReplacer infoReplacer);

    default void sendConsole(@NotNull String localePath){
        send(Bukkit.getConsoleSender(), localePath, null);
    }

    default void sendConsole(@NotNull String localePath, @Nullable InfoReplacer infoReplacer) {
        send(Bukkit.getConsoleSender(), localePath, infoReplacer);
    }

    default void send(CommandSender commandSender, @NotNull String localePath) {
        send(commandSender, localePath, null);
    }

    void send(CommandSender commandSender, @NotNull String localePath, @Nullable InfoReplacer infoReplacer);

    void send(CommandSender commandSender, @NotNull String localePath, @NotNull ChatMessageType type, @Nullable InfoReplacer infoReplacer);
}
