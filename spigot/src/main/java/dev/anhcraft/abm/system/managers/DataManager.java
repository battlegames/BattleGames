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
package dev.anhcraft.abm.system.managers;

import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.storage.StorageType;
import dev.anhcraft.abm.BattleComponent;
import dev.anhcraft.abm.api.storage.data.PlayerData;
import dev.anhcraft.abm.storage.handlers.FileStorage;
import dev.anhcraft.abm.storage.Storage;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DataManager extends BattleComponent {
    private final Map<OfflinePlayer, Storage> PLAYER_STORAGE = new HashMap<>();
    private Storage serverStorage;
    private StorageType storageType;
    private File dataDir;

    public DataManager(BattlePlugin plugin, StorageType storageType) {
        super(plugin);
        this.storageType = storageType;
    }

    public void initFileStorage(File dataDir) {
        this.dataDir = dataDir;
        serverStorage = new Storage(new FileStorage(new File(dataDir, "server.abm")));
    }

    public void loadServerData(){
        if(serverStorage != null) {
            plugin.getServerData().reset();
            if(serverStorage.load()) plugin.getServerData().read(serverStorage.getData());
        }
    }

    public void saveServerData(){
        if(serverStorage != null) {
            serverStorage.getData().clear();
            plugin.getServerData().write(serverStorage.getData());
            serverStorage.save();
        }
    }

    public void loadPlayerData(OfflinePlayer player){
        Storage provider = PLAYER_STORAGE.get(player);
        if(provider == null) {
            switch (storageType) {
                case FILE: {
                    File f = new File(dataDir, "player." + player.getUniqueId().toString() + ".abm");
                    PLAYER_STORAGE.put(player, provider = new Storage(new FileStorage(f)));
                    break;
                }
                default:
                    throw new UnsupportedOperationException();
            }
        }
        PlayerData pd = new PlayerData();
        if(provider.load()) pd.read(provider.getData());
        plugin.PLAYER_MAP.put(player, pd);
    }

    public void unloadPlayerData(OfflinePlayer player){
        plugin.PLAYER_MAP.remove(player);
        PLAYER_STORAGE.remove(player);
    }

    public void savePlayerData(OfflinePlayer player){
        plugin.getPlayerData(player).ifPresent(playerData -> {
            Storage provider = PLAYER_STORAGE.get(player);
            provider.getData().clear();
            playerData.write(provider.getData());
            provider.save();
        });
    }
}
