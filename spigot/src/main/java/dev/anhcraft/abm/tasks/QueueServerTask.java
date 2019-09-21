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
package dev.anhcraft.abm.tasks;

import dev.anhcraft.abm.BattleComponent;
import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.system.QueueServer;
import dev.anhcraft.craftkit.utils.BungeeUtil;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class QueueServerTask extends BattleComponent implements Runnable {
    public final List<QueueServer> QUEUE = Collections.synchronizedList(new ArrayList<>());

    public QueueServerTask(BattlePlugin plugin) {
        super(plugin);
    }

    @Override
    public void run() {
        for (Iterator<QueueServer> it = QUEUE.iterator(); it.hasNext(); ) {
            QueueServer qs = it.next();
            if(qs.canConnect()){
                Player p = qs.getPlayer();
                String ns = qs.nextServer();
                if(p == null) it.remove();
                else if(ns == null) {
                    plugin.chatManager.sendPlayer(p, "server.connect_failed");
                    it.remove();
                }
                else {
                    if(qs.getConnectCount() == 1){
                        if(qs.getServerIndex() > 0) plugin.chatManager.sendPlayer(p, "server.switched_server", s -> String.format(s, qs.getServerIndex(), qs.getServerCount()));
                        else plugin.chatManager.sendPlayer(p, "server.server_connecting");
                    } else plugin.chatManager.sendPlayer(p, "server.try_reconnect", s -> String.format(s, qs.getConnectCount() - 1, qs.getMaxConnect() - 1));
                    if(qs.getArena() != null)
                        plugin.bungeeMessenger.requestGameJoin(p, qs.getArena(), ns);
                    else
                        BungeeUtil.connect(p, ns);
                }
            }
        }
    }
}
