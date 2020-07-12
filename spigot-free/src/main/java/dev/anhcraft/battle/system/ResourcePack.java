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
import dev.anhcraft.jvmkit.trackers.BufferedStreamReadTracker;
import dev.anhcraft.jvmkit.trackers.reports.FixedStreamTransferReport;
import dev.anhcraft.jvmkit.utils.MathUtil;
import dev.anhcraft.jvmkit.utils.RandomUtil;
import dev.anhcraft.jvmkit.utils.UserAgent;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.Consumer;

@SuppressWarnings("UnstableApiUsage")
public class ResourcePack {
    private static byte[] HASH;
    private static String FILE;

    private static String getUrl(){
        return "https://tichcucquaytayvanmayseden.000webhostapp.com/"+FILE;
    }

    public static void init(Consumer<String> stringConsumer) {
        switch (NMSVersion.current()){
            case v1_12_R1: FILE = "abm-1.12.zip";
            case v1_13_R1:
            case v1_13_R2:
            case v1_14_R1: FILE = "abm-1.13-1.14.zip";
            default: FILE = "abm-1.15.zip";
        }
        stringConsumer.accept("Downloading resource pack....");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        HTTPConnectionHelper conn = new HTTPConnectionHelper(getUrl())
                .setProperty("User-Agent", UserAgent.CHROME_WINDOWS)
                .connect();
        BufferedStreamReadTracker tracker = new BufferedStreamReadTracker(4096, conn.getInput());
        FixedStreamTransferReport report = new FixedStreamTransferReport(conn.getContentLength());
        tracker.setBufferCallback(bytes -> {
            if(RandomUtil.randomInt(0, 10) == 0) {
                stringConsumer.accept(report.getTransferredBytes() + " bytes ... " + MathUtil.round(report.getProgress(), 3) + "%");
            }
            try {
                out.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        tracker.start(report, () -> {
            conn.disconnect();
            HashCode hashCode = Hashing.sha1().hashBytes(out.toByteArray());
            HASH = hashCode.asBytes();
            stringConsumer.accept("Finished! Hash: "+hashCode.toString());
        });
    }

    public static void send(Player player){
        if(HASH == null) return;
        String url = getUrl();
        String s = BattleApi.getInstance().getGeneralConfig().getResourcePackCustomUrl();
        if(s != null && !(s = s.trim()).isEmpty()) url = s;
        player.setResourcePack(url, HASH);
        BattleApi.getInstance().getChatManager().sendPlayer(player, "resource_pack.notice");
    }
}
