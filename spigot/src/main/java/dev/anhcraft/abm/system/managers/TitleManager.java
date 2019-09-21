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
package dev.anhcraft.abm.system.managers;

import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.BattleComponent;
import dev.anhcraft.abm.utils.PlaceholderUtil;
import org.bukkit.entity.Player;

import java.util.function.Function;
import java.util.function.UnaryOperator;

public class TitleManager extends BattleComponent {
    public TitleManager(BattlePlugin plugin) {
        super(plugin);
    }

    public void send(Player target, String titleLocalePath, String subTitleLocalePath){
        send(target, titleLocalePath, subTitleLocalePath, UnaryOperator.identity());
    }

    public void send(Player target, String titleLocalePath, String subTitleLocalePath, Function<String, String> x){
        String s1 = x.apply(PlaceholderUtil.formatPAPI(target, plugin.getLocaleConf().getString(titleLocalePath)));
        String s2 = x.apply(PlaceholderUtil.formatPAPI(target, plugin.getLocaleConf().getString(subTitleLocalePath)));
        target.sendTitle(s1, s2, 10, 70, 20);
    }
}
