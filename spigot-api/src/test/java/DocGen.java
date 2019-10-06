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

import dev.anhcraft.abm.api.inventory.items.*;
import dev.anhcraft.abm.api.misc.CustomBossBar;
import dev.anhcraft.abm.api.misc.ParticleEffect;
import dev.anhcraft.abm.api.misc.Skin;
import dev.anhcraft.confighelper.ConfigDoc;

import java.io.File;

public class DocGen {
    public static void main(String[] args){
        new ConfigDoc()
                .withSchema(ParticleEffect.SCHEMA)
                .withSchema(Skin.SCHEMA)
                .withSchema(CustomBossBar.SCHEMA)
                .withSchema(AmmoModel.SCHEMA)
                .withSchema(GrenadeModel.SCHEMA)
                .withSchema(GunModel.SCHEMA)
                .withSchema(MagazineModel.SCHEMA)
                .withSchema(ScopeModel.SCHEMA)
                .generate(new File("docs"));
    }
}
