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
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import dev.anhcraft.battle.BattlePlugin;
import dev.anhcraft.battle.api.arena.game.LocalGame;
import dev.anhcraft.battle.api.arena.game.controllers.BedWarController;
import dev.anhcraft.battle.api.arena.game.controllers.GameController;
import dev.anhcraft.battle.api.arena.game.controllers.MobRescueController;
import dev.anhcraft.battle.api.arena.game.controllers.TeamDeathmatchController;
import dev.anhcraft.battle.api.arena.team.ABTeam;
import dev.anhcraft.battle.api.arena.team.BWTeam;
import dev.anhcraft.battle.api.arena.team.MRTeam;
import dev.anhcraft.battle.api.arena.team.TeamManager;
import dev.anhcraft.battle.utils.PlaceholderUtil;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

@CommandAlias("radio|r|rd")
public class RadioCommand extends BaseCommand {
    private final BattlePlugin plugin;

    public RadioCommand(BattlePlugin plugin) {
        this.plugin = plugin;
    }

    @Subcommand("radio")
    @CatchUnknown
    @Default
    public void send(Player player, String[] msgs) {
        LocalGame game = plugin.getArenaManager().getGame(player);
        if (game == null) {
            plugin.chatManager.sendPlayer(player, "radio.not_in_game");
            return;
        }
        GameController mode = game.getMode().getController();
        if (mode instanceof TeamDeathmatchController) {
            TeamManager<ABTeam> tm = ((TeamDeathmatchController) mode).getTeamManager(game);
            if (tm == null) {
                plugin.chatManager.sendPlayer(player, "radio.no_team");
                return;
            }
            ABTeam abt = tm.getTeam(player);
            if (abt == null) {
                plugin.chatManager.sendPlayer(player, "radio.no_team");
                return;
            }
            String q = Objects.requireNonNull(PlaceholderUtil.formatPAPI(player, plugin.generalConf.getRadioMessageFormat())).replace("<message>", String.join(" ", msgs));
            List<Player> players = tm.getPlayers(abt);
            for (Player p : players) {
                p.sendMessage(q);
            }
        } else if (mode instanceof BedWarController) {
            TeamManager<BWTeam> tm = ((BedWarController) mode).getTeamManager(game);
            if (tm == null) {
                plugin.chatManager.sendPlayer(player, "radio.no_team");
                return;
            }
            BWTeam abt = tm.getTeam(player);
            if (abt == null) {
                plugin.chatManager.sendPlayer(player, "radio.no_team");
                return;
            }
            String q = Objects.requireNonNull(PlaceholderUtil.formatPAPI(player, plugin.generalConf.getRadioMessageFormat())).replace("<message>", String.join(" ", msgs));
            List<Player> players = tm.getPlayers(abt);
            for (Player p : players) {
                p.sendMessage(q);
            }
        } else if (mode instanceof MobRescueController) {
            TeamManager<MRTeam> tm = ((MobRescueController) mode).getTeamManager(game);
            if (tm == null) {
                plugin.chatManager.sendPlayer(player, "radio.no_team");
                return;
            }
            MRTeam mrt = tm.getTeam(player);
            if (mrt == null) {
                plugin.chatManager.sendPlayer(player, "radio.no_team");
                return;
            }
            String q = Objects.requireNonNull(PlaceholderUtil.formatPAPI(player, plugin.generalConf.getRadioMessageFormat())).replace("<message>", String.join(" ", msgs));
            List<Player> players = tm.getPlayers(mrt);
            for (Player p : players) {
                p.sendMessage(q);
            }
        } else {
            plugin.chatManager.sendPlayer(player, "radio.unsupported_mode");
        }
    }
}
