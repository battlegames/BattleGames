package dev.anhcraft.abm.api;

import dev.anhcraft.abm.api.game.Arena;
import dev.anhcraft.abm.api.inventory.items.AmmoModel;
import dev.anhcraft.abm.api.inventory.items.GunModel;
import dev.anhcraft.abm.api.inventory.items.MagazineModel;
import dev.anhcraft.abm.api.misc.Kit;
import dev.anhcraft.abm.api.storage.data.PlayerData;
import dev.anhcraft.abm.api.storage.data.ServerData;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface BattleAPI {
    String formatLongFormDate(Date date);
    String formatShortForm(long time);
    String formatShortFormDateHours(Date date);
    String formatShortFormDateMinutes(Date date);
    String formatShortFormDateSeconds(Date date);
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
    @NotNull BattleChatManager getChatManager();
}
