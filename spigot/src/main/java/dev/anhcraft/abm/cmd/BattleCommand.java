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
package dev.anhcraft.abm.cmd;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.game.Arena;
import dev.anhcraft.abm.api.game.Game;
import dev.anhcraft.abm.api.inventory.items.*;
import dev.anhcraft.abm.api.misc.info.InfoHolder;
import dev.anhcraft.abm.utils.LocationUtil;
import dev.anhcraft.abm.utils.PlaceholderUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@CommandAlias("abm|b|battle")
public class BattleCommand extends BaseCommand{
    private BattlePlugin plugin;

    public BattleCommand(BattlePlugin plugin){
        this.plugin = plugin;
    }

    public BattlePlugin getPlugin() {
        return plugin;
    }

    @CatchUnknown
    @HelpCommand
    public void help(CommandSender sender, CommandHelp help){
        help.showHelp();
    }

    @Subcommand("setspawn")
    @CommandPermission("abm.setspawn")
    public void setSpawn(Player player){
        Location loc = player.getLocation();
        plugin.getServerData().setSpawnPoint(loc);
        plugin.chatManager.sendPlayer(player, "server.set_spawn_success", s -> String.format(s, LocationUtil.toString(loc)));
    }

    @Subcommand("spawn")
    @CommandPermission("abm.spawn")
    public void spawn(Player player){
        player.teleport(plugin.getServerData().getSpawnPoint());
    }

    @Subcommand("game list")
    @CommandPermission("abm.game.list")
    public void listGames(CommandSender sender){
        Collection<Game> q = plugin.gameManager.getGames();
        plugin.chatManager.send(sender, "game.list_header", ChatMessageType.CHAT, str -> String.format(str, Integer.toString(q.size())));
        q.forEach(game -> {
            InfoHolder holder = new InfoHolder("game_");
            game.inform(holder);
            Map<String, String> map = plugin.mapInfo(holder);
            plugin.chatManager.send(sender, "game.list_section", ChatMessageType.CHAT, x -> PlaceholderUtils.formatInfo(x, map));
        });
    }

    @Subcommand("arena menu")
    @CommandPermission("abm.arena.menu")
    public void arenaMenu(Player player){
        plugin.guiManager.openTopInventory(player, "arena_chooser");
    }

    @Subcommand("arena join")
    @CommandPermission("abm.arena.join")
    @CommandCompletion("@arena")
    public void join(Player player, String arena, @co.aikar.commands.annotation.Optional Player target){
        Player t = (target == null) ? player : target;
        Optional<Arena> ao = plugin.getArena(arena);
        if(ao.isPresent()) {
            if(plugin.gameManager.join(t, ao.get()))
                plugin.chatManager.sendPlayer(player, "arena.join_success", str ->
                        String.format(str, t.getName()));
            else
                plugin.chatManager.sendPlayer(player, "arena.join_failed", str ->
                        String.format(str, t.getName()));
        }
        else plugin.chatManager.sendPlayer(player, "arena.not_found");
    }

    @Subcommand("arena quit")
    @CommandPermission("abm.arena.quit")
    @CommandCompletion("@players")
    public void quit(Player player, @co.aikar.commands.annotation.Optional Player target){
        Player t = (target == null) ? player : target;
        if(plugin.gameManager.quit(t))
            plugin.chatManager.sendPlayer(player, "arena.quit_success", str ->
                    String.format(str, t.getName()));
        else
            plugin.chatManager.sendPlayer(player, "arena.quit_failed", str ->
                    String.format(str, t.getName()));
    }

    @Subcommand("tool position")
    @CommandPermission("abm.tool.position")
    public void pos(Player player){
        String s1 = String.format(plugin.chatManager.getFormattedMessages(player, "tool.position.message").get(0), player.getWorld().getName(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());
        String s2 = String.format(plugin.chatManager.getFormattedMessages(player, "tool.position.location").get(0), player.getWorld().getName(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());
        TextComponent c = new TextComponent(TextComponent.fromLegacyText(s1));
        c.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, s2));
        player.spigot().sendMessage(c);
    }

    @Subcommand("give gun")
    @CommandPermission("abm.give.gun")
    @CommandCompletion("@gun @players")
    public void giveGun(Player player, String id, @co.aikar.commands.annotation.Optional Player target){
        target = (target == null ? player : target);
        Optional<GunModel> gun = plugin.getGunModel(id);
        if(gun.isPresent()) {
            plugin.getPlayerData(target).ifPresent(playerData ->
                    playerData.getInventory().getStorage(ItemType.GUN).put(id));
            String receiver = target.getName();
            plugin.chatManager.sendPlayer(player, "items.given", str -> String.format(str, id, receiver));
        } else plugin.chatManager.sendPlayer(player, "items.not_found");
    }

    @Subcommand("give magazine")
    @CommandPermission("abm.give.magazine")
    @CommandCompletion("@magazine @players")
    public void giveMagazine(Player player, String id, @co.aikar.commands.annotation.Optional Player target){
        target = (target == null ? player : target);
        Optional<MagazineModel> mag = plugin.getMagazineModel(id);
        if(mag.isPresent()) {
            plugin.getPlayerData(target).ifPresent(playerData ->
                    playerData.getInventory().getStorage(ItemType.MAGAZINE).put(id));
            String receiver = target.getName();
            plugin.chatManager.sendPlayer(player, "items.given", str -> String.format(str, id, receiver));
        } else plugin.chatManager.sendPlayer(player, "items.not_found");
    }

    @Subcommand("give ammo")
    @CommandPermission("abm.give.ammo")
    @CommandCompletion("@ammo @players")
    public void giveAmmo(Player player, String id, @co.aikar.commands.annotation.Optional Player target){
        target = (target == null ? player : target);
        Optional<AmmoModel> ammo = plugin.getAmmoModel(id);
        if(ammo.isPresent()) {
            plugin.getPlayerData(target).ifPresent(playerData ->
                    playerData.getInventory().getStorage(ItemType.AMMO).put(id));
            String receiver = target.getName();
            plugin.chatManager.sendPlayer(player, "items.given", str -> String.format(str, id, receiver));
        } else plugin.chatManager.sendPlayer(player, "items.not_found");
    }

    @Subcommand("give scope")
    @CommandPermission("abm.give.scope")
    @CommandCompletion("@scope @players")
    public void giveScope(Player player, String id, @co.aikar.commands.annotation.Optional Player target){
        target = (target == null ? player : target);
        Optional<ScopeModel> sc = plugin.getScopeModel(id);
        if(sc.isPresent()) {
            plugin.getPlayerData(target).ifPresent(playerData ->
                    playerData.getInventory().getStorage(ItemType.SCOPE).put(id));
            String receiver = target.getName();
            plugin.chatManager.sendPlayer(player, "items.given", str -> String.format(str, id, receiver));
        } else plugin.chatManager.sendPlayer(player, "items.not_found");
    }

    @Subcommand("inv")
    public void inv(Player player){
        plugin.guiManager.openTopInventory(player, "inventory_menu");
    }

    @Subcommand("kit")
    public void kit(Player player){
        plugin.guiManager.openTopInventory(player, "kit_menu");
    }
}
