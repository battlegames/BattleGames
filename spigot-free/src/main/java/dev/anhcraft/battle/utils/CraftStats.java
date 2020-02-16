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

package dev.anhcraft.battle.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.anhcraft.battle.BattlePlugin;
import dev.anhcraft.jvmkit.helpers.HTTPConnectionHelper;
import org.apache.commons.lang.SystemUtils;
import org.bukkit.Bukkit;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class CraftStats {
    public static void sendData(BattlePlugin plugin){
        try {
            JsonObject object = new JsonObject();
            object.addProperty("pn", plugin.getDescription().getName());
            object.addProperty("pv", plugin.getDescription().getVersion());
            object.addProperty("lcs", plugin.isPremium() ? "premium" : "free");
            object.addProperty("gv", Bukkit.getVersion());
            object.addProperty("onp", Bukkit.getOnlinePlayers().size());
            object.addProperty("onm", Boolean.toString(Bukkit.getOnlineMode()));
            object.addProperty("mrm", Runtime.getRuntime().maxMemory() / 1000000L);
            object.addProperty("frm", Runtime.getRuntime().freeMemory() / 1000000L);
            object.addProperty("trm", Runtime.getRuntime().totalMemory() / 1000000L);
            object.addProperty("prs", Runtime.getRuntime().availableProcessors());
            object.addProperty("os", SystemUtils.OS_NAME);
            object.addProperty("arc", SystemUtils.OS_ARCH);
            HttpURLConnection conn = (HttpURLConnection) new URL("https://anhcraft.dev/stats/").openConnection();
            conn.setRequestMethod("POST");
            conn.addRequestProperty("User-Agent", HTTPConnectionHelper.USER_AGENT_CHROME);
            conn.addRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            BufferedOutputStream output = new BufferedOutputStream(conn.getOutputStream());
            output.write(new Gson().toJson(object).getBytes(StandardCharsets.UTF_8));
            output.flush();
            BufferedInputStream input = new BufferedInputStream(conn.getInputStream());
            input.close();
            output.close();
            conn.disconnect();
        } catch (Exception ignored){}
    }
}
