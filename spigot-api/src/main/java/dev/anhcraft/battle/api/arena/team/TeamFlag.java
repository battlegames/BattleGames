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

package dev.anhcraft.battle.api.arena.team;

import dev.anhcraft.battle.api.arena.game.options.FlagOptions;
import dev.anhcraft.battle.impl.Informative;
import dev.anhcraft.battle.impl.Resettable;
import dev.anhcraft.battle.utils.info.InfoHolder;
import dev.anhcraft.craftkit.common.utils.ChatUtil;
import dev.anhcraft.craftkit.entity.ArmorStand;
import dev.anhcraft.craftkit.entity.TrackedEntity;
import dev.anhcraft.jvmkit.utils.Condition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;

public class TeamFlag<T extends Team> implements Informative, Resettable {
    private final AtomicInteger health = new AtomicInteger();
    private final TrackedEntity<ArmorStand> armorStand;
    private final FlagOptions options;
    private T team;
    private boolean valid;
    private boolean capturing;

    public TeamFlag(@NotNull TrackedEntity<ArmorStand> armorStand, @NotNull FlagOptions options) {
        Condition.argNotNull("armorStand", armorStand);
        Condition.argNotNull("options", options);
        this.armorStand = armorStand;
        this.options = options;
    }

    @NotNull
    public TrackedEntity<ArmorStand> getArmorStand() {
        return armorStand;
    }

    @NotNull
    public FlagOptions getOptions() {
        return options;
    }

    @NotNull
    public AtomicInteger getHealth() {
        return health;
    }

    @Nullable
    public T getTeam() {
        return team;
    }

    public void setTeam(@Nullable T team) {
        this.team = team;
    }

    public boolean isValid() {
        return valid;
    }

    public synchronized void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean isCapturing() {
        return capturing;
    }

    public synchronized void setCapturing(boolean capturing) {
        this.capturing = capturing;
    }

    public void updateDisplayName(){
        InfoHolder h;
        inform(h = new InfoHolder("flag_"));
        String n = team != null ? (valid ? options.getValidDisplayName() : options.getInvalidDisplayName()) : options.getNeutralDisplayName();
        armorStand.getEntity().setName(ChatUtil.formatColorCodes(h.compile().replace(n)));
        armorStand.getEntity().sendUpdate();
    }

    @Override
    public void reset() {
        valid = false;
        health.set(0);
    }

    @Override
    public void inform(@NotNull InfoHolder holder) {
        holder.inform("health", health.get()).inform("max_health", options.getMaxHealth());
        if(team != null) {
            holder.inform("team", team.getLocalizedName());
        }
    }
}
