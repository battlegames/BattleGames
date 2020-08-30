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
import dev.anhcraft.battle.api.Kit;
import dev.anhcraft.battle.api.events.KitReceiveEvent;
import dev.anhcraft.battle.api.gui.page.Pagination;
import dev.anhcraft.battle.api.gui.page.SlotChain;
import dev.anhcraft.battle.api.gui.screen.View;
import dev.anhcraft.battle.api.gui.struct.Slot;
import dev.anhcraft.battle.api.storage.data.PlayerData;
import dev.anhcraft.battle.utils.info.InfoHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class KitMenu implements Pagination {
    @Override
    public void supply(@NotNull Player player, @NotNull View view, @NotNull SlotChain chain) {
        BattleApi api = ApiProvider.consume();
        PlayerData pd = api.getPlayerData(player);
        if (pd != null) {
            for (Kit kit : api.listKits()) {
                if (!chain.hasNext()) break;
                if (chain.shouldSkip()) continue;
                Slot slot = chain.next();
                if (kit.getPermission() != null && !player.hasPermission(kit.getPermission())) {
                    slot.setPaginationItem(kit.getNoAccessIcon().duplicate());
                    slot.setExtraClickFunction((vm, report) -> api.getChatManager().sendPlayer(report.getPlayer(), "kit.no_permission"));
                    return;
                }
                long last = pd.getKits().getOrDefault(kit.getId(), 0L);
                if (last != 0) {
                    if (kit.getRenewTime() == -1) {
                        slot.setPaginationItem(kit.getNoAccessIcon().duplicate());
                        slot.setExtraClickFunction((vm, report) -> api.getChatManager().sendPlayer(report.getPlayer(), "kit.one_time_use"));
                        return;
                    }
                    long next = last + kit.getRenewTime() * 50;
                    if (next > System.currentTimeMillis()) {
                        slot.setPaginationItem(kit.getNoAccessIcon().duplicate());
                        slot.setExtraClickFunction((vm, report) -> {
                            String f = api.formatLongFormDate(new Date(next));
                            api.getChatManager().sendPlayer(report.getPlayer(), "kit.unavailable", new InfoHolder("").inform("next_available_date", f).compile());
                        });
                        return;
                    }
                }

                slot.setPaginationItem(kit.getIcon().duplicate());
                slot.setExtraClickFunction((vm, report) -> {
                    KitReceiveEvent event = new KitReceiveEvent(player, kit);
                    Bukkit.getPluginManager().callEvent(event);
                    if (event.isCancelled()) return;

                    kit.givePlayer(report.getPlayer(), pd);
                    pd.getKits().put(kit.getId(), System.currentTimeMillis());
                    api.getGuiManager().openTopGui(report.getPlayer(), "kit_menu");
                });
            }
        }
    }
}
