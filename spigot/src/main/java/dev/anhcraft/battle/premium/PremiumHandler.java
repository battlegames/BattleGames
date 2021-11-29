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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class PremiumHandler {

    @Contract(pure = true)
    @NotNull
    public static String getUserID() {
        return "%%__USER__%%";
    }

    @Contract(pure = true)
    @NotNull
    public static String getResourceID() {
        return "%%__RESOURCE__%%";
    }

    @Contract(pure = true)
    @NotNull
    public static String getDownloadID() {
        return "%%__NONCE__%%";
    }

    @Contract(pure = true)
    @NotNull
    public static String isSongoda() {
        return "%%__SONGODA__%%";
    }

    @NotNull
    public static String formatUserURL() {
        return "https://songoda.com/profile/%%__USERNAME__%%";
    }

    public static boolean isPremium() {
        return Boolean.parseBoolean("%%__SONGODA__%%");
    }
}