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

import dev.anhcraft.battle.api.*;
import dev.anhcraft.battle.api.advancement.Advancement;
import dev.anhcraft.battle.api.advancement.Progression;
import dev.anhcraft.battle.api.arena.Arena;
import dev.anhcraft.battle.api.arena.game.MobGroup;
import dev.anhcraft.battle.api.arena.game.MobRescueObjective;
import dev.anhcraft.battle.api.arena.game.Mode;
import dev.anhcraft.battle.api.arena.game.options.*;
import dev.anhcraft.battle.api.chat.BattleChat;
import dev.anhcraft.battle.api.effect.BattleEffect;
import dev.anhcraft.battle.api.effect.BattleParticle;
import dev.anhcraft.battle.api.effect.FakeBlockEffect;
import dev.anhcraft.battle.api.effect.firework.BattleFirework;
import dev.anhcraft.battle.api.effect.firework.BattleFireworkEffect;
import dev.anhcraft.battle.api.effect.potion.BattlePotionEffect;
import dev.anhcraft.battle.api.gui.Gui;
import dev.anhcraft.battle.api.gui.struct.Component;
import dev.anhcraft.battle.api.inventory.ItemSkin;
import dev.anhcraft.battle.api.inventory.item.*;
import dev.anhcraft.battle.api.market.Category;
import dev.anhcraft.battle.api.market.Market;
import dev.anhcraft.battle.api.market.PackageDetails;
import dev.anhcraft.battle.api.market.Product;
import dev.anhcraft.configdoc.ConfigDocGenerator;
import dev.anhcraft.jvmkit.utils.FileUtil;
import org.junit.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DocGen {
    @Test
    public void sort() {
        String s = "\n" +
                "                .withSchema(Ammo.Bullet.SCHEMA)\n" +
                "                .withSchema(AmmoModel.SCHEMA)\n" +
                "                .withSchema(MagazineModel.SCHEMA)\n" +
                "                .withSchema(GrenadeModel.SCHEMA)\n" +
                "                .withSchema(GunModel.SCHEMA)\n" +
                "                .withSchema(ScopeModel.SCHEMA)\n" +
                "                .withSchema(Kit.SCHEMA)\n" +
                "                .withSchema(ItemSkin.SCHEMA)\n" +
                "                .withSchema(BattleScoreboard.SCHEMA)\n" +
                "                .withSchema(BattleChat.SCHEMA)\n" +
                "                .withSchema(BattleBar.SCHEMA)\n" +
                "                .withSchema(BattleEffect.SCHEMA)\n" +
                "                .withSchema(BattleFirework.SCHEMA)\n" +
                "                .withSchema(BattleFireworkEffect.SCHEMA)\n" +
                "                .withSchema(BattlePotionEffect.SCHEMA)\n" +
                "                .withSchema(BattleParticle.SCHEMA)\n" +
                "                .withSchema(FakeBlockEffect.SCHEMA)\n" +
                "                .withSchema(Perk.SCHEMA)\n" +
                "                .withSchema(Booster.SCHEMA)\n" +
                "                .withSchema(Arena.SCHEMA)\n" +
                "                .withSchema(Rollback.SCHEMA)\n" +
                "                .withSchema(Product.SCHEMA)\n" +
                "                .withSchema(PackageDetails.SCHEMA)\n" +
                "                .withSchema(Category.SCHEMA)\n" +
                "                .withSchema(Market.SCHEMA)\n" +
                "                .withSchema(GeneralConfig.SCHEMA)\n" +
                "                .withSchema(Gui.SCHEMA)\n" +
                "                .withSchema(Component.SCHEMA)\n" +
                "                .withSchema(DeathmatchOptions.SCHEMA)\n" +
                "                .withSchema(TeamDeathmatchOptions.SCHEMA)\n" +
                "                .withSchema(CaptureTheFlagOptions.SCHEMA)\n" +
                "                .withSchema(FlagOptions.SCHEMA)\n" +
                "                .withSchema(BedWarOptions.SCHEMA)\n" +
                "                .withSchema(BWTeamOptions.SCHEMA)\n" +
                "                .withSchema(MobRescueOptions.SCHEMA)\n" +
                "                .withSchema(MobGroup.SCHEMA)\n" +
                "                .withSchema(MobRescueObjective.SCHEMA)\n" +
                "                .withSchema(Advancement.SCHEMA)\n" +
                "                .withSchema(Progression.SCHEMA)\n" +
                "                .withSchema(Mode.SCHEMA)";
        List<String> x = Arrays.asList(s.split("\n"));
        Collections.sort(x);
        System.out.println(String.join("\n", x));
    }

    @Test
    public void gen() {
        new ConfigDocGenerator()
                .withSchema(Advancement.SCHEMA)
                .withSchema(Ammo.Bullet.SCHEMA)
                .withSchema(AmmoModel.SCHEMA)
                .withSchema(Arena.SCHEMA)
                .withSchema(BWTeamOptions.SCHEMA)
                .withSchema(BattleBar.SCHEMA)
                .withSchema(BattleChat.SCHEMA)
                .withSchema(BattleEffect.SCHEMA)
                .withSchema(BattleFirework.SCHEMA)
                .withSchema(BattleFireworkEffect.SCHEMA)
                .withSchema(BattleParticle.SCHEMA)
                .withSchema(BattlePotionEffect.SCHEMA)
                .withSchema(BattleScoreboard.SCHEMA)
                .withSchema(BedWarOptions.SCHEMA)
                .withSchema(Booster.SCHEMA)
                .withSchema(CaptureTheFlagOptions.SCHEMA)
                .withSchema(Category.SCHEMA)
                .withSchema(Component.SCHEMA)
                .withSchema(DeathmatchOptions.SCHEMA)
                .withSchema(FakeBlockEffect.SCHEMA)
                .withSchema(FlagOptions.SCHEMA)
                .withSchema(GeneralConfig.SCHEMA)
                .withSchema(GrenadeModel.SCHEMA)
                .withSchema(Gui.SCHEMA)
                .withSchema(GunModel.SCHEMA)
                .withSchema(ItemSkin.SCHEMA)
                .withSchema(Kit.SCHEMA)
                .withSchema(MagazineModel.SCHEMA)
                .withSchema(Market.SCHEMA)
                .withSchema(MobGroup.SCHEMA)
                .withSchema(MobRescueObjective.SCHEMA)
                .withSchema(MobRescueOptions.SCHEMA)
                .withSchema(Mode.SCHEMA)
                .withSchema(PackageDetails.SCHEMA)
                .withSchema(Perk.SCHEMA)
                .withSchema(Product.SCHEMA)
                .withSchema(Progression.SCHEMA)
                .withSchema(Rollback.SCHEMA)
                .withSchema(ScopeModel.SCHEMA)
                .withSchema(TeamDeathmatchOptions.SCHEMA)
                .addJavadoc("dev.anhcraft.battle.*", "https://anhcraft.dev/jd/battle")
                .addJavadoc("dev.anhcraft.craftkit.*", "https://anhcraft.dev/jd/craftkit/spigot")
                .generate(new File(Paths.get(FileUtil.WORKING_DIR_PATH).getParent().toFile(), "docs"));
    }
}
