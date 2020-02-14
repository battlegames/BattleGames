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

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import dev.anhcraft.battle.BattlePlugin;
import dev.anhcraft.battle.api.BattleApi;
import dev.anhcraft.battle.api.advancement.Advancement;
import dev.anhcraft.battle.api.advancement.PlayerProgression;
import dev.anhcraft.battle.api.advancement.Progression;
import dev.anhcraft.battle.api.stats.natives.ExpStat;
import dev.anhcraft.battle.api.storage.data.PlayerData;
import dev.anhcraft.battle.system.integrations.VaultApi;
import dev.anhcraft.battle.utils.info.InfoHolder;
import dev.anhcraft.battle.utils.info.InfoReplacer;
import dev.anhcraft.jvmkit.utils.PresentPair;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.SortedSet;

public class QueueAdvancementTask implements Runnable {
    private static final Object LOCK = new Object();
    @SuppressWarnings("UnstableApiUsage")
    private Multimap<Player, PresentPair<String, Double>> queue = MultimapBuilder.linkedHashKeys().linkedListValues().build();

    public void put(Player player, String type, double amount){
        synchronized (LOCK) {
            queue.put(player, new PresentPair<>(type, amount));
        }
    }

    @Override
    public void run() {
        BattleApi api = BattleApi.getInstance();
        synchronized (LOCK) {
            Iterator<Player> keys = queue.keys().iterator();
            while (keys.hasNext()){
                Player player = keys.next();
                PlayerData pd = api.getPlayerData(player);
                if(pd == null) continue;
                Collection<PresentPair<String, Double>> pc = queue.get(player);
                for (PresentPair<String, Double> p : pc) {
                    String type = p.getFirst();
                    double amount = p.getSecond();
                    PlayerProgression pp = pd.getProgressionOrCreate(type);
                    Advancement current = null;
                    Advancement next = null;
                    Progression currentLevel = null;
                    if(pp.getActiveAdvancement() == null){
                        // if the player already done the advancement (amount > 0) we don't re-init it.
                        if (pp.getCurrentAmount() > 0) continue;
                        // if the player is first come to this advancement...
                        SortedSet<Advancement> set = api.getAdvancementManager().getAdvancementsFromType(type);
                        // this stat type must have its own advancement
                        if(set.isEmpty()) {
                            continue;
                        } else {
                            Advancement adv = set.first();
                            pp.setActiveAdvancement(adv.getId());
                            pp.setCurrentLevel(0);
                            pp.setTargetAmount((currentLevel = adv.getProgression().first()).getAmount());
                            pp.setCurrentAmount(amount);
                            if(pp.getCurrentAmount() < pp.getTargetAmount()) continue;
                            else {
                                current = adv;
                                next = set.stream().skip(1).findFirst().orElse(null);
                            }
                        }
                    } else {
                        if(amount < pp.getTargetAmount()){
                            pp.setCurrentAmount(amount);
                            continue;
                        }
                        SortedSet<Advancement> set = api.getAdvancementManager().getAdvancementsFromType(type);
                        for (Advancement adv : set){
                            if(adv.getId().equals(pp.getActiveAdvancement())){
                                current = adv;
                            } else if(current != null){
                                next = adv;
                                break;
                            }
                        }
                    }
                    if(current == null){
                        pp.setActiveAdvancement(null);
                        continue;
                    }
                    int currentLv = pp.getCurrentLevel();
                    if(currentLevel == null){
                        Optional<Progression> currLv = current.getProgression().stream().skip(currentLv).findFirst();
                        if(!currLv.isPresent()) continue;
                        currentLevel = currLv.get();
                    }
                    if(amount < currentLevel.getAmount()){
                        pp.setTargetAmount(currentLevel.getAmount());
                        pp.setCurrentAmount(amount);
                        continue;
                    }
                    InfoReplacer currInfo = new InfoHolder("")
                            .inform("advancement", current.getName())
                            .inform("exp", currentLevel.getRewardExp())
                            .inform("money", currentLevel.getRewardMoney())
                            .compile();
                    if(currentLv + 1 >= current.getProgression().size()) {
                        api.getChatManager().sendPlayer(player, "advancement.finished", currInfo);
                        if(next != null) {
                            api.getChatManager().sendPlayer(player, "advancement.new", new InfoHolder("").inform("advancement", next.getName()).compile());
                            if (next.getInheritProgress()) {
                                pp.setCurrentAmount(amount);
                            } else {
                                pp.setCurrentAmount(0);
                            }
                            pp.getFinishedAdvancements().add(current.getId());
                            pp.setActiveAdvancement(next.getId());
                            pp.setCurrentLevel(0);
                            pp.setTargetAmount(next.getProgression().stream().findFirst().orElseThrow(IllegalStateException::new).getAmount());
                        } else {
                            // congratulate! you have finished all advancements of this type!
                            pp.setCurrentAmount(amount);
                            pp.setCurrentLevel(-1);
                            pp.getFinishedAdvancements().add(current.getId());
                            pp.setActiveAdvancement(null);
                            pp.setTargetAmount(0);
                        }
                    } else {
                        api.getChatManager().sendPlayer(player, "advancement.level_up", currInfo);
                        api.getChatManager().sendPlayer(player, "advancement.level_up_overview",
                                new InfoHolder("")
                                .inform("last_lv", currentLv)
                                .inform("current_lv", currentLv + 1)
                                .inform("progress", 100d/current.getProgression().size()*(currentLv+1)).compile());
                        pp.setCurrentAmount(amount);
                        pp.setCurrentLevel(currentLv + 1);
                        pp.setTargetAmount(current.getProgression().stream().skip(currentLv).findFirst().orElseThrow(IllegalStateException::new).getAmount());
                    }
                    // award...
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 4f, 1f);
                    pd.getStats().of(ExpStat.class).increase(player, currentLevel.getRewardExp());
                    double money = currentLevel.getRewardMoney();
                    Bukkit.getScheduler().runTask((BattlePlugin) api, () -> VaultApi.getEconomyApi().depositPlayer(player, money));
                }
                keys.remove();
                break;
            }
        }
    }
}
