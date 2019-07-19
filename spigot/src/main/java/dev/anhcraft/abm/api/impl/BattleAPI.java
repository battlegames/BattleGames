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
    Optional<Ammo> getAmmo(String id);
    Optional<Gun> getGun(String id);
    Optional<Magazine> getMagazine(String id);
    Optional<Kit> getKit(String id);
    List<Arena> listArenas();
    List<Ammo> listAmmo();
    List<Gun> listGuns();
    List<Magazine> listMagazines();
    List<Kit> listKits();
}
