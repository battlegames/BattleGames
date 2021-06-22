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
package dev.anhcraft.battle.tasks;

import dev.anhcraft.battle.BattleComponent;
import dev.anhcraft.battle.BattlePlugin;
import dev.anhcraft.battle.system.QueueServer;
import dev.anhcraft.battle.utils.info.InfoHolder;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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
            if (qs.canConnect()) {
                Player p = qs.getPlayer();
                String ns = qs.nextServer();
                if (p == null) it.remove();
                else if (ns == null) {
                    plugin.chatManager.sendPlayer(p, "server.connect_failed");
                    it.remove();
                } else {
                    if (qs.getConnectCount() == 1) {
                        if (qs.getServerIndex() > 0) {
                            plugin.chatManager.sendPlayer(p, "server.switched_server", new InfoHolder("").inform("index", qs.getServerIndex() + 1).inform("size", qs.getConnectCount()).compile());
                        } else {
                            plugin.chatManager.sendPlayer(p, "server.server_connecting");
                        }
                    } else {
                        plugin.chatManager.sendPlayer(p, "server.try_reconnect", new InfoHolder("").inform("current", qs.getConnectCount() - 1).inform("max", qs.getMaxConnect() - 1).compile());
                    }
                    if (qs.getArena() != null) {
                        plugin.bungeeMessenger.requestGameJoin(p, qs.getArena(), ns);
                    } else {
                        try {
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            DataOutputStream out = new DataOutputStream(stream);
                            out.writeUTF("Connect");
                            out.writeUTF(ns);
                            p.sendPluginMessage(plugin, "BungeeCord", stream.toByteArray());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
