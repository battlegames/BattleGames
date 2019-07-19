package dev.anhcraft.abm.system.managers;

import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.enums.StorageType;
import dev.anhcraft.abm.api.ext.BattleComponent;
import dev.anhcraft.abm.api.objects.PlayerData;
import dev.anhcraft.abm.storage.handlers.FileStorage;
import dev.anhcraft.abm.system.providers.StorageProvider;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DataManager extends BattleComponent {
    private final Map<OfflinePlayer, StorageProvider> PLAYER_STORAGE = new HashMap<>();
    private StorageProvider serverStorage;
    private StorageType storageType;
    private File dataDir;

    public DataManager(BattlePlugin plugin, StorageType storageType) {
        super(plugin);
        this.storageType = storageType;
    }

    public void initFileStorage(File dataDir) {
        this.dataDir = dataDir;
        serverStorage = new StorageProvider(new FileStorage(new File(dataDir, "server.abm")));
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
        StorageProvider provider = PLAYER_STORAGE.get(player);
        if(provider == null) {
            switch (storageType) {
                case FILE: {
                    File f = new File(dataDir, "player." + player.getUniqueId().toString() + ".abm");
                    PLAYER_STORAGE.put(player, provider = new StorageProvider(new FileStorage(f)));
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
            StorageProvider provider = PLAYER_STORAGE.get(player);
            provider.getData().clear();
            playerData.write(provider.getData());
            provider.save();
        });
    }
}
