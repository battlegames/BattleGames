package dev.anhcraft.abm.gui;

import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.gui.core.BattleGuiHandler;
import dev.anhcraft.abm.gui.core.SlotClickHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MainInventoryHandler extends BattleGuiHandler {
    public MainInventoryHandler(BattlePlugin plugin) {
        super(plugin);
    }

    @SlotClickHandler("open")
    public void open(InventoryClickEvent event){
        plugin.guiManager.openInventory((Player) event.getWhoClicked(), "inventory_menu");
    }

    @SlotClickHandler("open_gun")
    public void openGunInv(InventoryClickEvent event){
        plugin.guiManager.openInventory((Player) event.getWhoClicked(), "inventory_gun");
    }

    @SlotClickHandler("open_magazine")
    public void openMagazineInv(InventoryClickEvent event){
        plugin.guiManager.openInventory((Player) event.getWhoClicked(), "inventory_magazine");
    }

    @SlotClickHandler("open_ammo")
    public void openAmmoInv(InventoryClickEvent event){
        plugin.guiManager.openInventory((Player) event.getWhoClicked(), "inventory_ammo");
    }
}
