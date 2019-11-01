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

import dev.anhcraft.jvmkit.helpers.PaginationHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class BattleGui {
    private Gui gui;
    private PlayerGui playerGui;
    private BattleGuiSlot[] slots;
    private PaginationHelper<BattleGuiSlot> pagination;
    private Map<String, Object> sharedData;

    public BattleGui(Gui gui, PlayerGui playerGui, BattleGuiSlot[] slots) {
        this.gui = gui;
        this.playerGui = playerGui;
        this.slots = slots;
    }

    public Gui getGui() {
        return gui;
    }

    public BattleGuiSlot[] getSlots() {
        return slots;
    }

    @Nullable
    public PaginationHelper<BattleGuiSlot> getPagination() {
        return pagination;
    }

    public PlayerGui getPlayerGui() {
        return playerGui;
    }

    public void setPagination(@Nullable PaginationHelper<BattleGuiSlot> pagination) {
        this.pagination = pagination;
    }

    public void updatePagination(){
        pagination.each(battleGuiSlot -> slots[battleGuiSlot.getIndex()] = battleGuiSlot);
    }

    @NotNull
    public Map<String, Object> getSharedData() {
        if(sharedData == null){
            sharedData = new HashMap<>();
        }
        return sharedData;
    }
}
