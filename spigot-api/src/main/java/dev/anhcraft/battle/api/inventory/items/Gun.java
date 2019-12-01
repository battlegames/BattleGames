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
package dev.anhcraft.battle.api.inventory.items;

import dev.anhcraft.battle.api.ApiProvider;
import dev.anhcraft.battle.utils.info.InfoHolder;
import dev.anhcraft.craftkit.cb_common.nbt.CompoundTag;
import dev.anhcraft.craftkit.cb_common.nbt.IntTag;
import dev.anhcraft.craftkit.cb_common.nbt.LongTag;
import dev.anhcraft.craftkit.cb_common.nbt.StringTag;
import org.apache.commons.lang.Validate;
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
        Validate.notNull(magazine, "Magazine must be non-null");
        this.magazine = magazine;
    }

    public int getNextSpray() {
        return nextSpray;
    }

    public void setNextSpray(int nextSpray) {
        this.nextSpray = nextSpray;
    }

    public int nextSpray() {
        if(getModel() != null){
            int max = getModel().getSprayPattern().size();
            if(max > 0) {
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
    public void save(CompoundTag compound) {
        if(getModel() != null) compound.put(ItemTag.GUN_ID, getModel().getId());

        CompoundTag mc = new CompoundTag();
        magazine.save(mc);
        compound.put(ItemTag.GUN_MAGAZINE, mc);

        if(scope != null) {
            CompoundTag sc = new CompoundTag();
            scope.save(sc);
            compound.put(ItemTag.GUN_SCOPE, sc);
        }
        compound.put(ItemTag.GUN_NEXT_SPRAY, nextSpray);
        compound.put(ItemTag.GUN_LAST_SPRAY_TIME, lastSprayTime);
    }

    @Override
    public void load(CompoundTag compound) {
        setModel(ApiProvider.consume().getGunModel(compound.getValue(ItemTag.GUN_ID, StringTag.class)));
        CompoundTag mag = compound.get(ItemTag.GUN_MAGAZINE, CompoundTag.class);
        if(mag != null) magazine.load(mag);

        CompoundTag scp = compound.get(ItemTag.GUN_SCOPE, CompoundTag.class);
        if(scp != null) (scope = (scope == null) ? new Scope() : scope).load(scp);

        Integer nextSpray = compound.getValue(ItemTag.GUN_NEXT_SPRAY, IntTag.class);
        if(nextSpray != null) this.nextSpray = nextSpray;

        Long lastSprayTime = compound.getValue(ItemTag.GUN_LAST_SPRAY_TIME, LongTag.class);
        if(lastSprayTime != null) this.lastSprayTime = lastSprayTime;
    }

    @Override
    public void inform(@NotNull InfoHolder holder) {
        if(getModel() != null)
            getModel().inform(holder);
        holder.link(magazine.collectInfo(null));
        if(scope != null)
            holder.link(scope.collectInfo(null));
    }
}
