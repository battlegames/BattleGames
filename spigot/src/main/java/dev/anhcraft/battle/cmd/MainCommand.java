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
package dev.anhcraft.battle.cmd;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import dev.anhcraft.battle.BattlePlugin;
import dev.anhcraft.battle.api.game.Arena;
import dev.anhcraft.battle.api.game.Game;
import dev.anhcraft.battle.api.gui.NativeGui;
import dev.anhcraft.battle.api.inventory.items.*;
import dev.anhcraft.battle.api.misc.Booster;
import dev.anhcraft.battle.api.misc.Perk;
import dev.anhcraft.battle.utils.info.InfoHolder;
import dev.anhcraft.battle.api.storage.data.PlayerData;
import dev.anhcraft.battle.system.handlers.GunHandler;
import dev.anhcraft.battle.utils.LocationUtil;
import dev.anhcraft.battle.utils.PlaceholderUtil;
import dev.anhcraft.craftkit.utils.ItemUtil;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Map;

@CommandAlias("b|bg|battle|battlegames")
public class MainCommand extends BaseCommand{
    private BattlePlugin plugin;

    public MainCommand(BattlePlugin plugin){
        this.plugin = plugin;
    }

    @CatchUnknown
    @HelpCommand
    public void help(CommandSender sender, CommandHelp help){
        help.showHelp();
    }

    @Subcommand("setspawn")
    @CommandPermission("battle.setspawn")
    public void setSpawn(Player player){
        Location loc = player.getLocation();
        plugin.getServerData().setSpawnPoint(loc);
        plugin.chatManager.sendPlayer(player, "server.set_spawn_success", s -> String.format(s, LocationUtil.toString(loc)));
    }

    @Subcommand("spawn")
    @CommandPermission("battle.spawn")
    public void spawn(Player player){
        player.teleport(plugin.getServerData().getSpawnPoint());
    }

    @Subcommand("open")
    @CommandPermission("battle.open")
    @CommandCompletion("@gui")
    public void openGui(Player player, String name, @Optional Player target){
        plugin.guiManager.openTopGui(target == null ? player : target, name);
    }

    @Subcommand("game list")
    @CommandPermission("battle.game.list")
    public void listGames(CommandSender sender){
        Collection<Game> q = plugin.gameManager.listGames();
        plugin.chatManager.send(sender, "game.list_header", str -> String.format(str, Integer.toString(q.size())));
        q.forEach(game -> {
            InfoHolder holder = new InfoHolder("game_");
            game.inform(holder);
            Map<String, String> map = plugin.mapInfo(holder);
            plugin.chatManager.send(sender, "game.list_section", x -> PlaceholderUtil.formatInfo(x, map));
        });
    }

    @Subcommand("game destroy")
    @CommandPermission("battle.game.destroy")
    @CommandCompletion("@arena")
    public void destroyGame(CommandSender sender, String arena){
        Arena a = plugin.getArena(arena);
        if(a != null) {
            Game g = plugin.gameManager.getGame(a);
            if(g != null){
                plugin.gameManager.destroy(g);
                plugin.chatManager.send(sender, "game.destroy_success");
            } else
                plugin.chatManager.send(sender, "game.inactive_arena");
        } else
            plugin.chatManager.send(sender, "arena.not_found");
    }

    @Subcommand("arena menu")
    @CommandPermission("battle.arena.menu")
    public void arenaMenu(Player player){
        plugin.guiManager.openTopGui(player, NativeGui.ARENA_CHOOSER);
    }

    @Subcommand("arena join")
    @CommandPermission("battle.arena.join")
    @CommandCompletion("@arena")
    public void join(Player player, String arena, @Optional Player target){
        Player t = (target == null) ? player : target;
        Arena a = plugin.getArena(arena);
        if(a != null) {
            if(plugin.gameManager.join(t, a) != null)
                plugin.chatManager.sendPlayer(player, "arena.join_success", str ->
                        String.format(str, t.getName()));
            else
                plugin.chatManager.sendPlayer(player, "arena.join_failed", str ->
                        String.format(str, t.getName()));
        }
        else plugin.chatManager.sendPlayer(player, "arena.not_found");
    }

    @Subcommand("arena quit")
    @CommandPermission("battle.arena.quit")
    @CommandCompletion("@players")
    public void quit(Player player, @Optional Player target){
        Player t = (target == null) ? player : target;
        if(plugin.gameManager.quit(t))
            plugin.chatManager.sendPlayer(player, "arena.quit_success", str ->
                    String.format(str, t.getName()));
        else
            plugin.chatManager.sendPlayer(player, "arena.quit_failed", str ->
                    String.format(str, t.getName()));
    }

    @Subcommand("tool position")
    @CommandPermission("battle.tool.position")
    public void pos(Player player){
        Location loc = player.getLocation();
        String s1 = String.format(plugin.chatManager.getFormattedMessages(player, "tool.position.message").get(0), player.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        String s2 = String.format(plugin.chatManager.getFormattedMessages(player, "tool.position.location").get(0), player.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        TextComponent c = new TextComponent(TextComponent.fromLegacyText(s1));
        c.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, s2));
        player.spigot().sendMessage(c);
    }

    @Subcommand("tool exp2lv")
    @CommandPermission("battle.tool.exp.to.level")
    public void exp2lv(CommandSender sender, long exp){
        int lv = plugin.calculateLevel(exp);
        plugin.chatManager.send(sender, "tool.exp2lv", s -> String.format(s, exp, lv));
    }

    @Subcommand("tool lv2exp")
    @CommandPermission("battle.tool.level.to.exp")
    public void lv2exp(CommandSender sender, int lv){
        long exp = plugin.calculateExp(lv);
        plugin.chatManager.send(sender, "tool.lv2exp", s -> String.format(s, lv, exp));
    }

    @Subcommand("give exp")
    @CommandPermission("battle.give.exp")
    public void giveExp(CommandSender sender, long exp, Player player){
        PlayerData playerData = plugin.getPlayerData(player);
        if(playerData != null) {
            playerData.getExp().addAndGet(exp);
        } else {
            plugin.chatManager.sendPlayer(player, "player_data.not_found");
        }
    }

    @Subcommand("give gun")
    @CommandPermission("battle.give.gun")
    @CommandCompletion("@gun @players")
    public void giveGun(Player player, String id, @Optional Player target){
        target = (target == null ? player : target);
        GunModel gun = plugin.getGunModel(id);
        if(gun != null) {
            PlayerData playerData = plugin.getPlayerData(target);
            if(playerData != null){
                playerData.getInventory().getStorage(ItemType.GUN).put(id);
                String receiver = target.getName();
                plugin.chatManager.sendPlayer(player, "items.given", str -> String.format(str, id, receiver));
            } else {
                plugin.chatManager.sendPlayer(player, "player_data.not_found");
            }
        } else plugin.chatManager.sendPlayer(player, "items.not_found");
    }

    @Subcommand("give magazine")
    @CommandPermission("battle.give.magazine")
    @CommandCompletion("@magazine @players")
    public void giveMagazine(Player player, String id, @Optional Player target){
        target = (target == null ? player : target);
        MagazineModel mag = plugin.getMagazineModel(id);
        if(mag != null) {
            PlayerData playerData = plugin.getPlayerData(target);
            if(playerData != null){
                playerData.getInventory().getStorage(ItemType.MAGAZINE).put(id);
                String receiver = target.getName();
                plugin.chatManager.sendPlayer(player, "items.given", str -> String.format(str, id, receiver));
            } else {
                plugin.chatManager.sendPlayer(player, "player_data.not_found");
            }
        } else plugin.chatManager.sendPlayer(player, "items.not_found");
    }

    @Subcommand("give ammo")
    @CommandPermission("battle.give.ammo")
    @CommandCompletion("@ammo @players")
    public void giveAmmo(Player player, String id, @Optional Player target){
        target = (target == null ? player : target);
        AmmoModel ammo = plugin.getAmmoModel(id);
        if(ammo != null) {
            PlayerData playerData = plugin.getPlayerData(target);
            if(playerData != null) {
                playerData.getInventory().getStorage(ItemType.AMMO).put(id);
                String receiver = target.getName();
                plugin.chatManager.sendPlayer(player, "items.given", str -> String.format(str, id, receiver));
            } else {
                plugin.chatManager.sendPlayer(player, "player_data.not_found");
            }
        } else plugin.chatManager.sendPlayer(player, "items.not_found");
    }

    @Subcommand("give scope")
    @CommandPermission("battle.give.scope")
    @CommandCompletion("@scope @players")
    public void giveScope(Player player, String id, @Optional Player target){
        target = (target == null ? player : target);
        ScopeModel sc = plugin.getScopeModel(id);
        if(sc != null) {
            PlayerData playerData = plugin.getPlayerData(target);
            if(playerData != null) {
                playerData.getInventory().getStorage(ItemType.SCOPE).put(id);
                String receiver = target.getName();
                plugin.chatManager.sendPlayer(player, "items.given", str -> String.format(str, id, receiver));
            } else {
                plugin.chatManager.sendPlayer(player, "player_data.not_found");
            }
        } else plugin.chatManager.sendPlayer(player, "items.not_found");
    }

    @Subcommand("give grenade")
    @CommandPermission("battle.give.grenade")
    @CommandCompletion("@grenade @players")
    public void giveGrenade(Player player, String id, @Optional Player target){
        target = (target == null ? player : target);
        GrenadeModel gm = plugin.getGrenadeModel(id);
        if(gm != null) {
            PlayerData playerData = plugin.getPlayerData(target);
            if(playerData != null) {
                playerData.getInventory().getStorage(ItemType.GRENADE).put(id);
                String receiver = target.getName();
                plugin.chatManager.sendPlayer(player, "items.given", str -> String.format(str, id, receiver));
            } else {
                plugin.chatManager.sendPlayer(player, "player_data.not_found");
            }
        } else plugin.chatManager.sendPlayer(player, "items.not_found");
    }

    @Subcommand("give perk")
    @CommandPermission("battle.give.perk")
    @CommandCompletion("@perk @players")
    public void givePerk(Player player, String id, @Optional Player target){
        target = (target == null ? player : target);
        Perk perk = plugin.getPerk(id);
        if(perk != null) {
            perk.give(target);
            String receiver = target.getName();
            plugin.chatManager.sendPlayer(player, "perks.given", str -> String.format(str, receiver));
        } else plugin.chatManager.sendPlayer(player, "perks.not_found");
    }

    @Subcommand("give booster")
    @CommandPermission("battle.give.booster")
    @CommandCompletion("@booster @players")
    public void giveBooster(Player player, String id, @Optional Player target){
        target = (target == null ? player : target);
        Booster b = plugin.getBooster(id);
        if(b != null) {
            PlayerData playerData = plugin.getPlayerData(target);
            if(playerData != null) {
                playerData.getBoosters().putIfAbsent(id, System.currentTimeMillis());
                String receiver = target.getName();
                plugin.chatManager.sendPlayer(player, "booster.given", str -> String.format(str, receiver));
            } else {
                plugin.chatManager.sendPlayer(player, "player_data.not_found");
            }
        } else plugin.chatManager.sendPlayer(player, "booster.not_found");
    }

    @Subcommand("inv")
    public void inv(Player player){
        plugin.guiManager.openTopGui(player, NativeGui.PLAYER_INV);
    }

    @Subcommand("booster")
    public void booster(Player player){
        plugin.guiManager.openTopGui(player, NativeGui.BOOSTER_MENU);
    }

    @Subcommand("market")
    public void market(Player player){
        plugin.guiManager.openTopGui(player, NativeGui.MARKET_CATEGORY_MENU);
    }

    @Subcommand("clearinv")
    @CommandPermission("battle.clearinv")
    public void clearInv(Player player, @Optional OfflinePlayer target){
        target = (target == null ? player : target);
        PlayerData pd = plugin.getPlayerData(target);
        if(pd == null)
            plugin.chatManager.sendPlayer(player, "player_data.not_found");
        else {
            pd.getInventory().clearInventory();
            plugin.chatManager.sendPlayer(player, "inv.cleared");
        }
    }

    @Subcommand("gun reload")
    @CommandPermission("battle.gun.reload")
    public void reloadGun(Player player){
        ItemStack item = player.getInventory().getItemInMainHand();
        if(ItemUtil.isNull(item)){
            plugin.chatManager.sendPlayer(player, "items.is_null");
            return;
        }
        BattleItem bi = plugin.itemManager.read(item);
        if(bi instanceof Gun){
            Gun g = (Gun) bi;
            g.getMagazine().resetAmmo();
            player.getInventory().setItemInMainHand(plugin.getHandler(GunHandler.class).createGun(g, false));
            plugin.chatManager.sendPlayer(player, "gun.ammo_reloaded");
        } else {
            plugin.chatManager.sendPlayer(player, "items.not_gun");
        }
    }

    @Subcommand("kit")
    public void kit(Player player){
        plugin.guiManager.openTopGui(player, NativeGui.KIT_MENU);
    }
}