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
package dev.anhcraft.battle.api.gui.screen;

import dev.anhcraft.battle.utils.CustomDataContainer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;

public class Window extends CustomDataContainer {
    private final WeakReference<Player> player;
    private View topView;
    private View bottomView;

    public Window(Player player) {
        this.player = new WeakReference<>(player);
    }

    @Nullable
    public View getTopView() {
        return topView;
    }

    public void setTopView(@Nullable View topView) {
        this.topView = topView;
    }

    @Nullable
    public View getBottomView() {
        return bottomView;
    }

    public void setBottomView(@Nullable View bottomView) {
        this.bottomView = bottomView;
    }

    @Nullable
    public Player getPlayer() {
        return player.get();
    }
}
