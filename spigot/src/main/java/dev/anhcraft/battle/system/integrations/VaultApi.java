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
package dev.anhcraft.battle.system.integrations;

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
