package dev.anhcraft.abm.system.integrations;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;

public class VaultApi {
    private static Economy eco;

    public static boolean init(){
        ServicesManager sm = Bukkit.getServicesManager();

        RegisteredServiceProvider<Economy> ecoPr = sm.getRegistration(Economy.class);
        if(ecoPr != null) {
            eco = ecoPr.getProvider();
        }

        return eco != null;
    }

    public static Economy getEconomyApi(){
        return eco;
    }

    public static boolean hasEnoughBalance(OfflinePlayer player, double balance){
        return eco.getBalance(player) >= balance;
    }

    public static boolean setBalance(OfflinePlayer player, double newBalance){
        if(eco == null) return false;
        double delta = eco.getBalance(player)-newBalance;
        double abs = Math.abs(delta);
        if(delta == 0) return true;
        else if(delta > 0) return eco.withdrawPlayer(player, abs).transactionSuccess();
        else return eco.depositPlayer(player, abs).transactionSuccess();
    }
}
