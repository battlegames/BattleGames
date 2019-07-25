package dev.anhcraft.abm.api.impl;

import dev.anhcraft.abm.api.objects.*;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.Optional;

public interface BattleAPI {
    long calculateExp(int level);
    int calculateLevel(long exp);
    Optional<PlayerData> getPlayerData(OfflinePlayer player);
    ServerData getServerData();
    Optional<Arena> getArena(String id);
    Optional<AmmoModel> getAmmoModel(String id);
    Optional<GunModel> getGunModel(String id);
    Optional<MagazineModel> getMagazineModel(String id);
    Optional<Kit> getKit(String id);
    List<Arena> listArenas();
    List<AmmoModel> listAmmoModels();
    List<GunModel> listGunModels();
    List<MagazineModel> listMagazineModels();
    List<Kit> listKits();
    BattleGameManager getGameManager();
}
