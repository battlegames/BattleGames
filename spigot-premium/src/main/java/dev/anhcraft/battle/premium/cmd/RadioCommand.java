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
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import dev.anhcraft.battle.api.BattleApi;
import dev.anhcraft.battle.api.arena.game.LocalGame;
import dev.anhcraft.battle.api.arena.game.controllers.BedWarController;
import dev.anhcraft.battle.api.arena.game.controllers.GameController;
import dev.anhcraft.battle.api.arena.game.controllers.TeamDeathmatchController;
import dev.anhcraft.battle.api.arena.team.ABTeam;
import dev.anhcraft.battle.api.arena.team.BWTeam;
import dev.anhcraft.battle.api.arena.team.TeamManager;
import dev.anhcraft.battle.premium.PremiumModule;
import dev.anhcraft.battle.utils.PlaceholderUtil;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

@CommandAlias("radio|r|rd")
public class RadioCommand extends BaseCommand {
    @Subcommand("radio")
    @CatchUnknown
    @Default
    public void send(Player player, String[] msgs) {
        LocalGame game = BattleApi.getInstance().getArenaManager().getGame(player);
        if (game == null) {
            player.sendMessage(BattleApi.getInstance().getLocalizedMessage("radio.not_in_game"));
            return;
        }
        GameController mode = game.getMode().getController();
        if (mode instanceof TeamDeathmatchController) {
            TeamManager<ABTeam> tm = ((TeamDeathmatchController) mode).getTeamManager(game);
            if (tm == null) {
                player.sendMessage(BattleApi.getInstance().getLocalizedMessage("radio.no_team"));
                return;
            }
            ABTeam abt = tm.getTeam(player);
            if (abt == null) {
                player.sendMessage(BattleApi.getInstance().getLocalizedMessage("radio.no_team"));
                return;
            }
            String q = Objects.requireNonNull(PlaceholderUtil.formatPAPI(player, PremiumModule.getInstance().getRadioConfigManagerX().getRadioSettings().getMessageFormat())).replace("<message>", String.join(" ", msgs));
            List<Player> players = tm.getPlayers(abt);
            for (Player p : players) {
                p.sendMessage(q);
            }
        } else if(mode instanceof BedWarController) {
            TeamManager<BWTeam> tm = ((BedWarController) mode).getTeamManager(game);
            if (tm == null) {
                player.sendMessage(BattleApi.getInstance().getLocalizedMessage("radio.no_team"));
                return;
            }
            BWTeam abt = tm.getTeam(player);
            if (abt == null) {
                player.sendMessage(BattleApi.getInstance().getLocalizedMessage("radio.no_team"));
                return;
            }
            String q = Objects.requireNonNull(PlaceholderUtil.formatPAPI(player, PremiumModule.getInstance().getRadioConfigManagerX().getRadioSettings().getMessageFormat())).replace("<message>", String.join(" ", msgs));
            List<Player> players = tm.getPlayers(abt);
            for (Player p : players) {
                p.sendMessage(q);
            }
        } else {
            player.sendMessage(BattleApi.getInstance().getLocalizedMessage("radio.unsupported_mode"));
        }
    }
}
