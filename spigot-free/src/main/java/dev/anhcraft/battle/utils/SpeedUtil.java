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

package dev.anhcraft.battle.utils;

import dev.anhcraft.battle.BattlePlugin;
import dev.anhcraft.battle.api.BattleApi;
import dev.anhcraft.battle.api.GeneralConfig;
import dev.anhcraft.jvmkit.utils.MathUtil;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SpeedUtil {
    @Nullable
    public static double[] getSpeedModifiers(Player player) {
        List<MetadataValue> list = player.getMetadata("BattleSpeedModifiers");
        return list.isEmpty() ? null : (double[]) list.get(0).value();
    }

    public static void setSpeedModifiers(Player player, @Nullable double[] v) {
        if (v == null) {
            player.removeMetadata("BattleSpeedModifiers", (BattlePlugin) BattleApi.getInstance());
        } else {
            player.setMetadata("BattleSpeedModifiers", new FixedMetadataValue((BattlePlugin) BattleApi.getInstance(), v));
        }
    }

    public static void setModifier(Player player, SpeedFactor factor, double speed) {
        double[] doubles = getSpeedModifiers(player);
        if (doubles == null) {
            setSpeedModifiers(player, doubles = new double[SpeedFactor.values().length]);
        }
        doubles[factor.ordinal()] = speed;
        recalculateSpeed(player, doubles);
    }

    private static void recalculateSpeed(Player player, double[] doubles) {
        double sum = 0;
        for (double v : doubles) {
            sum += v;
        }
        GeneralConfig c = BattleApi.getInstance().getGeneralConfig();
        player.setWalkSpeed(MathUtil.clampFloat((float) (c.getWalkSpeed() + sum), -0.5f, 1));
        player.setFlySpeed(MathUtil.clampFloat((float) (c.getFlySpeed() + sum), -0.5f, 1));
    }

    public static void resetSpeed(Player player) {
        GeneralConfig c = BattleApi.getInstance().getGeneralConfig();
        player.setWalkSpeed(MathUtil.clampFloat((float) c.getWalkSpeed(), -0.5f, 1));
        player.setFlySpeed(MathUtil.clampFloat((float) c.getFlySpeed(), -0.5f, 1));
        setSpeedModifiers(player, null);
    }
}
