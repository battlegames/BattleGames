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

package dev.anhcraft.abm;

import com.google.common.io.ByteStreams;
import dev.anhcraft.abm.system.listeners.MessageListener;
import dev.anhcraft.abm.system.listeners.PlayerListener;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("ALL")
public class BattlePlugin extends Plugin {
    public static final String BATTLE_CHANNEL = "battle:plugin";
    public final Map<ProxiedPlayer, String[]> tempJoinCache = new HashMap<>();
    public Configuration config;

    @Override
    public void onEnable() {
        File confFile = new File(getDataFolder(), "config.yml");
        try {
            if(!confFile.exists()) {
                getDataFolder().mkdir();
                confFile.createNewFile();

                FileOutputStream out = new FileOutputStream(confFile);
                InputStream in = getResourceAsStream("config.yml");
                ByteStreams.copy(in, out);
                in.close();
                out.close();
            }
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(confFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        getProxy().registerChannel(BATTLE_CHANNEL);

        getProxy().getPluginManager().registerListener(this, new MessageListener(this));
        getProxy().getPluginManager().registerListener(this, new PlayerListener(this));
    }

    @Override
    public void onDisable() {

    }
}
