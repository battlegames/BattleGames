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

package dev.anhcraft.battle.premium.cmd;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import dev.anhcraft.battle.utils.ChatUtil;
import dev.anhcraft.battle.utils.PreparedItem;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

@CommandAlias("bx|battlex")
public class ExtendedCommand extends BaseCommand {
    @CatchUnknown
    @HelpCommand
    public void help(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("give medical_kit")
    @CommandPermission("battle.extended.give.medical_kit")
    public void giveMedicalKit(Player player, int amount, @Optional Player target) {
        Player t = (target == null) ? player : target;
        PreparedItem pi = new PreparedItem();
        pi.material(Material.STONE_SWORD);
        pi.name(ChatUtil.formatColorCodes("&f&lMedical Kit &c&l(❤)"));
        pi.damage((short) 1);
        pi.flags().add(ItemFlag.HIDE_UNBREAKABLE);
        pi.flags().add(ItemFlag.HIDE_ATTRIBUTES);
        pi.unbreakable(true);
        ItemStack itemStack = pi.build();
        for (int i = 0; i < amount; i++) {
            t.getInventory().addItem(itemStack.clone());
        }
    }

    @Subcommand("give adrenaline_shot")
    @CommandPermission("battle.extended.give.adrenaline_shot")
    public void giveAdrenalineShot(Player player, int amount, @Optional Player target) {
        Player t = (target == null) ? player : target;
        PreparedItem pi = new PreparedItem();
        pi.material(Material.STONE_SWORD);
        pi.name(ChatUtil.formatColorCodes("&4&lAdrenaline Shot"));
        pi.damage((short) 4);
        pi.flags().add(ItemFlag.HIDE_UNBREAKABLE);
        pi.flags().add(ItemFlag.HIDE_ATTRIBUTES);
        pi.unbreakable(true);
        ItemStack itemStack = pi.build();
        for (int i = 0; i < amount; i++) {
            t.getInventory().addItem(itemStack.clone());
        }
    }
}
