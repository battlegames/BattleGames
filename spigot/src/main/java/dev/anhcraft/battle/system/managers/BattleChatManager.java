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
import dev.anhcraft.battle.api.chat.ChatManager;
import dev.anhcraft.battle.api.arena.game.LocalGame;
import dev.anhcraft.battle.api.chat.BattleChat;
import dev.anhcraft.battle.utils.PlaceholderUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class BattleChatManager extends BattleComponent implements ChatManager {
    public BattleChatManager(BattlePlugin plugin) {
        super(plugin);
    }

    public boolean chat(Player player, String msg){
        LocalGame g = plugin.arenaManager.getGame(player);
        if(g != null){
            switch (g.getPhase()){
                case WAITING:{
                    if(!g.getMode().isWaitingChatEnabled()) return false;
                    String q = PlaceholderUtil.formatPAPI(player, g.getArena()
                            .getMode().getWaitingChatFormat())
                            .replace("{__message__}", msg);
                    g.getPlayers().keySet().forEach(p -> p.sendMessage(q));
                    break;
                }
                default:{
                    if(!g.getMode().isPlayingChatEnabled()) return false;
                    String q = PlaceholderUtil.formatPAPI(player, g.getArena()
                            .getMode().getPlayingChatFormat())
                            .replace("{__message__}", msg);
                    g.getPlayers().keySet().forEach(p -> p.sendMessage(q));
                    break;
                }
            }
        } else {
            BattleChat bc = plugin.GENERAL_CONF.getDefaultChat();
            if(bc == null || !bc.isEnabled()) return false;
            String q = Objects.requireNonNull(PlaceholderUtil.formatPAPI(player, bc.getFormat()))
                    .replace("{__message__}", msg);
            for(Player p : Bukkit.getOnlinePlayers()){
                if(p.equals(player) || plugin.arenaManager.getGame(player) == null) {
                    p.sendMessage(q);
                }
            }
        }
        return true;
    }

    @Override
    public List<String> getFormattedMessages(String localePath, UnaryOperator<String> x) {
        Object s = plugin.getLocaleConf().get(localePath);
        if(s == null) {
            plugin.getLogger().warning(String.format("Locale path `%s` not found", localePath));
            return Collections.singletonList("null");
        }
        if(s instanceof Collection)
            return ((Collection<?>) s).stream().map((Function<Object, String>) o -> x.apply(String.valueOf(o))).collect(Collectors.toList());
        else
            return Collections.singletonList(x.apply(String.valueOf(s)));
    }

    @Override
    public List<String> getFormattedMessages(Player target, String localePath, UnaryOperator<String> x){
        Object s = plugin.getLocaleConf().get(localePath);
        if(s == null) {
            plugin.getLogger().warning(String.format("Locale path `%s` not found", localePath));
            return Collections.singletonList("null");
        }
        if(s instanceof Collection)
            return PlaceholderUtil.formatPAPI(target, ((Collection<?>) s).stream().map((Function<Object, String>) o -> x.apply(String.valueOf(o))).collect(Collectors.toList()));
        else
            return Collections.singletonList(PlaceholderUtil.formatPAPI(target, x.apply(String.valueOf(s))));
    }

    @Override
    public void sendPlayer(Player target, String localePath, ChatMessageType type, UnaryOperator<String> x){
        getFormattedMessages(target, localePath, x).forEach(s -> {
            TextComponent c = new TextComponent(TextComponent.fromLegacyText(s));
            target.spigot().sendMessage(type, c);
        });
    }

    @Override
    public void sendConsole(String localePath, UnaryOperator<String> x){
        getFormattedMessages(localePath, x).forEach(s -> Bukkit.getConsoleSender().sendMessage(s));
    }
}
