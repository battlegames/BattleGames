package dev.anhcraft.abm.gui;

import dev.anhcraft.abm.api.APIProvider;
import dev.anhcraft.abm.api.gui.GuiHandler;
import dev.anhcraft.jvmkit.lang.annotation.Label;
import org.bukkit.entity.Player;

public class MainInventoryHandler extends GuiHandler {
    @Label("open")
    public void open(Player player){
        APIProvider.get().getGuiManager().openTopInventory(player, "inventory_menu");
    }

    @Label("open_gun")
    public void openGunInv(Player player){
        APIProvider.get().getGuiManager().openTopInventory(player, "inventory_gun");
    }

    @Label("open_magazine")
    public void openMagazineInv(Player player){
        APIProvider.get().getGuiManager().openTopInventory(player, "inventory_magazine");
    }

    @Label("open_ammo")
    public void openAmmoInv(Player player){
        APIProvider.get().getGuiManager().openTopInventory(player, "inventory_ammo");
    }
}
