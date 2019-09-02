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
package dev.anhcraft.abm.api;

import dev.anhcraft.abm.api.game.Arena;
import dev.anhcraft.abm.api.inventory.items.*;
import dev.anhcraft.abm.api.misc.Kit;
import dev.anhcraft.abm.api.misc.info.InfoHolder;
import dev.anhcraft.abm.api.storage.data.PlayerData;
import dev.anhcraft.abm.api.storage.data.ServerData;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BattleAPI {
    @NotNull Map<String, String> mapInfo(InfoHolder holder);
    @NotNull String formatLongFormDate(Date date);
    @NotNull String formatShortForm(long time);
    @NotNull String formatShortFormDateHours(Date date);
    @NotNull String formatShortFormDateMinutes(Date date);
    @NotNull String formatShortFormDateSeconds(Date date);
    float getDefaultWalkingSpeed();
    float getDefaultFlyingSpeed();
    long calculateExp(int level);
    int calculateLevel(long exp);
    Optional<PlayerData> getPlayerData(@Nullable OfflinePlayer player);
    @NotNull ServerData getServerData();
    Optional<Arena> getArena(@Nullable String id);
    Optional<AmmoModel> getAmmoModel(@Nullable String id);
    Optional<GunModel> getGunModel(@Nullable String id);
    Optional<MagazineModel> getMagazineModel(@Nullable String id);
    Optional<ScopeModel> getScopeModel(@Nullable String id);
    Optional<Kit> getKit(@Nullable String id);
    @NotNull List<Arena> listArenas();
    @NotNull List<AmmoModel> listAmmoModels();
    @NotNull List<GunModel> listGunModels();
    @NotNull List<MagazineModel> listMagazineModels();
    @NotNull List<ScopeModel> listScopes();
    @NotNull List<Kit> listKits();
    @NotNull BattleGameManager getGameManager();
    @NotNull BattleItemManager getItemManager();
    @NotNull BattleGuiManager getGuiManager();
    @NotNull BattleChatManager getChatManager();
}
