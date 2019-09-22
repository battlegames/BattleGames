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

import java.util.*;
import java.util.function.Consumer;

public interface BattleAPI {
    /**
     * Creates a map represents the view of {@link InfoHolder}.
     * @param holder the information holder
     * @return info map
     */
    @NotNull
    Map<String, String> mapInfo(@NotNull InfoHolder holder);

    /**
     * Formats the given date in long-form.
     * @param date the date
     * @return formatted date
     */
    @NotNull
    String formatLongFormDate(@NotNull Date date);

    /**
     * Formats the given time in short-form.
     * <br>
     * This method will calculate to make sure that the formatted time is shortest.
     * For instance, only seconds is displayed if the given time is below 60 seconds.
     * Or if the given time is below 60 minutes, the hour will be hidden
     * @param time the time in milliseconds
     * @return formatted date
     */
    @NotNull
    String formatShortFormTime(long time);

    /**
     * Formats the given date in short-form that stops at hours.
     * @param date the date
     * @return formatted date
     */
    @NotNull
    String formatShortFormDateHours(@NotNull Date date);

    /**
     * Formats the given date in short-form that stops at minutes.
     * @param date the date
     * @return formatted date
     */
    @NotNull
    String formatShortFormDateMinutes(@NotNull Date date);

    /**
     * Formats the given date in short-form that stops at seconds.
     * @param date the date
     * @return formatted date
     */
    @NotNull
    String formatShortFormDateSeconds(@NotNull Date date);

    /**
     * Gets the default walking speed.
     * @return walking speed
     */
    float getDefaultWalkingSpeed();

    /**
     * Gets the default flying speed.
     * @return flying speed
     */
    float getDefaultFlyingSpeed();

    /**
     * Calculates the exp amount from the given level
     * @param level the level
     * @return the exp that equals to the level
     */
    long calculateExp(int level);

    /**
     * Calculates the level from the given exp
     * @param exp the exp
     * @return the level that equals to the exp
     */
    int calculateLevel(long exp);

    /**
     * Gets the {@link PlayerData} of the given player
     * @param player player
     * @return {@link PlayerData} or null if not found
     */
    @Nullable
    PlayerData getPlayerData(@Nullable OfflinePlayer player);

    /**
     * Gets the {@link ServerData}
     * @return server data
     */
    @NotNull
    ServerData getServerData();

    /**
     * Gets the arena that matches the given id.
     * @param id the id of the arena
     * @return {@link Arena} if found or null if not
     */
    @Nullable
    Arena getArena(@Nullable String id);

    /**
     * Gets the ammo that matches the given id.
     * @param id the id of the ammo
     * @return {@link AmmoModel} if found or null if not
     */
    AmmoModel getAmmoModel(@Nullable String id);

    /**
     * Gets the gun that matches the given id.
     * @param id the id of the gun
     * @return {@link GunModel} if found or null if not
     */
    GunModel getGunModel(@Nullable String id);

    /**
     * Gets the magazine that matches the given id.
     * @param id the id of the magazine
     * @return {@link MagazineModel} if found or null if not
     */
    MagazineModel getMagazineModel(@Nullable String id);

    /**
     * Gets the scope that matches the given id.
     * @param id the id of the scope
     * @return {@link ScopeModel} if found or null if not
     */
    ScopeModel getScopeModel(@Nullable String id);

    /**
     * Gets the kit that matches the given id.
     * @param id the id of the kit
     * @return {@link Kit} if found or null if not
     */
    Kit getKit(@Nullable String id);

    /**
     * Lists all registered arenas.
     * @return an immutable list of arenas
     */
    @NotNull
    List<Arena> listArenas();

    /**
     * Lists all arenas and gets them.
     * @param consumer the consumer
     */
    void listArenas(@NotNull Consumer<Arena> consumer);

    /**
     * Lists all registered ammo.
     * @return an immutable list of ammo
     */
    @NotNull
    List<AmmoModel> listAmmoModels();

    /**
     * Lists all ammo and gets them.
     * @param consumer the consumer
     */
    void listAmmoModels(@NotNull Consumer<AmmoModel> consumer);

    /**
     * Lists all registered guns.
     * @return an immutable list of guns
     */
    @NotNull
    List<GunModel> listGunModels();

    /**
     * Lists all guns and gets them.
     * @param consumer the consumer
     */
    void listGunModels(@NotNull Consumer<GunModel> consumer);

    /**
     * Lists all registered magazines.
     * @return an immutable list of magazines
     */
    @NotNull
    List<MagazineModel> listMagazineModels();

    /**
     * Lists all magazines and gets them.
     * @param consumer the consumer
     */
    void listMagazineModels(@NotNull Consumer<MagazineModel> consumer);

    /**
     * Lists all registered scopes.
     * @return an immutable list of scopes
     */
    @NotNull
    List<ScopeModel> listScopes();

    /**
     * Lists all scopes and gets them.
     * @param consumer the consumer
     */
    void listScopes(@NotNull Consumer<ScopeModel> consumer);

    /**
     * Lists all registered kits.
     * @return an immutable list of kits
     */
    @NotNull
    List<Kit> listKits();

    /**
     * Lists all kits and gets them.
     * @param consumer the consumer
     */
    void listKits(@NotNull Consumer<Kit> consumer);

    /**
     * Gets the game manager.
     * @return {@link BattleGameManager}
     */
    @NotNull
    BattleGameManager getGameManager();

    /**
     * Gets the item manager.
     * @return {@link BattleItemManager}
     */
    @NotNull
    BattleItemManager getItemManager();

    /**
     * Gets the GUI manager.
     * @return {@link BattleGuiManager}
     */
    @NotNull
    BattleGuiManager getGuiManager();

    /**
     * Gets the chat manager.
     * @return {@link BattleChatManager}
     */
    @NotNull
    BattleChatManager getChatManager();

    /**
     * Checks if Bungeecord is supported.
     * @return {@link true} if it is or {@link false} otherwise
     */
    boolean hasBungeecordSupport();

    /**
     * Gets all lobby servers
     * @return an immutable list contains lobby servers.
     */
    @NotNull
    List<String> getLobbyServers();

    /**
     * Gets the maximum reconnection tries.
     * @return maximum reconnection tries
     */
    int getMaxReconnectionTries();

    /**
     * Gets the connection delay.
     * @return connection delay
     */
    long getConnectionDelay();
}
