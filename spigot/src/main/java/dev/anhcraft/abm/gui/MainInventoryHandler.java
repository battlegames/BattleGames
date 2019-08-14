package dev.anhcraft.abm.gui;

import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.ext.gui.GuiHandler;
import dev.anhcraft.jvmkit.lang.annotation.Label;
import org.bukkit.entity.Player;

public class MainInventoryHandler extends GuiHandler {
    public MainInventoryHandler(BattlePlugin plugin) {
        super(plugin);
    }

    @Label("open")
    public void open(Player player){
        plugin.guiManager.openTopInventory(player, "inventory_menu");
    }

    @Label("open_gun")
    public void openGunInv(Player player){
        plugin.guiManager.openTopInventory(player, "inventory_gun");
    }

    @Label("open_magazine")
    public void openMagazineInv(Player player){
        plugin.guiManager.openTopInventory(player, "inventory_magazine");
    }

    @Label("open_ammo")
    public void openAmmoInv(Player player){
        plugin.guiManager.openTopInventory(player, "inventory_ammo");
    }
}
