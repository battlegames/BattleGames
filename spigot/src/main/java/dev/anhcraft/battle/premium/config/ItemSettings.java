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

package dev.anhcraft.battle.premium.config;

import dev.anhcraft.battle.api.BattleSound;
import dev.anhcraft.config.annotations.Configurable;
import dev.anhcraft.config.annotations.Description;
import dev.anhcraft.config.annotations.Path;
import dev.anhcraft.config.annotations.Setting;
import org.jetbrains.annotations.Nullable;

@Configurable
public class ItemSettings {
    @Setting
    @Path("medical_kit.health_bonus")
    @Description("Additional health give to the player")
    private int medicalKitBonusHealth;

    @Setting
    @Path("medical_kit.use_sound")
    @Description("Sound to be played when using the medical kit")
    private BattleSound medicalKitUseSound;

    @Setting
    @Path("adrenaline_shot.use_sound")
    @Description("Sound to be played when using the adrenaline shot")
    private BattleSound adrenalineShotUseSound;

    public int getMedicalKitBonusHealth() {
        return medicalKitBonusHealth;
    }

    @Nullable
    public BattleSound getMedicalKitUseSound() {
        return medicalKitUseSound;
    }

    @Nullable
    public BattleSound getAdrenalineShotUseSound() {
        return adrenalineShotUseSound;
    }
}
