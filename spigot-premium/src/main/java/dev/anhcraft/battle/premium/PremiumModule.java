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

package dev.anhcraft.battle.premium;

import dev.anhcraft.battle.BattlePlugin;
import dev.anhcraft.battle.api.arena.mode.Mode;
import dev.anhcraft.battle.premium.system.controllers.CTFController;
import dev.anhcraft.battle.premium.system.controllers.TeamDeathmatchController;
import dev.anhcraft.battle.premium.system.integrations.SWMIntegration;
import dev.anhcraft.battle.system.IPremiumModule;

public class PremiumModule implements IPremiumModule {
    @Override
    public void onIntegrate(BattlePlugin plugin) {
        if(plugin.getServer().getPluginManager().isPluginEnabled("SlimeWorldManager")){
            plugin.slimeWorldManagerSupport = true;
            plugin.SWMIntegration = new SWMIntegration(plugin);
            plugin.getLogger().info("Hooked to SlimeWorldManager");
        }
    }

    @Override
    public void onInitSystem(BattlePlugin plugin) {
        plugin.arenaManager.initController(Mode.TEAM_DEATHMATCH, new TeamDeathmatchController(plugin));
        plugin.arenaManager.initController(Mode.CTF, new CTFController(plugin));
    }

    @Override
    public void onInitConfig(BattlePlugin plugin) {

    }

    @Override
    public void onRegisterEvents(BattlePlugin plugin) {

    }

    @Override
    public void onRegisterTasks(BattlePlugin plugin) {

    }

    @Override
    public void onDisable(BattlePlugin plugin) {

    }
}
