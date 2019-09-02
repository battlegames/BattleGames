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

    @Label("open_scope")
    public void openScopeInv(Player player){
        APIProvider.get().getGuiManager().openTopInventory(player, "inventory_scope");
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
