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

package dev.anhcraft.battle.api.events.game;

import dev.anhcraft.battle.api.arena.game.LocalGame;
import dev.anhcraft.battle.api.arena.team.BWTeam;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class BedBreakEvent extends GameEvent implements Cancellable {
    public static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final Block bed;
    private final BWTeam playerTeam;
    private final BWTeam targetTeam;
    private boolean cancelled;

    public BedBreakEvent(@NotNull LocalGame game, @NotNull Player player, @NotNull Block bed, @NotNull BWTeam playerTeam, @NotNull BWTeam targetTeam) {
        super(game);
        this.player = player;
        this.bed = bed;
        this.playerTeam = playerTeam;
        this.targetTeam = targetTeam;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    @NotNull
    public LocalGame getGame() {
        return (LocalGame) game;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    @NotNull
    public Block getBed() {
        return bed;
    }

    @NotNull
    public BWTeam getPlayerTeam() {
        return playerTeam;
    }

    @NotNull
    public BWTeam getTargetTeam() {
        return targetTeam;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
