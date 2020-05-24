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

package dev.anhcraft.battle.system.managers;

import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SortedSetMultimap;
import dev.anhcraft.battle.BattleComponent;
import dev.anhcraft.battle.BattlePlugin;
import dev.anhcraft.battle.api.advancement.Advancement;
import dev.anhcraft.battle.api.advancement.AdvancementManager;
import dev.anhcraft.battle.tasks.QueueAdvancementTask;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Comparator;
import java.util.SortedSet;

public class BattleAdvancementManager extends BattleComponent implements AdvancementManager {
    @SuppressWarnings("UnstableApiUsage")
    private final SortedSetMultimap<String, Advancement> advancements = MultimapBuilder.hashKeys().treeSetValues(new Comparator<Advancement>() {
        @Override
        public int compare(Advancement o1, Advancement o2) {
            return Double.compare(o1.getMaxAmount(), o2.getMaxAmount());
        }
    }).build();
    private final QueueAdvancementTask advancementTask = new QueueAdvancementTask();

    public BattleAdvancementManager(BattlePlugin plugin) {
        super(plugin);

        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, advancementTask, 0, 60);
    }

    @Override
    public void registerAdvancement(@NotNull Advancement advancement) {
        advancements.put(advancement.getType(), advancement);
    }

    @Override
    public Collection<Advancement> getAdvancements() {
        return advancements.values();
    }

    @Override
    public @NotNull SortedSet<Advancement> getAdvancementsFromType(@NotNull String type) {
        return advancements.get(type);
    }

    @Override
    public void report(@NotNull Player player, @NotNull String type, double current) {
        advancementTask.put(player, type, current);
    }

    @Deprecated
    public void clean() {
        advancements.clear();
    }
}
