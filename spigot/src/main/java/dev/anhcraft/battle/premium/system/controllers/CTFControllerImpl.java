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
package dev.anhcraft.battle.premium.system.controllers;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import dev.anhcraft.battle.BattlePlugin;
import dev.anhcraft.battle.api.arena.game.Game;
import dev.anhcraft.battle.api.arena.game.GamePlayer;
import dev.anhcraft.battle.api.arena.game.LocalGame;
import dev.anhcraft.battle.api.arena.game.Mode;
import dev.anhcraft.battle.api.arena.game.controllers.CaptureTheFlagController;
import dev.anhcraft.battle.api.arena.game.options.CaptureTheFlagOptions;
import dev.anhcraft.battle.api.arena.game.options.FlagOptions;
import dev.anhcraft.battle.api.arena.team.ABTeam;
import dev.anhcraft.battle.api.arena.team.TeamFlag;
import dev.anhcraft.battle.api.arena.team.TeamManager;
import dev.anhcraft.battle.api.events.game.FlagUpdateEvent;
import dev.anhcraft.craftkit.entity.ArmorStand;
import dev.anhcraft.craftkit.entity.TrackedEntity;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IntSummaryStatistics;
import java.util.List;

public class CTFControllerImpl extends TDMControllerImpl implements CaptureTheFlagController {
    private final Multimap<LocalGame, TeamFlag<ABTeam>> FLAG = LinkedHashMultimap.create();

    public CTFControllerImpl(BattlePlugin plugin) {
        super(plugin, Mode.CTF);

        String p = getMode().getId() + "_";

        plugin.getPapiExpansion().handlers.put(p + "flags", (player, pd, game, gp) -> {
            if (game == null) return null;
            Collection<TeamFlag<ABTeam>> f = FLAG.get(game);
            return f == null ? null : Integer.toString(f.size());
        });

        plugin.getPapiExpansion().handlers.put(p + "valid_flags", (player, pd, game, gp) -> {
            if (game == null) return null;
            Collection<TeamFlag<ABTeam>> f = FLAG.get(game);
            return f == null ? null : Long.toString(f.stream().filter(TeamFlag::isValid).count());
        });

        plugin.getPapiExpansion().handlers.put(p + "team_all_flags", (player, pd, game, gp) -> {
            if (game == null) return null;
            TeamManager<ABTeam> t = TEAM.get(game);
            if (t == null) return null;
            ABTeam team = t.getTeam(player);
            Collection<TeamFlag<ABTeam>> f = FLAG.get(game);
            return f == null ? null : Long.toString(f.stream().filter(flag -> flag.getTeam() == team).count());
        });

        plugin.getPapiExpansion().handlers.put(p + "team_valid_flags", (player, pd, game, gp) -> {
            if (game == null) return null;
            TeamManager<ABTeam> t = TEAM.get(game);
            if (t == null) return null;
            ABTeam team = t.getTeam(player);
            Collection<TeamFlag<ABTeam>> f = FLAG.get(game);
            return f == null ? null : Long.toString(f.stream().filter(flag -> flag.isValid() && flag.getTeam() == team).count());
        });

        plugin.getPapiExpansion().handlers.put(p + "team_a_all_flags", (player, pd, game, gp) -> {
            if (game == null) return null;
            TeamManager<ABTeam> t = TEAM.get(game);
            if (t == null) return null;
            Collection<TeamFlag<ABTeam>> f = FLAG.get(game);
            return f == null ? null : Long.toString(f.stream().filter(flag -> flag.getTeam() == ABTeam.TEAM_A).count());
        });

        plugin.getPapiExpansion().handlers.put(p + "team_a_valid_flags", (player, pd, game, gp) -> {
            if (game == null) return null;
            TeamManager<ABTeam> t = TEAM.get(game);
            if (t == null) return null;
            Collection<TeamFlag<ABTeam>> f = FLAG.get(game);
            return f == null ? null : Long.toString(f.stream().filter(flag -> flag.isValid() && flag.getTeam() == ABTeam.TEAM_A).count());
        });

        plugin.getPapiExpansion().handlers.put(p + "team_b_all_flags", (player, pd, game, gp) -> {
            if (game == null) return null;
            TeamManager<ABTeam> t = TEAM.get(game);
            if (t == null) return null;
            Collection<TeamFlag<ABTeam>> f = FLAG.get(game);
            return f == null ? null : Long.toString(f.stream().filter(flag -> flag.getTeam() == ABTeam.TEAM_B).count());
        });

        plugin.getPapiExpansion().handlers.put(p + "team_b_valid_flags", (player, pd, game, gp) -> {
            if (game == null) return null;
            TeamManager<ABTeam> t = TEAM.get(game);
            if (t == null) return null;
            Collection<TeamFlag<ABTeam>> f = FLAG.get(game);
            return f == null ? null : Long.toString(f.stream().filter(flag -> flag.isValid() && flag.getTeam() == ABTeam.TEAM_B).count());
        });
    }

    @Override
    public void onInitGame(@NotNull Game game) {
        super.onInitGame(game);
        if (game instanceof LocalGame) {
            LocalGame lc = (LocalGame) game;
            if (game.getArena().getGameOptions() instanceof CaptureTheFlagOptions) {
                CaptureTheFlagOptions options = (CaptureTheFlagOptions) game.getArena().getGameOptions();
                for (FlagOptions f : options.getFlags()) {
                    lc.addInvolvedWorld(f.getLocation().getWorld());
                }
            }
        }
    }

    @Override
    public void onJoin(@NotNull Player player, @NotNull LocalGame game) {
        super.onJoin(player, game);

        Collection<TeamFlag<ABTeam>> flags = FLAG.get(game);
        if (flags != null) {
            for (TeamFlag<ABTeam> f : flags) {
                f.getArmorStand().addViewer(player);
            }
        }
    }

    @Override
    public void onQuit(@NotNull Player player, @NotNull LocalGame game) {
        super.onQuit(player, game);

        Collection<TeamFlag<ABTeam>> flags = FLAG.get(game);
        if (flags != null) {
            for (TeamFlag<ABTeam> f : flags) {
                f.getArmorStand().removeViewer(player);
            }
        }
    }

    @Override
    protected void play(LocalGame game) {
        super.play(game);

        plugin.extension.getTaskHelper().newTask(() -> {
            Collection<FlagOptions> fs = ((CaptureTheFlagOptions) game.getArena().getGameOptions()).getFlags();
            for (FlagOptions k : fs) {
                ArmorStand armorStand = ArmorStand.spawn(k.getLocation());
                armorStand.setVisible(false);
                armorStand.setNameVisible(true);
                TrackedEntity<ArmorStand> te = plugin.extension.trackEntity(armorStand);
                te.setViewDistance(50);
                te.setViewers(new ArrayList<>(game.getPlayers().keySet()));
                TeamFlag<ABTeam> flag = new TeamFlag<>(te, k);
                flag.updateDisplayName();
                FLAG.put(game, flag);
            }
        });
    }

    private void startOccupyFlag(LocalGame game, TeamFlag<ABTeam> flag, Player occupier) {
        ABTeam team = TEAM.get(game).getTeam(occupier);
        if (team == null || flag.isCapturing() || (flag.isValid() && team == flag.getTeam())) return;
        flag.setCapturing(true);
        if (flag.getOptions().getStartCaptureSound() != null) {
            flag.getOptions().getStartCaptureSound().play(occupier);
        }
        String id = "ctf_flag_occupy_" + occupier.getName();
        int tid = plugin.extension.getTaskHelper().newTimerTask(() -> {
            if (occupier.getLocation().distance(flag.getArmorStand().getLocation()) >= 1.5) {
                stopOccupyFlag(game, flag, occupier);
                return;
            }
            if (flag.getTeam() == null || flag.getTeam() == team) {
                int h = flag.getHealth().incrementAndGet();
                if (h == 1) flag.setTeam(team);
                else if (h == flag.getOptions().getMaxHealth()) {
                    flag.setValid(true);
                    stopOccupyFlag(game, flag, occupier);
                }
            } else {
                int h = flag.getHealth().decrementAndGet();
                if (h == 0) flag.setTeam(null);
                else if (h == flag.getOptions().getMaxHealth() - 1) flag.setValid(false);
            }
            flag.updateDisplayName();
            FlagUpdateEvent e = new FlagUpdateEvent(game, occupier, team, flag);
            Bukkit.getPluginManager().callEvent(e);
        }, 0, 20);
        trackTask(game, id, tid);
    }

    private void stopOccupyFlag(LocalGame game, TeamFlag<ABTeam> flag, Player occupier) {
        cancelTask(game, "ctf_flag_occupy_" + occupier.getName());
        flag.setCapturing(false);
        if (flag.getOptions().getStopCaptureSound() != null) {
            flag.getOptions().getStopCaptureSound().play(occupier);
        }
    }

    @EventHandler
    public void asm(PlayerArmorStandManipulateEvent event) {
        if (event.getRightClicked().hasMetadata("abm_ctf_flag"))
            event.setCancelled(true);
    }

    @EventHandler
    public void sneak(PlayerToggleSneakEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.SPECTATOR) return;
        LocalGame game = plugin.arenaManager.getGame(event.getPlayer());
        if (game != null) {
            if (game.getMode() != getMode()) return;
            if (!event.isSneaking() && !hasTask(game, "ctf_flag_occupy_" + event.getPlayer().getName())) {
                return;
            }
            Location loc = event.getPlayer().getLocation();
            Collection<TeamFlag<ABTeam>> flags = FLAG.get(game);
            for (TeamFlag<ABTeam> flag : flags) {
                if (flag.getArmorStand().getLocation().distanceSquared(loc) >= 3) continue;
                if (event.isSneaking()) {
                    startOccupyFlag(game, flag, event.getPlayer());
                } else {
                    stopOccupyFlag(game, flag, event.getPlayer());
                }
            }
        }
    }

    @Override
    public void onEnd(@NotNull LocalGame game) {
        super.onEnd(game);

        FLAG.removeAll(game).forEach(f -> {
            f.getArmorStand().kill();
            plugin.extension.untrackEntity(f.getArmorStand());
            f.reset();
        });
    }

    @Override
    protected ABTeam handleResult(LocalGame game, IntSummaryStatistics sa, IntSummaryStatistics sb, List<GamePlayer> aPlayers, List<GamePlayer> bPlayers) {
        int a = 0, b = 0;
        Collection<TeamFlag<ABTeam>> flags = FLAG.get(game);
        for (TeamFlag<ABTeam> f : flags) {
            if (f.isValid()) {
                if (f.getTeam() == ABTeam.TEAM_A) a++;
                else b++;
            }
        }
        if (a == b) return super.handleResult(game, sa, sb, aPlayers, bPlayers);
        else return a > b ? ABTeam.TEAM_A : ABTeam.TEAM_B;
    }

    @Override
    public @NotNull Collection<TeamFlag<ABTeam>> getFlags(@Nullable LocalGame game) {
        return FLAG.get(game);
    }
}
