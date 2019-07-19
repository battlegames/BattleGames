package dev.anhcraft.abm.utils;

import org.bukkit.Bukkit;

public enum GameVersion {
    v1_13_R1(1),
    v1_13_R2(2),
    v1_14_R1(3);

    private static final GameVersion version = GameVersion.valueOf(Bukkit.getServer().getClass().getPackage()
            .getName().replace(".",  ",").split(",")[3]);

    private int id;

    GameVersion(int id){
        this.id = id;
    }

    public int getId(){
        return this.id;
    }

    public static GameVersion getVersion(){
        return version;
    }
}
