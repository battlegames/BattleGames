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
import dev.anhcraft.battle.api.gui.NativeGui;
import dev.anhcraft.battle.api.market.Market;
import dev.anhcraft.battle.utils.ConfigHelper;
import dev.anhcraft.battle.utils.info.InfoHolder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

@CommandAlias("be|bge")
public class EditorCommand extends BaseCommand {
    private final BattlePlugin plugin;

    public EditorCommand(BattlePlugin plugin) {
        this.plugin = plugin;
    }

    @CatchUnknown
    @HelpCommand
    public void help(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("market open")
    @CommandPermission("battle.editor.market.open")
    public void marketOpen(Player player) {
        plugin.guiManager.openTopGui(player, NativeGui.MARKET_CATEGORY_MENU_EDITOR);
    }

    @Subcommand("market save")
    @CommandPermission("battle.editor.market.save")
    public void marketSave(CommandSender sender) {
        ConfigHelper.save(Market.class, plugin.marketConfigManager.getSettings(), plugin.getMarket());
        File f = new File(plugin.getEditorFolder(), "market." + System.currentTimeMillis() + ".yml");
        try {
            plugin.marketConfigManager.getSettings().save(f);
            plugin.chatManager.send(sender, "editor.market.saved", new InfoHolder("").inform("path", f.getAbsolutePath()).compile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
