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

package dev.anhcraft.battle.system.cleaners;

import dev.anhcraft.battle.system.cleaners.works.Work;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

public class WorkSession {
    private final CountDownLatch countDownLatch;
    private final long start;
    private final Logger logger;

    public WorkSession(int sz, Logger logger) {
        this.countDownLatch = new CountDownLatch(sz);
        this.logger = logger;
        start = System.currentTimeMillis();
    }

    public void done(Work work){
        logger.info(String.format("Finished work #%s in %s ms", work.id(), System.currentTimeMillis() - start));
        countDownLatch.countDown();
    }

    public void await(){
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
