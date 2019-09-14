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
package dev.anhcraft.abm.system.managers;

import dev.anhcraft.abm.BattleComponent;
import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.BattleChatManager;
import dev.anhcraft.abm.api.game.Game;
import dev.anhcraft.abm.utils.PlaceholderUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
    public List<String> getFormattedMessages(String localePath) {
        Object s = plugin.getLocaleConf().get(localePath);
        if(s == null) {
            plugin.getLogger().warning(String.format("Locale path `%s` not found", localePath));
            return Collections.singletonList("null");
        }
        if(s instanceof Collection)
            return ((Collection<?>) s).stream().map((Function<Object, String>) String::valueOf).collect(Collectors.toList());
        else
            return Collections.singletonList(String.valueOf(s));
    }

    @Override
    public List<String> getFormattedMessages(Player target, String localePath){
        Object s = plugin.getLocaleConf().get(localePath);
        if(s == null) {
            plugin.getLogger().warning(String.format("Locale path `%s` not found", localePath));
            return Collections.singletonList("null");
        }
        if(s instanceof Collection)
            return PlaceholderUtils.formatPAPI(target, ((Collection<?>) s).stream().map((Function<Object, String>) String::valueOf).collect(Collectors.toList()));
        else
            return Collections.singletonList(PlaceholderUtils.formatPAPI(target, String.valueOf(s)));
    }

    @Override
    public void sendPlayer(Player target, String localePath, ChatMessageType type, Function<String, String> x){
        getFormattedMessages(target, localePath).forEach(s -> {
            s = x.apply(s);
            TextComponent c = new TextComponent(TextComponent.fromLegacyText(s));
            target.spigot().sendMessage(type, c);
        });
    }

    @Override
    public void sendConsole(String localePath, Function<String, String> x){
        getFormattedMessages(localePath).forEach(s -> {
            Bukkit.getConsoleSender().sendMessage(x.apply(s));
        });
    }
}
