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

package dev.anhcraft.battle.premium.tasks;

import dev.anhcraft.battle.premium.PremiumModule;
import dev.anhcraft.battle.premium.config.WorldSettings;
import org.bukkit.Bukkit;
import org.bukkit.WeatherType;
import org.bukkit.World;

public class Task implements Runnable {
    @Override
    public void run() {
        for (World world : Bukkit.getWorlds()) {
            WorldSettings ws = PremiumModule.getInstance().getWorldConfigManagerX().getWorldSettings(world.getName());
            if (ws != null) {
                if (ws.getAlwaysTime() != -1) world.setTime(ws.getAlwaysTime());
                if (ws.getAlwaysWeather() != null) world.setStorm(ws.getAlwaysWeather() == WeatherType.DOWNFALL);
            }
        }
    }
}
