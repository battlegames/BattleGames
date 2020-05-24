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
import dev.anhcraft.battle.api.arena.Arena;
import dev.anhcraft.battle.api.arena.mode.Mode;
import dev.anhcraft.battle.premium.cmd.ExtendedCommand;
import dev.anhcraft.battle.premium.cmd.RadioCommand;
import dev.anhcraft.battle.premium.system.ArenaSettings;
import dev.anhcraft.battle.premium.system.ExConfigManager;
import dev.anhcraft.battle.premium.system.WorldSettings;
import dev.anhcraft.battle.premium.system.controllers.CTFController;
import dev.anhcraft.battle.premium.system.controllers.TeamDeathmatchController;
import dev.anhcraft.battle.premium.system.listeners.GameListener;
import dev.anhcraft.battle.premium.system.listeners.PlayerListener;
import dev.anhcraft.battle.premium.system.listeners.WorldListener;
import dev.anhcraft.battle.premium.tasks.Task;
import dev.anhcraft.battle.system.IPremiumModule;
import dev.anhcraft.confighelper.ConfigHelper;
import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.exception.InvalidValueException;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PremiumModule implements IPremiumModule {
    private static PremiumModule instance;
    public YamlConfiguration conf;
    private WorldSettings globalWorldSettings;
    private final Map<String, WorldSettings> worldSettingsMap = new HashMap<>();
    private final Map<String, ArenaSettings> arenaSettingsMap = new HashMap<>();
    private ExConfigManager exConfigManager;

    private static void fillOptions(ConfigurationSection model, ConfigurationSection target){
        for(String s : model.getKeys(false)){
            if(!target.isSet(s)){
                target.set(s, model.get(s));
            }
        }
    }

    @NotNull
    public static PremiumModule getInstance() {
        return instance;
    }

    public PremiumModule(){
        instance = this;
    }

    @Nullable
    public WorldSettings getWorldSettings(String world){
        WorldSettings ws = worldSettingsMap.get(world);
        if(ws != null) return ws;
        return globalWorldSettings.getBlacklistWorlds().contains(world) ? null : globalWorldSettings;
    }

    @Nullable
    public ArenaSettings getArenaSettings(String arena){
        return arenaSettingsMap.get(arena);
    }

    @Override
    public void onIntegrate(BattlePlugin plugin) {
    }

    @Override
    public void onInitSystem(BattlePlugin plugin) {
        exConfigManager = new ExConfigManager();
        plugin.arenaManager.initController(Mode.TEAM_DEATHMATCH, new TeamDeathmatchController(plugin));
        plugin.arenaManager.initController(Mode.CTF, new CTFController(plugin));
    }

    @Override
    public void onReloadConfig(BattlePlugin plugin) {
        exConfigManager.reloadConfig();
        conf = exConfigManager.getSettings();
        try {
            worldSettingsMap.clear();
            arenaSettingsMap.clear();
            ConfigurationSection gen = conf.getConfigurationSection("world_settings.general");
            globalWorldSettings = ConfigHelper.readConfig(Objects.requireNonNull(gen), ConfigSchema.of(WorldSettings.class));
            for(String k : conf.getConfigurationSection("world_settings.specific").getKeys(false)){
                ConfigurationSection s = conf.getConfigurationSection("world_settings.specific."+k);
                fillOptions(gen, s);
                worldSettingsMap.put(k, ConfigHelper.readConfig(s, ConfigSchema.of(WorldSettings.class)));
            }
            ConfigurationSection ascs = conf.getConfigurationSection("arena_settings");
            if(ascs != null) {
                for(String k : ascs.getKeys(false)) {
                    ConfigurationSection s = conf.getConfigurationSection("arena_settings." + k);
                    ArenaSettings as = ConfigHelper.readConfig(s, ConfigSchema.of(ArenaSettings.class));
                    arenaSettingsMap.put(k, as);
                    Arena a = plugin.getArena(k);
                    if (a != null && a.getRollback() == null) {
                        as.getEmptyRegions().clear();
                        plugin.getLogger().warning("You've not configured rollback system for arena #" + k + ". Some extra settings will be disabled for safe reasons.");
                    }
                }
            }
        } catch (InvalidValueException e) {
            e.printStackTrace();
        }
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
}
