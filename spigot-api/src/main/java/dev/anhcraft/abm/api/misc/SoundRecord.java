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
package dev.anhcraft.abm.api.misc;

import dev.anhcraft.abm.utils.EnumUtil;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class SoundRecord {
    private String soundName;
    private Sound bukkitSound;
    private float volume = 2;
    private float pitch = 1;

    public SoundRecord(String s) {
        if(s.length() == 0) return;
        String[] x = s.split(":");
        if(x.length >= 1){
            String sound = x[0];
            if(sound.charAt(0) == '$')
                bukkitSound = EnumUtil.getEnum(Sound.values(), sound.substring(1));
            else
                soundName = sound;

            if(x.length >= 2) {
                volume = Float.parseFloat(x[1]);

                if(x.length >= 3) {
                    pitch = Float.parseFloat(x[2]);
                }
            }
        }
    }

    public void play(Player player){
        if(soundName != null)
            player.playSound(player.getLocation(), soundName, volume, pitch);
        else if(bukkitSound != null)
            player.playSound(player.getLocation(), bukkitSound, volume, pitch);
    }

    public void play(World world, Location location){
        if(soundName != null)
            world.playSound(location, soundName, volume, pitch);
        else if(bukkitSound != null)
            world.playSound(location, bukkitSound, volume, pitch);
    }
}
