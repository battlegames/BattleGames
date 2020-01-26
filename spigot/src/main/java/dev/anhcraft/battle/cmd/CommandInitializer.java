/*
 *
 *     Battle Minigame.
 *     Copyright (c) 2019 by anhcraft.
 *
 *     plugin program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     plugin program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with plugin program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package dev.anhcraft.battle.cmd;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.PaperCommandManager;
import dev.anhcraft.battle.BattleComponent;
import dev.anhcraft.battle.BattlePlugin;

public class CommandInitializer extends BattleComponent {
    public CommandInitializer(BattlePlugin plugin) {
        super(plugin);

        PaperCommandManager manager = new PaperCommandManager(plugin);
        manager.enableUnstableAPI("help");
        manager.registerCommand(new MainCommand(plugin));
        manager.registerCommand(new EditorCommand(plugin));
        CommandCompletions<BukkitCommandCompletionContext> cc = manager.getCommandCompletions();
        cc.registerAsyncCompletion("ammo", context -> plugin.AMMO_MAP.keySet());
        cc.registerAsyncCompletion("gun", context -> plugin.GUN_MAP.keySet());
        cc.registerAsyncCompletion("magazine", context -> plugin.MAGAZINE_MAP.keySet());
        cc.registerAsyncCompletion("scope", context -> plugin.SCOPE_MAP.keySet());
        cc.registerAsyncCompletion("arena", context -> plugin.ARENA_MAP.keySet());
        cc.registerAsyncCompletion("grenade", context -> plugin.GRENADE_MAP.keySet());
        cc.registerAsyncCompletion("perk", context -> plugin.PERK_MAP.keySet());
        cc.registerAsyncCompletion("booster", context -> plugin.BOOSTER_MAP.keySet());
        cc.registerAsyncCompletion("gui", context -> plugin.guiManager.GUI.keySet());
    }
}
