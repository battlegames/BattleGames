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

package dev.anhcraft.battle.system.managers.config;

import dev.anhcraft.battle.api.market.Market;
import dev.anhcraft.battle.utils.ConfigHelper;
import dev.anhcraft.battle.utils.ConfigUpdater;
import dev.anhcraft.config.bukkit.NMSVersion;

public class MarketConfigManager extends ConfigManager {
    public MarketConfigManager() {
        super("Market", "market.yml", (NMSVersion.current().compare(NMSVersion.v1_13_R1) >= 0 ? "market.yml" : "market.legacy.yml"));
    }

    @Override
    public void onLoad() {
        ConfigUpdater u = new ConfigUpdater(plugin.getLogger());
        u.getPathRelocating().add(
                new ConfigUpdater.PathRelocating()
                        .oldPath("categories.*.products.*.price.vault")
                        .newPath("categories.#0.products.#1.price")
                        .type(Number.class)
        );
        u.update(getSettings());
        ConfigHelper.load(Market.class, getSettings(), plugin.getMarket());
    }

    @Override
    public void onClean() {

    }
}
