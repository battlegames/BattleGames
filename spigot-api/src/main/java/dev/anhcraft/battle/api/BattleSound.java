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
package dev.anhcraft.battle.api;

import dev.anhcraft.battle.utils.XSound;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.function.Supplier;

public class BattleSound {
    private String soundName;
    private Sound bukkitSound;
    private float volume = 0.5f;
    private float pitch = 1.0f;

    public BattleSound(String s) {
        if (s.length() == 0) return;
        String[] x = s.split(":");
        if (x.length >= 1) {
            String sound = x[0];
            if (sound.charAt(0) == '$') {
                try {
                    bukkitSound = XSound.matchXSound(sound.substring(1)).orElseThrow((Supplier<Throwable>) () ->
                            new IllegalArgumentException("No sound matched")).parseSound();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            } else {
                soundName = sound;
            }
            if (x.length >= 2) {
                volume = Float.parseFloat(x[1]);

                if (x.length >= 3) {
                    pitch = Float.parseFloat(x[2]);
                }
            }
        }
    }

    public void play(Player player) {
        if (soundName != null)
            player.playSound(player.getLocation(), soundName, volume, pitch);
        else if (bukkitSound != null)
            player.playSound(player.getLocation(), bukkitSound, volume, pitch);
    }

    public void play(Location location) {
        if (soundName != null)
            location.getWorld().playSound(location, soundName, volume, pitch);
        else if (bukkitSound != null)
            location.getWorld().playSound(location, bukkitSound, volume, pitch);
    }

    @Override
    public String toString() {
        if (bukkitSound != null)
            return "$" + bukkitSound.name() + ":" + volume + ":" + pitch;
        else
            return soundName + ":" + volume + ":" + pitch;
    }
}
