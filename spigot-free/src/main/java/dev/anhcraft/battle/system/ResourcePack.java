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

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import dev.anhcraft.battle.api.BattleApi;
import dev.anhcraft.craftkit.cb_common.NMSVersion;
import dev.anhcraft.jvmkit.helpers.HTTPConnectionHelper;
import org.bukkit.entity.Player;

@SuppressWarnings("UnstableApiUsage")
public class ResourcePack {
    private static byte[] HASH;
    private static String FILE;

    private static String getUrl(){
        return "https://tichcucquaytayvanmayseden.000webhostapp.com/"+FILE;
    }

    public static void init() {
        switch (NMSVersion.current()){
            case v1_12_R1: FILE = "abm-1.12.zip";
            case v1_13_R1:
            case v1_13_R2:
            case v1_14_R1: FILE = "abm-1.13-1.14.zip";
            default: FILE = "abm-1.15.zip";
        }
        BattleApi.getInstance().getLogger().info("Downloading resource pack....");
        HTTPConnectionHelper conn = new HTTPConnectionHelper(getUrl())
                .setProperty("User-Agent", HTTPConnectionHelper.USER_AGENT_CHROME)
                .connect();
        HashCode hashCode = Hashing.sha1().hashBytes(conn.read());
        HASH = hashCode.asBytes();
        BattleApi.getInstance().getLogger().info("Finished! Hash: "+hashCode.toString());
        conn.disconnect();
    }

    public static void send(Player player){
        String url = getUrl();
        String s = BattleApi.getInstance().getGeneralConfig().getResourcePackCustomUrl();
        if(s != null && !(s = s.trim()).isEmpty()) url = s;
        player.setResourcePack(url, HASH);
        BattleApi.getInstance().getChatManager().sendPlayer(player, "resource_pack.notice");
    }
}
