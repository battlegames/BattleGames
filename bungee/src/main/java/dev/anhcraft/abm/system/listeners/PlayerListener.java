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

package dev.anhcraft.abm.system.listeners;

import dev.anhcraft.abm.BattleComponent;
import dev.anhcraft.abm.BattlePlugin;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class PlayerListener extends BattleComponent implements Listener {
    public PlayerListener(BattlePlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void connected(ServerConnectedEvent event){
        String[] tempArr = plugin.tempJoinCache.remove(event.getPlayer());
        if(tempArr != null){
            plugin.getProxy().getScheduler().schedule(plugin, () -> {
                try {
                    ByteArrayOutputStream s = new ByteArrayOutputStream();
                    DataOutputStream out = new DataOutputStream(s);
                    out.writeByte(2);
                    out.writeUTF(event.getPlayer().getName());
                    out.writeUTF(tempArr[0]);
                    out.writeUTF(tempArr[1]);
                    out.close();
                    event.getServer().getInfo().sendData(BattlePlugin.BATTLE_CHANNEL, s.toByteArray(), true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }, plugin.config.getLong("game_join.delay_time"), TimeUnit.SECONDS);
        }
    }

    @EventHandler
    public void disconnect(PlayerDisconnectEvent event){
        plugin.tempJoinCache.remove(event.getPlayer());
    }
}
