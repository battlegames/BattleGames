package dev.anhcraft.abm.cmd;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.inventory.items.ItemType;
import dev.anhcraft.abm.api.inventory.items.AmmoModel;
import dev.anhcraft.abm.api.game.Arena;
import dev.anhcraft.abm.api.inventory.items.GunModel;
import dev.anhcraft.abm.api.inventory.items.MagazineModel;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

    @Default
    @CatchUnknown
    @HelpCommand
    public void help(CommandSender sender){
    }

    @Subcommand("setspawn")
    @CommandPermission("abm.setspawn")
    public void setSpawn(Player player){
        plugin.getServerData().setSpawnPoint(player.getLocation());
    }

    @Subcommand("spawn")
    @CommandPermission("abm.spawn")
    public void spawn(Player player){
        player.teleport(plugin.getServerData().getSpawnPoint());
    }

    @Subcommand("arena join")
    @CommandPermission("abm.arena.join")
    public void join(Player player, String arenaName){
        Optional<Arena> arena = plugin.getArena(arenaName);
        if(arena.isPresent()) plugin.gameManager.join(player, arena.get());
        else plugin.chatManager.sendPlayer(player, "arena.not_found");
    }

    @Subcommand("arena forcejoin")
    @CommandPermission("abm.arena.forcejoin")
    public void forceJoin(Player s, Player p, String arenaName){
        Optional<Arena> arena = plugin.getArena(arenaName);
        if(arena.isPresent()) plugin.gameManager.forceJoin(p, arena.get());
        else plugin.chatManager.sendPlayer(s, "arena.not_found");
    }

    @Subcommand("arena quit")
    @CommandPermission("abm.arena.quit")
    public void quit(Player player){
        plugin.gameManager.quit(player);
    }

    @Subcommand("tool position")
    @CommandPermission("abm.tool.position")
    public void pos(Player player){
        String s1 = String.format(plugin.chatManager.getFormattedMessage(player, "tool.position.message"), player.getWorld().getName(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());
        String s2 = String.format(plugin.chatManager.getFormattedMessage(player, "tool.position.location"), player.getWorld().getName(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());
        TextComponent c = new TextComponent(TextComponent.fromLegacyText(s1));
        c.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, s2));
        player.spigot().sendMessage(c);
    }

    @Subcommand("give gun")
    @CommandPermission("abm.give.gun")
    public void giveGun(Player player, String s, @co.aikar.commands.annotation.Optional Player r){
        r = (r == null ? player : r);
        Optional<GunModel> gun = plugin.getGunModel(s);
        if(gun.isPresent()) {
            plugin.getPlayerData(r).ifPresent(playerData ->
                    playerData.getInventory().getStorage(ItemType.GUN).put(s));
        } else plugin.chatManager.sendPlayer(player, "items.not_found");
    }

    @Subcommand("give magazine")
    @CommandPermission("abm.give.magazine")
    public void giveMagazine(Player player, String s, @co.aikar.commands.annotation.Optional Player r){
        r = (r == null ? player : r);
        Optional<MagazineModel> mag = plugin.getMagazineModel(s);
        if(mag.isPresent()) {
            plugin.getPlayerData(r).ifPresent(playerData ->
                    playerData.getInventory().getStorage(ItemType.MAGAZINE).put(s));
        } else plugin.chatManager.sendPlayer(player, "items.not_found");
    }

    @Subcommand("give ammo")
    @CommandPermission("abm.give.ammo")
    public void giveAmmo(Player player, String s, @co.aikar.commands.annotation.Optional Player r){
        r = (r == null ? player : r);
        Optional<AmmoModel> ammo = plugin.getAmmoModel(s);
        if(ammo.isPresent()) {
            plugin.getPlayerData(r).ifPresent(playerData ->
                    playerData.getInventory().getStorage(ItemType.AMMO).put(s));
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
