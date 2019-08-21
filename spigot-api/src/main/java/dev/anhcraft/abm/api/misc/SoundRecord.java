package dev.anhcraft.abm.api.misc;

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
        String[] x = s.split(":");
        if(x.length >= 1){
            String sound = x[0];
            if(sound.charAt(0) == '$')
                bukkitSound = Sound.valueOf(sound.substring(1).toUpperCase());
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
