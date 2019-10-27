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

package dev.anhcraft.battle.system.listeners;

import dev.anhcraft.battle.BattleComponent;
import dev.anhcraft.battle.BattlePlugin;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.*;

public class MessageListener extends BattleComponent implements Listener {
    public MessageListener(BattlePlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent ev) {
        if(!ev.getTag().equals(BattlePlugin.BATTLE_CHANNEL)) return;
        ByteArrayInputStream stream = new ByteArrayInputStream(ev.getData());
        DataInputStream in = new DataInputStream(stream);
        try {
            byte c = in.readByte();
            switch (c){
                case 1: {
                    String player = in.readUTF();
                    String arena = in.readUTF();
                    String server = in.readUTF();
                    ProxiedPlayer pp = plugin.getProxy().getPlayer(player);
                    String oldSv = pp.getServer().getInfo().getName();
                    pp.connect(plugin.getProxy().getServerInfo(server), (result, error) -> {
                        if(result){
                            plugin.tempJoinCache.putIfAbsent(pp, new String[]{arena, oldSv});
                        }
                    }, ServerConnectEvent.Reason.PLUGIN);
                    break;
                }
                case 0: {
                    String arena = in.readUTF();
                    String phase = in.readUTF();
                    int players = in.readInt();
                    long time = in.readLong();
                    ByteArrayOutputStream s = new ByteArrayOutputStream();
                    DataOutputStream out = new DataOutputStream(s);
                    out.writeByte(0);
                    out.writeUTF(arena);
                    out.writeUTF(phase);
                    out.writeInt(players);
                    out.writeLong(time);
                    byte[] bytes = s.toByteArray();
                    if(ev.getSender() instanceof Server && plugin.config.getBoolean("game_update.send_all")){
                        ServerInfo sender = ((Server) ev.getSender()).getInfo();
                        plugin.getProxy().getServers().forEach((s1, serverInfo) -> {
                            if(!serverInfo.equals(sender)){
                                serverInfo.sendData(BattlePlugin.BATTLE_CHANNEL, bytes, false);
                            }
                        });
                    } else {
                        int svs = in.readInt();
                        for(int i = 0; i < svs; i++){
                            String sv = in.readUTF();
                            plugin.getProxy().getServerInfo(sv).sendData(BattlePlugin.BATTLE_CHANNEL, bytes, false);
                        }
                    }
                }
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
