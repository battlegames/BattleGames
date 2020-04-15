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

package dev.anhcraft.battle.system.debugger;

import java.util.concurrent.TimeUnit;

public class TimingStack {
    private final long start;
    private long end;

    public TimingStack(long start) {
        this.start = start;
    }

    public boolean inProgress(){
        return end == 0;
    }

    public long delta(){
        return end - start;
    }

    public long delta(TimeUnit unit){
        return unit.convert(end - start, TimeUnit.MILLISECONDS);
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }
}
