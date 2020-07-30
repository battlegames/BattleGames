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

import co.aikar.commands.PaperCommandManager;
import dev.anhcraft.battle.BattlePlugin;
import dev.anhcraft.battle.api.arena.game.Mode;
import dev.anhcraft.battle.premium.cmd.ExtendedCommand;
import dev.anhcraft.battle.premium.cmd.RadioCommand;
import dev.anhcraft.battle.premium.config.managers.ArenaConfigManagerX;
import dev.anhcraft.battle.premium.config.managers.ItemConfigManagerX;
import dev.anhcraft.battle.premium.config.managers.RadioConfigManagerX;
import dev.anhcraft.battle.premium.config.managers.WorldConfigManagerX;
import dev.anhcraft.battle.premium.system.controllers.CTFControllerImpl;
import dev.anhcraft.battle.premium.system.controllers.MRControllerImpl;
import dev.anhcraft.battle.premium.system.controllers.TDMControllerImpl;
import dev.anhcraft.battle.premium.system.listeners.GameListener;
import dev.anhcraft.battle.premium.system.listeners.PlayerListener;
import dev.anhcraft.battle.premium.system.listeners.WorldListener;
import dev.anhcraft.battle.premium.tasks.Task;
import dev.anhcraft.battle.system.IPremiumModule;
import org.jetbrains.annotations.NotNull;

public class PremiumModule implements IPremiumModule {
    private static PremiumModule instance;
    private ArenaConfigManagerX arenaConfigManagerX;
    private ItemConfigManagerX itemConfigManagerX;
    private RadioConfigManagerX radioConfigManagerX;
    private WorldConfigManagerX worldConfigManagerX;

    @NotNull
    public static PremiumModule getInstance() {
        return instance;
    }

    public PremiumModule(){
        instance = this;
    }

    @Override
    public void onIntegrate(BattlePlugin plugin) {
    }

    @Override
    public void onInitSystem(BattlePlugin plugin) {
        // configs
        arenaConfigManagerX = new ArenaConfigManagerX();
        itemConfigManagerX = new ItemConfigManagerX();
        radioConfigManagerX = new RadioConfigManagerX();
        worldConfigManagerX = new WorldConfigManagerX();
        // gamemode
        plugin.arenaManager.initController(Mode.TEAM_DEATHMATCH, new TDMControllerImpl(plugin));
        plugin.arenaManager.initController(Mode.CTF, new CTFControllerImpl(plugin));
        plugin.arenaManager.initController(Mode.MOB_RESCUE, new MRControllerImpl(plugin));
    }

    @Override
    public void onReloadConfig(BattlePlugin plugin) {
        arenaConfigManagerX.reloadConfig();
        itemConfigManagerX.reloadConfig();
        radioConfigManagerX.reloadConfig();
        worldConfigManagerX.reloadConfig();
    }

    @Override
    public void onRegisterEvents(BattlePlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new PlayerListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new WorldListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new GameListener(), plugin);
    }

    @Override
    public void onRegisterTasks(BattlePlugin plugin) {
        plugin.getServer().getScheduler().runTaskTimer(plugin, new Task(), 0, 100);
    }

    @Override
    public void onRegisterCommands(BattlePlugin plugin, PaperCommandManager commandManager) {
        commandManager.registerCommand(new ExtendedCommand());
        commandManager.registerCommand(new RadioCommand());
    }

    @Override
    public void onDisable(BattlePlugin plugin) {

    }

    @NotNull
    public ArenaConfigManagerX getArenaConfigManagerX() {
        return arenaConfigManagerX;
    }

    @NotNull
    public ItemConfigManagerX getItemConfigManagerX() {
        return itemConfigManagerX;
    }

    @NotNull
    public RadioConfigManagerX getRadioConfigManagerX() {
        return radioConfigManagerX;
    }

    @NotNull
    public WorldConfigManagerX getWorldConfigManagerX() {
        return worldConfigManagerX;
    }
}
