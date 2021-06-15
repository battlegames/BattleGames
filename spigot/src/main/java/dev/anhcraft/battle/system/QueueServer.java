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

package dev.anhcraft.battle.system;

import dev.anhcraft.battle.ApiProvider;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.List;

public class QueueServer {
    private final WeakReference<Player> player;
    private final List<String> serverList;
    private final int maxConnect;
    private final long connectInterval;
    private final String arena;
    private int connectCount;
    private long nextConnectTime;
    private int serverIndex;

    public QueueServer(Player player, List<String> serverList, String arena) {
        this(player, serverList, ApiProvider.consume().getGeneralConfig().getBungeeReconnectTries(), ApiProvider.consume().getGeneralConfig().getBungeeConnectDelay(), arena);
    }

    public QueueServer(Player player, List<String> serverList, int maxReconnect, long connectInterval, String arena) {
        this.player = new WeakReference<>(player);
        this.serverList = serverList;
        this.maxConnect = Math.max(0, maxReconnect) + 1; // max connect = max [re]connect + 1
        this.connectInterval = Math.max(1000, connectInterval * 50);
        this.arena = arena;
    }

    public boolean canConnect() {
        return System.currentTimeMillis() >= nextConnectTime;
    }

    @Nullable
    public Player getPlayer() {
        return player.get();
    }

    @Nullable
    public String nextServer() {
        nextConnectTime = System.currentTimeMillis() + connectInterval;
        if (maxConnect == connectCount) {
            serverIndex++;
            connectCount = 0;
        }
        if (serverIndex >= serverList.size()) return null;
        connectCount++;
        return serverList.get(serverIndex);
    }

    public int getMaxConnect() {
        return maxConnect;
    }

    public int getConnectCount() {
        return connectCount;
    }

    public int getServerIndex() {
        return serverIndex;
    }

    public int getServerCount() {
        return serverList.size();
    }

    @Nullable
    public String getArena() {
        return arena;
    }
}
