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

package dev.anhcraft.abm.system.messengers;

import com.google.common.collect.Multiset;
import dev.anhcraft.abm.BattleComponent;
import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.game.Arena;
import dev.anhcraft.abm.api.game.GamePhase;
import dev.anhcraft.abm.api.game.LocalGame;
import dev.anhcraft.abm.api.game.RemoteGame;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.Nullable;

import java.io.*;

public class BungeeMessenger extends BattleComponent implements PluginMessageListener {
    public static final String BATTLE_CHANNEL = "battle:plugin";

    public BungeeMessenger(BattlePlugin plugin) {
        super(plugin);
    }

    public void requestGameJoin(Player player, @Nullable String arena, String server){
        if(arena == null) return;
        try {
            ByteArrayOutputStream s = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(s);
            out.writeByte(1);
            out.writeUTF(player.getName());
            out.writeUTF(arena);
            out.writeUTF(server);
            out.close();
            player.sendPluginMessage(plugin, BATTLE_CHANNEL, s.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendGameUpdate(LocalGame game){
        try {
            ByteArrayOutputStream s = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(s);
            out.writeByte(0);
            out.writeUTF(game.getArena().getId());
            out.writeUTF(game.getPhase().toString());
            out.writeInt(game.getPlayerCount());
            out.writeLong(game.getCurrentTime().get());
            Multiset<String> servers = game.getDownstreamServers().keys();
            out.writeInt(servers.size());
            for(String sv : servers) out.writeUTF(sv);
            out.close();
            byte[] array = s.toByteArray();
            game.getPlayers().keySet().iterator().next().sendPluginMessage(plugin, BATTLE_CHANNEL, array);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPluginMessageReceived(String channel, Player mid, byte[] message) {
        if(!channel.equals(BATTLE_CHANNEL)) return;
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
        try {
            byte c = in.readByte();
            switch (c){
                case 0: {
                    String arena = in.readUTF();
                    String phase = in.readUTF();
                    int players = in.readInt();
                    long time = in.readLong();
                    plugin.getArena(arena).filter(Arena::hasBungeecordSupport)
                            .flatMap(a -> plugin.gameManager.getGame(a))
                            .ifPresent(game -> {
                        RemoteGame rg = (RemoteGame) game;
                        rg.setPhase(GamePhase.valueOf(phase));
                        rg.setPlayerCount(players);
                        rg.getCurrentTime().set(time);
                    });
                    break;
                }
                case 2: {
                    Player player = Bukkit.getPlayer(in.readUTF());
                    String arena = in.readUTF();
                    String server = in.readUTF();
                    plugin.getArena(arena).ifPresent(arena1 -> {
                        if(plugin.gameManager.join(player, arena1, true)) {
                            plugin.gameManager.getGame(arena1).ifPresent(game -> ((LocalGame) game).getDownstreamServers().put(server, player));
                        }
                    });
                }
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
