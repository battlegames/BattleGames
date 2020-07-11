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

import dev.anhcraft.battle.BattlePlugin;
import dev.anhcraft.battle.api.arena.game.Game;
import dev.anhcraft.battle.api.arena.game.GamePhase;
import dev.anhcraft.battle.api.arena.game.LocalGame;
import dev.anhcraft.battle.api.arena.mode.IMobRescue;
import dev.anhcraft.battle.api.arena.mode.Mode;
import dev.anhcraft.battle.api.arena.mode.options.MobRescueOptions;
import dev.anhcraft.battle.api.arena.team.MRTeam;
import dev.anhcraft.battle.api.arena.team.TeamManager;
import dev.anhcraft.battle.api.events.WeaponUseEvent;
import dev.anhcraft.battle.api.misc.BattleScoreboard;
import dev.anhcraft.battle.premium.system.MobRescueMatch;
import dev.anhcraft.battle.system.controllers.DeathmatchController;
import dev.anhcraft.battle.system.renderers.scoreboard.PlayerScoreboard;
import dev.anhcraft.battle.utils.CooldownMap;
import dev.anhcraft.battle.utils.EntityUtil;
import dev.anhcraft.battle.utils.info.InfoHolder;
import dev.anhcraft.battle.utils.info.InfoReplacer;
import dev.anhcraft.jvmkit.utils.RandomUtil;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class MobRescueController extends DeathmatchController implements IMobRescue {
    protected final Map<LocalGame, TeamManager<MRTeam>> TEAM = new HashMap<>();
    protected final Map<LocalGame, MobRescueMatch> MATCH = new HashMap<>();

    public MobRescueController(BattlePlugin plugin) {
        this(plugin, Mode.MOB_RESCUE);
    }

    MobRescueController(BattlePlugin plugin, Mode mode) {
        super(plugin, mode);

        String p = mode.getId()+"_";

        plugin.getPapiExpansion().handlers.put(p+"team", (player, pd, game, gp) -> {
            if(game == null) return null;
            TeamManager<MRTeam> t = TEAM.get(game);
            if(t == null)
                return null;
            MRTeam dt = t.getTeam(player);
            if(dt == null)
                return null;
            return dt.getLocalizedName();
        });

        plugin.getPapiExpansion().handlers.put(p+"team_players", (player, pd, game, gp) -> {
            if(game == null) return null;
            TeamManager<MRTeam> t = TEAM.get(game);
            if(t == null)
                return null;
            MRTeam dt = t.getTeam(player);
            if(dt == null)
                return null;
            return Integer.toString(t.countPlayers(dt));
        });

        plugin.getPapiExpansion().handlers.put(p+"team_thief", (player, pd, game, gp) -> MRTeam.THIEF.getLocalizedName());

        plugin.getPapiExpansion().handlers.put(p+"team_thief_players", (player, pd, game, gp) -> {
            if(game == null) return null;
            TeamManager<MRTeam> t = TEAM.get(game);
            return t == null ? null : Integer.toString(t.countPlayers(MRTeam.THIEF));
        });

        plugin.getPapiExpansion().handlers.put(p+"team_farmer", (player, pd, game, gp) -> MRTeam.FARMER.getLocalizedName());

        plugin.getPapiExpansion().handlers.put(p+"team_farmer_players", (player, pd, game, gp) -> {
            if(game == null) return null;
            TeamManager<MRTeam> t = TEAM.get(game);
            return t == null ? null : Integer.toString(t.countPlayers(MRTeam.FARMER));
        });

        plugin.getPapiExpansion().handlers.put(p+"mobs_stolen", (player, pd, game, gp) -> {
            if(game == null) return null;
            MobRescueMatch m = MATCH.get(game);
            return m == null ? null : Integer.toString(m.getStolenMobs());
        });

        plugin.getPapiExpansion().handlers.put(p+"mobs_all", (player, pd, game, gp) -> {
            if(game == null) return null;
            MobRescueMatch m = MATCH.get(game);
            return m == null ? null : Integer.toString(m.getTotalMobs());
        });
    }

    @Override
    public void onInitGame(@NotNull Game game){
        super.onInitGame(game);
        if(game instanceof LocalGame) {
            LocalGame lc = (LocalGame) game;
            if(game.getArena().getModeOptions() instanceof MobRescueOptions) {
                MobRescueOptions options = (MobRescueOptions) game.getArena().getModeOptions();
                for (Location loc : options.getPlaySpawnPoints(MRTeam.THIEF)) {
                    lc.addInvolvedWorld(loc.getWorld());
                }
                for (Location loc : options.getPlaySpawnPoints(MRTeam.FARMER)) {
                    lc.addInvolvedWorld(loc.getWorld());
                }
            }
        }
    }

    @Override
    public void onTick(@NotNull LocalGame game){
        super.onTick(game);
        TeamManager<MRTeam> x = TEAM.get(game);
        if(x != null && x.nextEmptyTeam().isPresent()) {
            game.end();
        }
    }

    @Override
    public void onJoin(@NotNull Player player, @NotNull LocalGame game) {
        broadcast(game, "player_join_broadcast", new InfoHolder("").inform("player", player.getName()).compile());
        int m = Math.max(game.getArena().getModeOptions().getMinPlayers(), 1);
        switch (game.getPhase()){
            case WAITING:{
                respw(game, player, null);
                BattleScoreboard bs = game.getMode().getWaitingScoreboard();
                if(bs.isEnabled()) {
                    plugin.scoreboardRenderer.setScoreboard(new PlayerScoreboard(player, bs.getTitle(), bs.getContent(), bs.getFixedLength()));
                }
                if(m <= game.getPlayerCount()) countdown(game);
                break;
            }
            case PLAYING: {
                TeamManager<MRTeam> teamManager = TEAM.get(game);
                int a = teamManager.countPlayers(MRTeam.THIEF);
                int b = teamManager.countPlayers(MRTeam.FARMER);
                MRTeam t = a <= b ? MRTeam.THIEF : MRTeam.FARMER;
                teamManager.addPlayer(player, t);

                // don't stack these functions with the player count above
                // this one already include the new player...
                List<Player> ta = teamManager.getPlayers(MRTeam.THIEF);
                List<Player> tb = teamManager.getPlayers(MRTeam.FARMER);
                PlayerScoreboard sps = addPlayer(game, player, t);
                if (sps != null) {
                    sps.addTeamPlayers(MRTeam.THIEF.name(), ta);
                    sps.addTeamPlayers(MRTeam.FARMER.name(), tb);
                }

                List<Player> lc = Collections.singletonList(player);

                for(Player p : ta) {
                    if(p.equals(player)) continue;
                    PlayerScoreboard ps = plugin.scoreboardRenderer.getScoreboard(p);
                    if (ps != null) {
                        ps.addTeamPlayers(t.name(), lc);
                    }
                }

                for(Player p : tb) {
                    if(p.equals(player)) continue;
                    PlayerScoreboard ps = plugin.scoreboardRenderer.getScoreboard(p);
                    if (ps != null) {
                        ps.addTeamPlayers(t.name(), lc);
                    }
                }
            }
        }
    }

    @Override
    public void onQuit(@NotNull Player player, @NotNull LocalGame game){
        super.onQuit(player, game);
        TeamManager<MRTeam> teamManager = TEAM.get(game);
        if(teamManager != null) {
            MRTeam abTeam = teamManager.removePlayer(player);
            for(Map.Entry<Player, MRTeam> ent : teamManager.getPlayerTeam()) {
                PlayerScoreboard ps = plugin.scoreboardRenderer.getScoreboard(ent.getKey());
                if (ps != null) {
                    ps.removeTeamPlayer(abTeam.name(), player);
                }
            }
        }
    }

    @Override
    protected void play(LocalGame game) {
        broadcast(game,"game_start_broadcast");

        MobRescueOptions options = (MobRescueOptions) game.getArena().getModeOptions();
        MATCH.put(game, MobRescueMatch.create(options));

        List<Player> x = new ArrayList<>(game.getPlayers().keySet());
        int sz = Math.floorDiv(x.size(), 2);
        List<Player> ta = x.subList(0, sz);
        List<Player> tb = x.subList(sz, x.size());

        TeamManager<MRTeam> teamManager = new TeamManager<>();
        teamManager.addPlayers(ta, MRTeam.THIEF);
        teamManager.addPlayers(tb, MRTeam.FARMER);
        TEAM.put(game, teamManager);

        plugin.extension.getTaskHelper().newTask(() -> {
            game.setPhase(GamePhase.PLAYING);
            ta.forEach(p -> {
                cancelTask(game, "respawn::"+p.getName());
                PlayerScoreboard ps = addPlayer(game, p, MRTeam.THIEF);
                if (ps != null) {
                    ps.addTeamPlayers(MRTeam.THIEF.name(), ta);
                    ps.addTeamPlayers(MRTeam.FARMER.name(), tb);
                }
            });
            tb.forEach(p -> {
                cancelTask(game, "respawn::"+p.getName());
                extraFarmerCountdown(game, tb, options.getExtraCountdownTimeFarmer(), () -> {
                    PlayerScoreboard ps = addPlayer(game, p, MRTeam.FARMER);
                    if (ps != null) {
                        ps.addTeamPlayers(MRTeam.THIEF.name(), ta);
                        ps.addTeamPlayers(MRTeam.FARMER.name(), tb);
                    }
                });
            });
            options.getMobGroups().forEach(mg -> {
                for (int i = 0; i < mg.getAmount(); i++){
                    Location loc = mg.getLocation();
                    Entity ent = loc.getWorld().spawnEntity(loc, mg.getEntityType());
                    ent.setInvulnerable(true);
                    if(mg.isStealable()) {
                        float speed = (float) (mg.getWeight() / options.getWeightSpeedRatio());
                        ent.setMetadata("stealable", new FixedMetadataValue(plugin, speed));
                        ent.setGlowing(true);
                    }
                }
            });
        });
    }

    protected void extraFarmerCountdown(LocalGame game, List<Player> tb, long time, Runnable callback) {
        if(hasTask(game, "extraFarmerCountdown")) return;
        AtomicLong current = new AtomicLong(time/20L);
        trackTask(game, "extraFarmerCountdown", plugin.extension.getTaskHelper().newAsyncTimerTask(() -> {
            InfoReplacer f = new InfoHolder("").inform("current", current.get()).compile();
            for(Player p : tb) {
                sendTitle(p, "extra_farmer_countdown_title", "extra_farmer_countdown_subtitle", f);
            }
            playSound(game, Sound.BLOCK_FENCE_GATE_OPEN);
            if(current.getAndDecrement() == 0) {
                cancelTask(game, "extraFarmerCountdown");
                plugin.extension.getTaskHelper().newTask(callback);
            }
        }, 0, 20));
    }

    @Nullable
    private PlayerScoreboard addPlayer(LocalGame game, Player player, MRTeam dt) {
        PlayerScoreboard ps = null;
        BattleScoreboard bs = game.getMode().getPlayingScoreboard();
        if(bs.isEnabled()) {
            ps = new PlayerScoreboard(player, bs.getTitle(), bs.getContent(), bs.getFixedLength());
            plugin.scoreboardRenderer.setScoreboard(ps);
        }
        respw(game, player, dt);
        return ps;
    }

    @Override
    protected void respw(LocalGame game, Player player) {
        TeamManager<MRTeam> t = TEAM.get(game);
        respw(game, player, t == null ? null : t.getTeam(player));
    }

    private void respw(LocalGame game, Player player, MRTeam team) {
        player.setGameMode(GameMode.SURVIVAL);
        switch (game.getPhase()) {
            case END:
            case WAITING: {
                Location loc = RandomUtil.pickRandom(game.getArena().getModeOptions().getWaitSpawnPoints());
                EntityUtil.teleport(player, loc);
                break;
            }
            case PLAYING: {
                Location loc = RandomUtil.pickRandom(((MobRescueOptions) game.getArena().getModeOptions()).getPlaySpawnPoints(team));
                EntityUtil.teleport(player, loc, ok -> {
                    if(ok){
                        performCooldownMap(game, "spawn_protection",
                                cooldownMap -> cooldownMap.resetTime(player),
                                () -> new CooldownMap(player));
                        performCooldownMap(game, "item_selection",
                                cooldownMap -> cooldownMap.resetTime(player),
                                () -> new CooldownMap(player));
                    }
                });
            }
        }
    }

    @Override
    public void onDeath(@NotNull PlayerDeathEvent event, @NotNull LocalGame game){
        if(!event.getEntity().getPassengers().isEmpty()) {
            Entity ent = event.getEntity().getPassengers().get(0);
            if (ent instanceof LivingEntity && ent.hasMetadata("stealable")) {
                event.getEntity().removePassenger(ent);
            }
        }
        super.onDeath(event, game);
    }

    @Override
    public void onUseWeapon(@NotNull WeaponUseEvent event, @NotNull LocalGame game) {
        if(event.getReport().getEntity() instanceof Player){
            Player target = (Player) event.getReport().getEntity();
            TeamManager<MRTeam> teamManager = TEAM.get(game);
            if(teamManager.getTeam(event.getReport().getDamager()) == teamManager.getTeam(target)) {
                event.setCancelled(true);
            } else {
                performCooldownMap(game, "spawn_protection",
                        cooldownMap -> {
                            long t = game.getArena().getModeOptions().getSpawnProtectionTime();
                            if (!cooldownMap.isPassed(target, t)) {
                                event.setCancelled(true);
                            }
                        });
            }
        }
    }

    @EventHandler
    public void interactMob(PlayerInteractEntityEvent event){
        if (event.getRightClicked() instanceof LivingEntity) {
            LivingEntity ent = (LivingEntity) event.getRightClicked();
            if(ent.hasMetadata("stealable")) {
                float sr = ent.getMetadata("stealable").get(0).asFloat();
                Player p = event.getPlayer();
                if(p.getPassengers().isEmpty()) {
                    p.addPassenger(ent);
                    p.playSound(p.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 0f);
                    p.setWalkSpeed(Math.max(p.getWalkSpeed() - sr, 0));
                    p.setFlySpeed(Math.max(p.getFlySpeed() - sr, 0));
                }
            }
        }
    }

    @EventHandler
    public void sneak(PlayerToggleSneakEvent event){
        if(event.isSneaking()) {
            Player p = event.getPlayer();
            if(p.getPassengers().isEmpty()) return;
            Entity ent = p.getPassengers().get(0);
            if(!ent.hasMetadata("stealable")) return;
            if(ent instanceof LivingEntity) {
                LocalGame game = plugin.arenaManager.getGame(p);
                if(game == null) return;
                if (game.getMode() != getMode()) return;
                MobRescueMatch match = MATCH.get(game);
                float sr = ent.getMetadata("stealable").get(0).asFloat();
                p.removePassenger(ent);
                p.setWalkSpeed(p.getWalkSpeed() + sr);
                p.setFlySpeed(p.getFlySpeed() + sr);
                if(match.getGatheringRegion().contains(p.getLocation())) {
                    plugin.extension.getTaskHelper().newTask(ent::remove);
                    Integer i = match.getMobCount().get(ent.getType());
                    if(i != null && i > 0) {
                        match.setStolenMobs(match.getStolenMobs() + 1);
                        match.getMobCount().put(ent.getType(), i - 1);
                        MobRescueOptions opt = (MobRescueOptions) game.getArena().getModeOptions();
                        double reward = opt.getObjectives().get(ent.getType()).getRewardCoins();
                        Objects.requireNonNull(game.getPlayer(p)).getIgBalance().addAndGet(reward);
                        if(match.getStolenMobs() == match.getTotalMobs()) {
                            game.end();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onEnd(@NotNull LocalGame game) {
        cancelAllTasks(game);

        TeamManager<MRTeam> teamManager = TEAM.remove(game);
        MobRescueMatch match = MATCH.remove(game);

        MRTeam winner = match.getStolenMobs() >= match.getTotalMobs() ? MRTeam.THIEF : MRTeam.FARMER;
        teamManager.getPlayers(winner).forEach(player -> Objects.requireNonNull(game.getPlayer(player)).setWinner(true));
        teamManager.reset();

        plugin.arenaManager.handleEnd(game);
    }

    @Override
    public @Nullable TeamManager<MRTeam> getTeamManager(@Nullable LocalGame game) {
        return TEAM.get(game);
    }
}
