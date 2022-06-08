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
package dev.anhcraft.battle.api.inventory.item;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import dev.anhcraft.battle.ApiProvider;
import dev.anhcraft.battle.utils.info.InfoHolder;
import dev.anhcraft.jvmkit.utils.Condition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Gun extends Weapon<GunModel> {
    @NotNull
    private Magazine magazine = new Magazine();

    @Nullable
    private Scope scope;

    private int nextSpray;
    private long lastSprayTime;

    @Nullable
    public Scope getScope() {
        return scope;
    }

    public void setScope(@Nullable Scope scope) {
        this.scope = scope;
    }

    @NotNull
    public Magazine getMagazine() {
        return magazine;
    }

    public void setMagazine(@NotNull Magazine magazine) {
        Condition.notNull(magazine, "Magazine must be non-null");
        this.magazine = magazine;
    }

    public int getNextSpray() {
        return nextSpray;
    }

    public void setNextSpray(int nextSpray) {
        this.nextSpray = nextSpray;
    }

    public int nextSpray() {
        if (getModel() != null) {
            int max = getModel().getSprayPattern().size();
            if (max > 0) {
                if (System.currentTimeMillis() - lastSprayTime >= 500) nextSpray = 0;
                else if (++nextSpray == max) nextSpray = max - 1;
                lastSprayTime = System.currentTimeMillis();
                return nextSpray;
            }
        }
        return -1;
    }

    public long getLastSprayTime() {
        return lastSprayTime;
    }

    public void setLastSprayTime(long lastSprayTime) {
        this.lastSprayTime = lastSprayTime;
    }

    @Override
    public void save(NBTCompound compound) {
        if (getModel() != null) compound.setString(ItemTag.GUN_ID, getModel().getId());

        NBTCompound mc = compound.addCompound(ItemTag.GUN_MAGAZINE);
        magazine.save(mc);

        if (scope != null) {
            NBTCompound sc = compound.addCompound(ItemTag.GUN_SCOPE);
            scope.save(sc);
        }
        compound.setInteger(ItemTag.GUN_NEXT_SPRAY, nextSpray);
        compound.setLong(ItemTag.GUN_LAST_SPRAY_TIME, lastSprayTime);
    }

    @Override
    public void load(NBTCompound compound) {
        setModel(ApiProvider.consume().getGunModel(compound.getString(ItemTag.GUN_ID)));
        NBTCompound mag = compound.getCompound(ItemTag.GUN_MAGAZINE);
        if (mag != null) magazine.load(mag);

        NBTCompound scp = compound.getCompound(ItemTag.GUN_SCOPE);
        if (scp != null) (scope = (scope == null) ? new Scope() : scope).load(scp);

        Integer nextSpray = compound.getInteger(ItemTag.GUN_NEXT_SPRAY);
        if (nextSpray != null) this.nextSpray = nextSpray;

        Long lastSprayTime = compound.getLong(ItemTag.GUN_LAST_SPRAY_TIME);
        if (lastSprayTime != null) this.lastSprayTime = lastSprayTime;
    }

    @Override
    public void inform(@NotNull InfoHolder holder) {
        if (getModel() != null)
            getModel().inform(holder);
        holder.link(magazine.collectInfo(null));
        if (scope != null)
            holder.link(scope.collectInfo(null));
    }
}
