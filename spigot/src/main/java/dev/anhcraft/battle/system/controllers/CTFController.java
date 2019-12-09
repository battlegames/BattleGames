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
package dev.anhcraft.battle.system.controllers;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import dev.anhcraft.battle.BattlePlugin;
import dev.anhcraft.battle.api.events.game.FlagUpdateEvent;
import dev.anhcraft.battle.api.game.*;
import dev.anhcraft.battle.api.misc.BattleSound;
import dev.anhcraft.battle.utils.LocationUtil;
import dev.anhcraft.battle.utils.PlaceholderUtil;
import dev.anhcraft.battle.utils.info.InfoHolder;
import dev.anhcraft.craftkit.entity.ArmorStand;
import dev.anhcraft.craftkit.entity.TrackedEntity;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CTFController extends TeamDeathmatchController {
    private final Multimap<LocalGame, TeamFlag<ABTeam>> FLAG = LinkedHashMultimap.create();

    public CTFController(BattlePlugin plugin) {
        super(plugin, Mode.CTF);

        String p = getMode().getId()+"_";

        plugin.getPapiExpansion().handlers.put(p+"flags", player -> {
            LocalGame game = plugin.gameManager.getGame(player);
            Collection<TeamFlag<ABTeam>> f = FLAG.get(game);
            return f == null ? null : Integer.toString(f.size());
        });

        plugin.getPapiExpansion().handlers.put(p+"valid_flags", player -> {
            LocalGame game = plugin.gameManager.getGame(player);
            Collection<TeamFlag<ABTeam>> f = FLAG.get(game);
            return f == null ? null : Long.toString(f.stream().filter(TeamFlag::isValid).count());
        });

        plugin.getPapiExpansion().handlers.put(p+"team_all_flags", player -> {
            LocalGame game = plugin.gameManager.getGame(player);
            TeamManager<ABTeam> t = TEAM.get(game);
            if(t == null) return null;
            ABTeam team = t.getTeam(player);
            Collection<TeamFlag<ABTeam>> f = FLAG.get(game);
            return f == null ? null : Long.toString(f.stream().filter(flag -> flag.getTeam() == team).count());
        });

        plugin.getPapiExpansion().handlers.put(p+"team_valid_flags", player -> {
            LocalGame game = plugin.gameManager.getGame(player);
            TeamManager<ABTeam> t = TEAM.get(game);
            if(t == null) return null;
            ABTeam team = t.getTeam(player);
            Collection<TeamFlag<ABTeam>> f = FLAG.get(game);
            return f == null ? null : Long.toString(f.stream().filter(flag -> flag.isValid() && flag.getTeam() == team).count());
        });
    }

    @Override
    public void onJoin(@NotNull Player player, @NotNull LocalGame game) {
        super.onJoin(player, game);

        Collection<TeamFlag<ABTeam>> flags = FLAG.get(game);
        if(flags != null) {
            for(TeamFlag<ABTeam> f : flags){
                f.getArmorStand().addViewer(player);
            }
        }
    }

    @Override
    public void onQuit(@NotNull Player player, @NotNull LocalGame game){
        super.onQuit(player, game);

        Collection<TeamFlag<ABTeam>> flags = FLAG.get(game);
        if(flags != null) {
            for(TeamFlag<ABTeam> f : flags){
                f.getArmorStand().removeViewer(player);
            }
        }
    }

    @Override
    protected void play(LocalGame game) {
        super.play(game);

        plugin.taskHelper.newTask(() -> {
            ConfigurationSection sec = game.getArena().getAttributes().getConfigurationSection("flags");
            if(sec != null){
                Set<String> keys = sec.getKeys(false);
                for(String k : keys){
                    Location loc = LocationUtil.fromString(sec.getString(k+".location"));
                    int mh = sec.getInt(k+".max_health");
                    ArmorStand armorStand = ArmorStand.spawn(loc);
                    armorStand.setVisible(false);
                    armorStand.setNameVisible(true);
                    TrackedEntity<ArmorStand> te = plugin.extension.trackEntity(armorStand);
                    te.setViewDistance(50);
                    te.setViewers(new ArrayList<>(game.getPlayers().keySet()));
                    TeamFlag<ABTeam> flag = new TeamFlag<>(te, mh);
                    flag.getDisplayNames()[0] = sec.getString(k+".display_name.valid");
                    flag.getDisplayNames()[1] = sec.getString(k+".display_name.invalid");
                    flag.getDisplayNames()[2] = sec.getString(k+".display_name.neutral");
                    flag.updateDisplayName(s -> {
                        InfoHolder h = new InfoHolder("flag_");
                        flag.inform(h);
                        return PlaceholderUtil.formatInfo(s, plugin.mapInfo(h));
                    });
                    String startCaptureSound = sec.getString(k+".start_capture_sound");
                    if(startCaptureSound != null) flag.setCaptureStartSound(new BattleSound(startCaptureSound));
                    String stopCaptureSound = sec.getString(k+".stop_capture_sound");
                    if(stopCaptureSound != null) flag.setCaptureStopSound(new BattleSound(stopCaptureSound));
                    FLAG.put(game, flag);
                }
            }
        });
    }

    private void startOccupyFlag(LocalGame game, TeamFlag<ABTeam> flag, Player occupier){
        ABTeam team = TEAM.get(game).getTeam(occupier);
        if(team == null || flag.isCapturing() || (flag.isValid() && team == flag.getTeam())) return;
        flag.setCapturing(true);
        if(flag.getCaptureStartSound() != null) flag.getCaptureStartSound().play(occupier);
        String id = "ctf_flag_occupy_"+occupier.getName();
        int tid = plugin.taskHelper.newTimerTask(() -> {
            if(occupier.getLocation().distance(flag.getArmorStand().getLocation()) >= 1.5){
                stopOccupyFlag(game, flag, occupier);
                return;
            }
            if(flag.getTeam() == null || flag.getTeam() == team){
                int h = flag.getHealth().incrementAndGet();
                if(h == 1) flag.setTeam(team);
                else if(h == flag.getMaxHealth()){
                    flag.setValid(true);
                    stopOccupyFlag(game, flag, occupier);
                }
            }
            else {
                int h = flag.getHealth().decrementAndGet();
                if(h == 0) flag.setTeam(null);
                else if(h == flag.getMaxHealth() - 1) flag.setValid(false);
            }
            flag.updateDisplayName(s -> {
                InfoHolder h = new InfoHolder("flag_");
                flag.inform(h);
                return PlaceholderUtil.formatInfo(s, plugin.mapInfo(h));
            });
            FlagUpdateEvent e = new FlagUpdateEvent(game, occupier, team, flag);
            Bukkit.getPluginManager().callEvent(e);
        }, 0, 20);
        trackTask(game, id, tid);
    }

    private void stopOccupyFlag(LocalGame game, TeamFlag<ABTeam> flag, Player occupier){
        cancelTask(game, "ctf_flag_occupy_"+occupier.getName());
        flag.setCapturing(false);
        if(flag.getCaptureStopSound() != null) flag.getCaptureStopSound().play(occupier);
    }

    @EventHandler
    public void asm(PlayerArmorStandManipulateEvent event){
        if(event.getRightClicked().hasMetadata("abm_ctf_flag"))
            event.setCancelled(true);
    }

    @EventHandler
    public void sneak(PlayerToggleSneakEvent event){
        if(event.getPlayer().getGameMode() == GameMode.SPECTATOR) return;
        LocalGame game = plugin.gameManager.getGame(event.getPlayer());
        if(game != null){
            if(game.getMode() != getMode()) return;
            if(!event.isSneaking() && !hasTask(game, "ctf_flag_occupy_"+event.getPlayer().getName())){
                return;
            }
            Location loc = event.getPlayer().getLocation();
            Collection<TeamFlag<ABTeam>> flags = FLAG.get(game);
            for(TeamFlag<ABTeam> flag : flags){
                if(flag.getArmorStand().getLocation().distanceSquared(loc) >= 3) continue;
                if(event.isSneaking()) {
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
        for(TeamFlag<ABTeam> f : flags){
            if(f.isValid()){
                if(f.getTeam() == ABTeam.TEAM_A) a++;
                else b++;
            }
        }
        if(a == b) return super.handleResult(game, sa, sb, aPlayers, bPlayers);
        else return a > b ? ABTeam.TEAM_A : ABTeam.TEAM_B;
    }
}
