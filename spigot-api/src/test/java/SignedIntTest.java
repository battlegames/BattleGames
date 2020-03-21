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

import dev.anhcraft.battle.utils.SignedInt;
import org.junit.Assert;
import org.junit.Test;

public class SignedIntTest {
    @Test
    public void a(){
        Assert.assertEquals(SignedInt.ONE.asInt(), 1);
        Assert.assertEquals(SignedInt.ONE.negate().asInt(), -1);
        Assert.assertEquals(SignedInt.ONE.add(SignedInt.TWO).asInt(), 3);
        Assert.assertEquals(SignedInt.ONE.add(SignedInt.TWO).negate().asInt(), -3);
        Assert.assertEquals(SignedInt.ONE.negate().add(SignedInt.TWO).asInt(), 1);
        Assert.assertEquals(SignedInt.ONE.add(SignedInt.TWO.negate()).asInt(), -1);
        Assert.assertEquals(SignedInt.ONE.negate().add(SignedInt.TWO.negate()).asInt(), -3);
        Assert.assertEquals(new SignedInt(-5).negate().add(SignedInt.TWO.negate()).asInt(), 3);
    }
}
