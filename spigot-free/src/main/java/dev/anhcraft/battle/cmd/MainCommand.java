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
import dev.anhcraft.battle.api.arena.Arena;
import dev.anhcraft.battle.api.arena.game.Game;
import dev.anhcraft.battle.api.arena.game.LocalGame;
import dev.anhcraft.battle.api.gui.NativeGui;
import dev.anhcraft.battle.api.inventory.item.*;
import dev.anhcraft.battle.api.misc.Booster;
import dev.anhcraft.battle.api.misc.Perk;
import dev.anhcraft.battle.api.stats.natives.ExpStat;
import dev.anhcraft.battle.api.storage.data.PlayerData;
import dev.anhcraft.battle.system.debugger.BattleDebugger;
import dev.anhcraft.battle.utils.EntityUtil;
import dev.anhcraft.battle.utils.LocationUtil;
import dev.anhcraft.battle.utils.info.InfoHolder;
import dev.anhcraft.battle.utils.info.InfoReplacer;
import dev.anhcraft.craftkit.chat.Chat;
import dev.anhcraft.craftkit.utils.ItemUtil;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Objects;

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

    @Subcommand("info")
    @CommandPermission("battle.info")
    public void info(CommandSender sender){
        Chat.noPrefix()
                .message(sender, "&e&lBattleGames Minigame &7&lv"+plugin.getDescription().getVersion())
                .message(sender, "&d◈ Type: "+(plugin.premiumConnector.isSuccess() ? "&bPremium" : "&fFree"))
                .message(sender, "&d◈ Author: &fanhcraft")
                .message(sender, "&d◈ Discord: &fhttps://discord.gg/QSpc5xH")
                .message(sender, "&d◈ Spigot: &fhttps://spigotmc.org/resources/69463");
    }

    @Subcommand("setspawn")
    @CommandPermission("battle.setspawn")
    public void setSpawn(Player player){
        Location loc = player.getLocation();
        plugin.getServerData().setSpawnPoint(loc);
        plugin.chatManager.sendPlayer(player, "server.set_spawn_success", new InfoHolder("").inform("location", LocationUtil.toString(loc)).compile());
    }

    @Subcommand("spawn")
    @CommandPermission("battle.spawn")
    public void spawn(Player player){
        EntityUtil.teleport(player, plugin.getServerData().getSpawnPoint());
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
        Collection<Game> q = plugin.arenaManager.listGames();
        if(q.isEmpty()){
            plugin.chatManager.send(sender, "game.list_header_none");
            return;
        }
        plugin.chatManager.send(sender, "game.list_header", new InfoHolder("").inform("size", q.size()).compile());
        for (Game game : q){
            InfoHolder holder = new InfoHolder("game_");
            game.inform(holder);
            plugin.chatManager.send(sender, "game.list_section", holder.compile());
        }
    }

    @Subcommand("game destroy")
    @CommandPermission("battle.game.destroy")
    @CommandCompletion("@arena")
    public void destroyGame(CommandSender sender, String arena){
        Arena a = plugin.getArena(arena);
        if(a != null) {
            Game g = plugin.arenaManager.getGame(a);
            if(g != null){
                plugin.arenaManager.destroy(g);
                plugin.chatManager.send(sender, "game.destroy_success");
            } else
                plugin.chatManager.send(sender, "game.inactive_arena");
        } else
            plugin.chatManager.send(sender, "arena.not_found");
    }

    @Subcommand("game end")
    @CommandPermission("battle.game.end")
    @CommandCompletion("@arena")
    public void endGame(CommandSender sender, String arena){
        Arena a = plugin.getArena(arena);
        if(a != null) {
            Game g = plugin.arenaManager.getGame(a);
            if(g != null){
                if(g instanceof LocalGame){
                    ((LocalGame) g).end();
                } else {
                    plugin.arenaManager.destroy(g);
                }
                plugin.chatManager.send(sender, "game.end_success");
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
            if(plugin.arenaManager.join(t, a) != null) {
                plugin.chatManager.sendPlayer(player, "arena.join_success", new InfoHolder("").inform("target", t.getName()).compile());
            } else {
                plugin.chatManager.sendPlayer(player, "arena.join_failed", new InfoHolder("").inform("target", t.getName()).compile());
            }
        }
        else plugin.chatManager.sendPlayer(player, "arena.not_found");
    }

    @Subcommand("arena quit")
    @CommandPermission("battle.arena.quit")
    @CommandCompletion("@players")
    public void quit(Player player, @Optional Player target){
        Player t = (target == null) ? player : target;
        if(plugin.arenaManager.quit(t)) {
            plugin.chatManager.sendPlayer(player, "arena.quit_success", new InfoHolder("").inform("target", t.getName()).compile());
        } else {
            plugin.chatManager.sendPlayer(player, "arena.quit_failed", new InfoHolder("").inform("target", t.getName()).compile());
        }
    }

    @Subcommand("tool position")
    @CommandPermission("battle.tool.position")
    public void pos(Player player){
        Location loc = player.getLocation();
        InfoReplacer replacer = new InfoHolder("")
                .inform("location", LocationUtil.toString(loc))
                .compile();
        String s1 = Objects.requireNonNull(plugin.getLocalizedMessage("tool.position.message"));
        String s2 = Objects.requireNonNull(plugin.getLocalizedMessage("tool.position.location"));
        TextComponent c = new TextComponent(TextComponent.fromLegacyText(replacer.replace(s1)));
        c.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, replacer.replace(s2)));
        player.spigot().sendMessage(c);
    }

    @Subcommand("tool exp2lv")
    @CommandPermission("battle.tool.exp.to.level")
    public void exp2lv(CommandSender sender, long exp){
        int lv = plugin.calculateLevel(exp);
        plugin.chatManager.send(sender, "tool.exp2lv", new InfoHolder("").inform("exp", exp).inform("level", lv).compile());
    }

    @Subcommand("tool lv2exp")
    @CommandPermission("battle.tool.level.to.exp")
    public void lv2exp(CommandSender sender, int lv){
        long exp = plugin.calculateExp(lv);
        plugin.chatManager.send(sender, "tool.lv2exp", new InfoHolder("").inform("exp", exp).inform("level", lv).compile());
    }

    @Subcommand("tool spawn")
    @CommandPermission("battle.tool.spawn")
    @CommandCompletion("@entityTypes")
    public void spawn(Player player, EntityType entityType, int amount, @Optional Double health){
        Location loc = player.getLocation();
        for (int i = 0; i < amount; i++) {
            Entity entity = player.getWorld().spawnEntity(loc, entityType);
            if (health != null && entity instanceof LivingEntity) {
                LivingEntity le = (LivingEntity) entity;
                le.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
                le.setHealth(Math.max(health, 1));
            }
        }
    }

    @Subcommand("give exp")
    @CommandPermission("battle.give.exp")
    public void giveExp(CommandSender sender, long exp, Player player){
        PlayerData playerData = plugin.getPlayerData(player);
        if(playerData != null) {
            playerData.getStats().of(ExpStat.class).increase(player, exp);
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
                plugin.chatManager.sendPlayer(player, "items.given", new InfoHolder("").inform("id", id).inform("receiver", receiver).compile());
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
                plugin.chatManager.sendPlayer(player, "items.given", new InfoHolder("").inform("id", id).inform("receiver", receiver).compile());
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
                plugin.chatManager.sendPlayer(player, "items.given", new InfoHolder("").inform("id", id).inform("receiver", receiver).compile());
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
                plugin.chatManager.sendPlayer(player, "items.given", new InfoHolder("").inform("id", id).inform("receiver", receiver).compile());
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
                plugin.chatManager.sendPlayer(player, "items.given", new InfoHolder("").inform("id", id).inform("receiver", receiver).compile());
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
            plugin.chatManager.sendPlayer(player, "perks.given", new InfoHolder("").inform("id", id).inform("receiver", receiver).compile());
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
                plugin.chatManager.sendPlayer(player, "booster.given", new InfoHolder("").inform("id", id).inform("receiver", receiver).compile());
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
        BattleItem<?> bi = plugin.itemManager.read(item);
        if(bi instanceof Gun){
            Gun g = (Gun) bi;
            g.getMagazine().resetAmmo();
            player.getInventory().setItemInMainHand(plugin.gunManager.createGun(g, false));
            plugin.chatManager.sendPlayer(player, "gun.ammo_reloaded");
        } else {
            plugin.chatManager.sendPlayer(player, "items.not_gun");
        }
    }

    @Subcommand("kit")
    public void kit(Player player){
        plugin.guiManager.openTopGui(player, NativeGui.KIT_MENU);
    }

    @Subcommand("advancement")
    public void adv(Player player){
        plugin.guiManager.openTopGui(player, NativeGui.ADVANCEMENT_RECORDS);
    }

    @Subcommand("debug 3min")
    @CommandPermission("battle.debug")
    public void debug3(CommandSender sender){
        if(BattleDebugger.create(s -> plugin.chatManager.send(sender, "debug.done", new InfoHolder("").inform("path", s).compile()), 20 * 60 * 3)){
            plugin.chatManager.send(sender, "debug.created_success");
        } else {
            plugin.chatManager.send(sender, "debug.created_failure");
        }
    }

    @Subcommand("debug 5min")
    @CommandPermission("battle.debug")
    public void debug5(CommandSender sender){
        if(BattleDebugger.create(s -> plugin.chatManager.send(sender, "debug.done", new InfoHolder("").inform("path", s).compile()), 20 * 60 * 5)){
            plugin.chatManager.send(sender, "debug.created_success");
        } else {
            plugin.chatManager.send(sender, "debug.created_failure");
        }
    }

    @Subcommand("debug 15min")
    @CommandPermission("battle.debug")
    public void debug15(CommandSender sender){
        if(BattleDebugger.create(s -> plugin.chatManager.send(sender, "debug.done", new InfoHolder("").inform("path", s).compile()), 20 * 60 * 15)){
            plugin.chatManager.send(sender, "debug.created_success");
        } else {
            plugin.chatManager.send(sender, "debug.created_failure");
        }
    }
}
