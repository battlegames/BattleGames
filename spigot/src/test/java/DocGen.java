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

import dev.anhcraft.battle.api.game.Arena;
import dev.anhcraft.battle.api.inventory.items.*;
import dev.anhcraft.battle.api.market.Category;
import dev.anhcraft.battle.api.market.Market;
import dev.anhcraft.battle.api.market.Product;
import dev.anhcraft.battle.api.misc.*;
import dev.anhcraft.battle.utils.GeneralConfig;
import dev.anhcraft.confighelper.ConfigDoc;

import java.io.File;

public class DocGen {
    public static void main(String[] args){
        new ConfigDoc()
                .withSchema(Ammo.Bullet.SCHEMA)
                .withSchema(AmmoModel.SCHEMA)
                .withSchema(MagazineModel.SCHEMA)
                .withSchema(GrenadeModel.SCHEMA)
                .withSchema(GunModel.SCHEMA)
                .withSchema(ScopeModel.SCHEMA)
                .withSchema(Kit.SCHEMA)
                .withSchema(ItemSkin.SCHEMA)
                .withSchema(BattleScoreboard.SCHEMA)
                .withSchema(BattleChat.SCHEMA)
                .withSchema(BattleBar.SCHEMA)
                .withSchema(BattleEffect.SCHEMA)
                .withSchema(BattleFirework.SCHEMA)
                .withSchema(BattleFireworkEffect.SCHEMA)
                .withSchema(BattlePotionEffect.SCHEMA)
                .withSchema(BattleParticle.SCHEMA)
                .withSchema(FakeBlockEffect.SCHEMA)
                .withSchema(Perk.SCHEMA)
                .withSchema(Booster.SCHEMA)
                .withSchema(Arena.SCHEMA)
                .withSchema(Rollback.SCHEMA)
                .withSchema(Product.SCHEMA)
                .withSchema(Category.SCHEMA)
                .withSchema(Market.SCHEMA)
                .withSchema(GeneralConfig.SCHEMA)
                .addJavadoc("dev.anhcraft.battle.*", "https://anhcraft.dev/jd/battle")
                .addJavadoc("dev.anhcraft.craftkit.*", "https://anhcraft.dev/jd/craftkit/spigot")
                .generate(new File("docs"));
    }
}
