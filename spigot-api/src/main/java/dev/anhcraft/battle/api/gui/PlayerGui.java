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
package dev.anhcraft.battle.api.gui;

import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class PlayerGui {
    private BattleGui topGui;
    private BattleGui bottomGui;
    private Inventory topInv;
    private Map<String, Object> sharedData;

    @Nullable
    public BattleGui getTopGui() {
        return topGui;
    }

    public void setTopGui(@Nullable BattleGui topGui) {
        this.topGui = topGui;
    }

    @Nullable
    public BattleGui getBottomGui() {
        return bottomGui;
    }

    public void setBottomGui(@Nullable BattleGui bottomGui) {
        this.bottomGui = bottomGui;
    }

    @Nullable
    public Inventory getTopInv() {
        return topInv;
    }

    public void setTopInv(@Nullable Inventory topInv) {
        this.topInv = topInv;
    }

    @NotNull
    public Map<String, Object> getSharedData() {
        if(sharedData == null){
            sharedData = new HashMap<>();
        }
        return sharedData;
    }
}
