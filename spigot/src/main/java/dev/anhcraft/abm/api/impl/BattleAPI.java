package dev.anhcraft.abm.api.impl;

import dev.anhcraft.abm.api.objects.*;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public interface BattleAPI {
    long calculateExp(int level);
    int calculateLevel(long exp);
    Optional<PlayerData> getPlayerData(@Nullable OfflinePlayer player);
    ServerData getServerData();
    Optional<Arena> getArena(@Nullable String id);
    Optional<AmmoModel> getAmmoModel(@Nullable String id);
    Optional<GunModel> getGunModel(@Nullable String id);
    Optional<MagazineModel> getMagazineModel(@Nullable String id);
    Optional<Kit> getKit(@Nullable String id);
    List<Arena> listArenas();
    List<AmmoModel> listAmmoModels();
    List<GunModel> listGunModels();
    List<MagazineModel> listMagazineModels();
    List<Kit> listKits();
    @NotNull BattleGameManager getGameManager();
    @NotNull BattleItemManager getItemManager();
    @NotNull BattleGuiManager getGuiManager();
}
