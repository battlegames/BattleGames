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

package dev.anhcraft.battle.tasks;

import dev.anhcraft.battle.ApiProvider;
import dev.anhcraft.battle.BattleComponent;
import dev.anhcraft.battle.BattlePlugin;
import dev.anhcraft.battle.api.arena.Arena;
import dev.anhcraft.battle.api.arena.game.Game;
import dev.anhcraft.battle.utils.BlockPosition;
import dev.anhcraft.battle.utils.info.InfoHolder;
import dev.anhcraft.battle.utils.info.InfoReplacer;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JoinSignUpdateTask extends BattleComponent implements Runnable {
    public JoinSignUpdateTask(BattlePlugin plugin) {
        super(plugin);
    }

    @Override
    public void run() {
        List<String> lines = plugin.getGeneralConfig().getJoinSignDynamicLines();
        if (lines == null || lines.isEmpty()) return;
        for (Iterator<Map.Entry<BlockPosition, String>> it = plugin.getServerData().getJoinSigns().iterator(); it.hasNext(); ) {
            Map.Entry<BlockPosition, String> e = it.next();
            Block b = e.getKey().getBlock();
            if (b.getState() instanceof Sign) {
                Arena arena = plugin.getArena(e.getValue());
                if (arena == null) continue;
                InfoHolder infoHolder;
                Game game = ApiProvider.consume().getArenaManager().getGame(arena);
                if (game != null) {
                    infoHolder = new InfoHolder("game_");
                    game.inform(infoHolder);
                } else {
                    infoHolder = new InfoHolder("arena_");
                    arena.inform(infoHolder);
                }
                InfoReplacer compiled = infoHolder.compile();
                Sign state = (Sign) b.getState();
                int i = 0;
                for (String s : lines) {
                    state.setLine(i, compiled.replace(s));
                    i++;
                }
                state.update(true);
                continue;
            }
            it.remove();
        }
    }
}
