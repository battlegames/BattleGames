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
package dev.anhcraft.battle.system.managers;

import dev.anhcraft.battle.BattleComponent;
import dev.anhcraft.battle.BattlePlugin;
import dev.anhcraft.battle.api.arena.game.GamePhase;
import dev.anhcraft.battle.api.arena.game.LocalGame;
import dev.anhcraft.battle.api.chat.BattleChat;
import dev.anhcraft.battle.api.chat.ChatManager;
import dev.anhcraft.battle.utils.PlaceholderUtil;
import dev.anhcraft.battle.utils.info.InfoReplacer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;

public class BattleChatManager extends BattleComponent implements ChatManager {
    public BattleChatManager(BattlePlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean chat(@NotNull Player player, @NotNull String message) {
        LocalGame g = plugin.arenaManager.getGame(player);
        if(g != null){
            if (g.getPhase() == GamePhase.WAITING) {
                if (!g.getMode().getWaitingChat().isEnabled()) return false;
                String q = PlaceholderUtil.formatPAPI(player, g.getArena()
                        .getMode().getWaitingChat().getFormat())
                        .replace("<message>", message);
                for (Player p : g.getPlayers().keySet()){
                    p.sendMessage(q);
                }
            } else {
                if (!g.getMode().getPlayingChat().isEnabled()) return false;
                String q = PlaceholderUtil.formatPAPI(player, g.getArena()
                        .getMode().getPlayingChat().getFormat())
                        .replace("<message>", message);
                for (Player p : g.getPlayers().keySet()){
                    p.sendMessage(q);
                }
            }
        } else {
            BattleChat bc = plugin.generalConf.getDefaultChat();
            if(bc == null || !bc.isEnabled()) return false;
            String q = Objects.requireNonNull(PlaceholderUtil.formatPAPI(player, bc.getFormat()))
                    .replace("<message>", message);
            for(Player p : Bukkit.getOnlinePlayers()){
                if(p.equals(player) || plugin.arenaManager.getGame(player) == null) {
                    p.sendMessage(q);
                }
            }
        }
        return true;
    }

    @NotNull
    private String getLocaleMsg(String localePath){
        String msg = plugin.getLocalizedMessage(localePath);
        if(msg == null) {
            msg = "[ Missing localized message: "+localePath+" ]";
        }
        return msg;
    }

    @Override
    public void sendPlayer(@NotNull Player player, @NotNull String localePath, @NotNull ChatMessageType type, @Nullable InfoReplacer infoReplacer) {
        String msg = getLocaleMsg(localePath);
        player.spigot().sendMessage(type, new TextComponent(TextComponent.fromLegacyText(
                PlaceholderUtil.formatPAPI(player, infoReplacer == null ? msg : infoReplacer.replace(msg))
        )));
    }

    @Override
    public void sendPlayer(@NotNull Collection<Player> players, @NotNull String localePath, @NotNull ChatMessageType type, @Nullable InfoReplacer infoReplacer) {
        String msg = getLocaleMsg(localePath);
        msg = infoReplacer == null ? msg : infoReplacer.replace(msg);
        for(Player player : players){
            player.spigot().sendMessage(type, new TextComponent(TextComponent.fromLegacyText(
                    PlaceholderUtil.formatPAPI(player, msg)
            )));
        }
    }

    @Override
    public void send(CommandSender commandSender, @NotNull String localePath, @Nullable InfoReplacer infoReplacer) {
        String msg = getLocaleMsg(localePath);
        if(commandSender instanceof Player) {
            commandSender.sendMessage(PlaceholderUtil.formatPAPI((Player) commandSender, infoReplacer == null ? msg : infoReplacer.replace(msg)));
        } else {
            commandSender.sendMessage(infoReplacer == null ? msg : infoReplacer.replace(msg));
        }
    }

    @Override
    public void send(CommandSender commandSender, @NotNull String localePath, @NotNull ChatMessageType type, @Nullable InfoReplacer infoReplacer) {
        if(commandSender instanceof Player){
            sendPlayer((Player) commandSender, localePath, type, infoReplacer);
        } else {
            send(commandSender, localePath, infoReplacer);
        }
    }
}
