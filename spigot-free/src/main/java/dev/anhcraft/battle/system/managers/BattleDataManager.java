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
package dev.anhcraft.battle.system.managers;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.anhcraft.battle.BattleComponent;
import dev.anhcraft.battle.BattlePlugin;
import dev.anhcraft.battle.api.stats.NativeStats;
import dev.anhcraft.battle.api.storage.StorageType;
import dev.anhcraft.battle.api.storage.data.DataMap;
import dev.anhcraft.battle.api.storage.data.PlayerData;
import dev.anhcraft.battle.storage.Storage;
import dev.anhcraft.battle.storage.handlers.FileStorage;
import dev.anhcraft.battle.storage.handlers.MySQLStorage;
import dev.anhcraft.battle.system.debugger.BattleDebugger;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BattleDataManager extends BattleComponent {
    private final Map<OfflinePlayer, Storage> PLAYER_STORAGE = new HashMap<>();
    private Storage serverStorage;
    private StorageType storageType;
    private File dataDir;
    private HikariDataSource dataSource;

    public BattleDataManager(BattlePlugin plugin, StorageType storageType) {
        super(plugin);
        this.storageType = storageType;
    }

    public void initFileStorage(File dataDir) {
        this.dataDir = dataDir;
        serverStorage = new Storage(new FileStorage(new File(dataDir, "server.abm")));
    }

    public void initMySQLStorage(String url, String user, String pass, ConfigurationSection dsc) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(pass);
        if(dsc != null){
            Set<String> keys = dsc.getKeys(true);
            for(String k : keys) config.addDataSourceProperty(k, dsc.get(k));
        }
        dataSource = new HikariDataSource(config);
        serverStorage = new Storage(new MySQLStorage(dataSource, "abm_server_"));
    }

    public synchronized void loadServerData(){
        if(serverStorage != null) {
            BattleDebugger.startTiming("server-data-load");
            plugin.getServerData().reset();
            if(serverStorage.load()) plugin.getServerData().read(serverStorage.getData());
            BattleDebugger.endTiming("server-data-load");
        }
    }

    public synchronized void saveServerData(){
        if(serverStorage != null) {
            BattleDebugger.startTiming("server-data-save");
            plugin.getServerData().write(serverStorage.getData());
            if(serverStorage.getData().getModifyTracker().get() && serverStorage.save())
                serverStorage.getData().getModifyTracker().set(false);
            BattleDebugger.endTiming("server-data-save");
        }
    }

    @NotNull
    public synchronized PlayerData loadPlayerData(OfflinePlayer player){
        Storage provider = PLAYER_STORAGE.get(player);
        if(provider == null) {
            switch (storageType) {
                case FILE: {
                    File f = new File(dataDir, "player." + player.getUniqueId().toString() + ".abm");
                    PLAYER_STORAGE.put(player, provider = new Storage(new FileStorage(f)));
                    break;
                }
                case MYSQL: {
                    PLAYER_STORAGE.put(player, provider = new Storage(new MySQLStorage(dataSource, "abm_player_"+player.getUniqueId().toString().replace("-", "")+"_")));
                    break;
                }
                default:
                    throw new UnsupportedOperationException();
            }
        }
        BattleDebugger.startTiming("player-data-load");
        PlayerData pd = new PlayerData();
        if(provider.load()) {
            DataMap<String> data = provider.getData();
            Integer ver = data.readTag("version", Integer.class);
            if(ver == null || ver < 2){
                plugin.getLogger().info("Upgrading player data: " + player.getUniqueId().toString()+" v1 -> v2");
                data.cutTag("exp", "stats." + NativeStats.EXP);
                data.cutTag("kill", "stats." + NativeStats.KILL);
                data.cutTag("death", "stats." + NativeStats.DEATH);
                data.cutTag("ast", "stats." + NativeStats.ASSIST);
                data.cutTag("fsk", "stats." + NativeStats.FIRST_KILL);
                data.cutTag("hs", "stats." + NativeStats.HEADSHOT);
                data.cutTag("win", "stats." + NativeStats.WIN);
                data.cutTag("lose", "stats." + NativeStats.LOSE);
                data.writeTag("version", 2);
            }
            if(ver == null || ver < 3){
                plugin.getLogger().info("Upgrading player data: " + player.getUniqueId().toString()+" v2 -> v3");
                data.cutTag("kl", "stats." + NativeStats.KILL);
                data.cutTag("dt", "stats." + NativeStats.DEATH);
                data.cutTag("wn", "stats." + NativeStats.WIN);
                data.cutTag("ls", "stats." + NativeStats.LOSE);
                data.writeTag("version", 3);
            }
            pd.read(data);
        }
        plugin.PLAYER_MAP.put(player, pd);
        BattleDebugger.endTiming("player-data-load");
        return pd;
    }

    public synchronized void unloadPlayerData(OfflinePlayer player){
        plugin.PLAYER_MAP.remove(player);
        PLAYER_STORAGE.remove(player);
    }

    public synchronized void savePlayerData(OfflinePlayer player){
        PlayerData playerData = plugin.getPlayerData(player);
        if(playerData != null) {
            BattleDebugger.startTiming("player-data-save");
            Storage provider = PLAYER_STORAGE.get(player);
            playerData.write(provider.getData());
            if(provider.getData().getModifyTracker().get() && provider.save())
                provider.getData().getModifyTracker().set(false);
            BattleDebugger.endTiming("player-data-save");
        }
    }

    public void destroy(){
        PLAYER_STORAGE.values().forEach(Storage::destroy);
        serverStorage.destroy();
        PLAYER_STORAGE.clear();
        if(dataSource != null) dataSource.close();
    }
}
