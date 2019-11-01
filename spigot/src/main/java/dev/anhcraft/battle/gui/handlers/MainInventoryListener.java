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
package dev.anhcraft.battle.gui.handlers;

import dev.anhcraft.battle.api.ApiProvider;
import dev.anhcraft.battle.api.gui.GuiListener;
import dev.anhcraft.jvmkit.lang.annotation.Label;
import org.bukkit.entity.Player;

public class MainInventoryListener extends GuiListener {
    @Label("open")
    public void open(Player player){
        ApiProvider.consume().getGuiManager().openTopGui(player, "inventory_menu");
    }

    @Label("open_scope")
    public void openScopeInv(Player player){
        ApiProvider.consume().getGuiManager().openTopGui(player, "inventory_scope");
    }

    @Label("open_gun")
    public void openGunInv(Player player){
        ApiProvider.consume().getGuiManager().openTopGui(player, "inventory_gun");
    }

    @Label("open_magazine")
    public void openMagazineInv(Player player){
        ApiProvider.consume().getGuiManager().openTopGui(player, "inventory_magazine");
    }

    @Label("open_ammo")
    public void openAmmoInv(Player player){
        ApiProvider.consume().getGuiManager().openTopGui(player, "inventory_ammo");
    }

    @Label("open_grenade")
    public void openGrenadeInv(Player player){
        ApiProvider.consume().getGuiManager().openTopGui(player, "inventory_grenade");
    }
}
