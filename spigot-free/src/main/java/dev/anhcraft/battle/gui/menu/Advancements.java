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

package dev.anhcraft.battle.gui.menu;

import dev.anhcraft.battle.ApiProvider;
import dev.anhcraft.battle.api.BattleApi;
import dev.anhcraft.battle.api.advancement.Advancement;
import dev.anhcraft.battle.api.advancement.PlayerProgression;
import dev.anhcraft.battle.api.advancement.Progression;
import dev.anhcraft.battle.api.gui.page.Pagination;
import dev.anhcraft.battle.api.gui.page.SlotChain;
import dev.anhcraft.battle.api.gui.screen.View;
import dev.anhcraft.battle.api.gui.struct.Slot;
import dev.anhcraft.battle.api.storage.data.PlayerData;
import dev.anhcraft.battle.gui.GDataRegistry;
import dev.anhcraft.battle.utils.info.InfoHolder;
import dev.anhcraft.battle.utils.info.InfoReplacer;
import dev.anhcraft.craftkit.abif.PreparedItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Advancements implements Pagination {
    @Override
    public void supply(@NotNull Player player, @NotNull View view, @NotNull SlotChain chain) {
        String stat = (String) view.getWindow().getBackend().remove(GDataRegistry.STAT);
        if(stat == null) return;
        BattleApi api = ApiProvider.consume();
        PlayerData pd = api.getPlayerData(player);
        if(pd == null) return;
        PlayerProgression pp = pd.getProgression(stat);
        for (Advancement adv : api.getAdvancementManager().getAdvancementsFromType(stat)){
            if(!chain.hasNext()) break;
            if(chain.shouldSkip()) continue;
            Slot slot = chain.next();
            PreparedItem icon = new PreparedItem();
            icon.flags().add(ItemFlag.HIDE_ATTRIBUTES);
            icon.material(adv.getIcon());
            icon.name(adv.getName());
            if(adv.getDescription() != null) {
                icon.lore().addAll(adv.getDescription());
            }
            if(pp != null){
                if(pp.getCurrentLevel() >= 0 && adv.getId().equals(pp.getActiveAdvancement())){
                    Progression p = adv.getProgression().stream().skip(pp.getCurrentLevel()).findFirst().orElseThrow(IllegalStateException::new);
                    InfoReplacer infoReplacer = new InfoHolder("")
                            .inform("amount", pp.getCurrentAmount())
                            .inform("max_amount", p.getAmount())
                            .inform("amount_progress", 100d/p.getAmount()*pp.getCurrentAmount())
                            .inform("lv", pp.getCurrentLevel())
                            .inform("max_lv", adv.getProgression().size())
                            .inform("lv_progress", 100d/adv.getProgression().size()*pp.getCurrentLevel())
                            .inform("reward_exp", p.getRewardExp())
                            .inform("reward_money", p.getRewardMoney())
                            .compile();
                    List<String> list = api.getLocalizedMessages("gui.advancements.status.in_progress");
                    if(list != null) {
                        for(String s : list){
                            icon.lore().add(infoReplacer.replace(s));
                        }
                    }
                } else if(pp.getFinishedAdvancements().contains(adv.getId())){
                    List<String> list = api.getLocalizedMessages("gui.advancements.status.finished");
                    if(list != null) {
                        icon.lore().addAll(list);
                    }
                } else {
                    List<String> list = api.getLocalizedMessages("gui.advancements.status.locked");
                    if(list != null) {
                        icon.lore().addAll(list);
                    }
                }
            } else {
                List<String> list = api.getLocalizedMessages("gui.advancements.status.locked");
                if(list != null) {
                    icon.lore().addAll(list);
                }
            }
            slot.setPaginationItem(icon);
        }
    }
}
