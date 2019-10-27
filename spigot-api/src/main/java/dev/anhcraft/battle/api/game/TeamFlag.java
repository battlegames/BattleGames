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

package dev.anhcraft.battle.api.game;

import dev.anhcraft.battle.api.misc.BattleSound;
import dev.anhcraft.battle.api.misc.Resettable;
import dev.anhcraft.battle.api.misc.info.InfoHolder;
import dev.anhcraft.battle.api.misc.info.Informative;
import dev.anhcraft.craftkit.common.utils.ChatUtil;
import dev.anhcraft.craftkit.entity.ArmorStand;
import dev.anhcraft.craftkit.entity.TrackedEntity;
import dev.anhcraft.jvmkit.utils.Condition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.UnaryOperator;

public class TeamFlag<T extends Enum & Team> implements Informative, Resettable {
    private final String[] displayNames = new String[3];
    private final AtomicInteger health = new AtomicInteger();
    private final TrackedEntity<ArmorStand> armorStand;
    private boolean valid;
    private T team;
    private int maxHealth;
    private boolean capturing;
    private BattleSound captureStartSound;
    private BattleSound captureStopSound;

    public TeamFlag(@NotNull TrackedEntity<ArmorStand> armorStand, int maxHealth) {
        Condition.argNotNull("armorStand", armorStand);
        this.armorStand = armorStand;
        this.maxHealth = maxHealth;
    }

    @Nullable
    public T getTeam() {
        return team;
    }

    public void setTeam(@Nullable T team) {
        this.team = team;
    }

    @NotNull
    public TrackedEntity<ArmorStand> getArmorStand() {
        return armorStand;
    }

    @NotNull
    public AtomicInteger getHealth() {
        return health;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean isCapturing() {
        return capturing;
    }

    public synchronized void setCapturing(boolean capturing) {
        this.capturing = capturing;
    }

    @NotNull
    public String[] getDisplayNames() {
        return displayNames;
    }

    public void updateDisplayName(UnaryOperator<String> uo){
        String n = uo.apply(displayNames[team != null ? (valid ? 0 : 1) : 2]);
        armorStand.getEntity().setName(ChatUtil.formatColorCodes(n));
        armorStand.getEntity().sendUpdate();
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    @Nullable
    public BattleSound getCaptureStartSound() {
        return captureStartSound;
    }

    public void setCaptureStartSound(@Nullable BattleSound captureStartSound) {
        this.captureStartSound = captureStartSound;
    }

    @Nullable
    public BattleSound getCaptureStopSound() {
        return captureStopSound;
    }

    public void setCaptureStopSound(@Nullable BattleSound captureStopSound) {
        this.captureStopSound = captureStopSound;
    }

    @Override
    public void reset() {
        valid = false;
        health.set(0);
        displayNames[0] = null;
        displayNames[1] = null;
        displayNames[2] = null;
        maxHealth = 0;
        captureStartSound = null;
        captureStopSound = null;
    }

    @Override
    public void inform(@NotNull InfoHolder holder) {
        holder.inform("health", health.get()).inform("max_health", maxHealth);
        if(team != null) holder.inform("team", team.getLocalizedName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamFlag<?> teamFlag = (TeamFlag<?>) o;
        return maxHealth == teamFlag.maxHealth && armorStand.equals(teamFlag.armorStand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(armorStand, maxHealth);
    }
}
