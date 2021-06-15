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

package dev.anhcraft.battle.premium.config.managers;

import dev.anhcraft.battle.premium.config.ItemSettings;
import dev.anhcraft.battle.system.managers.config.ConfigManager;
import dev.anhcraft.confighelper.ConfigHelper;
import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.exception.InvalidValueException;
import org.jetbrains.annotations.NotNull;

public class ItemConfigManagerX extends ConfigManager {
    private ItemSettings itemSettings;

    public ItemConfigManagerX() {
        super("Premium/Items", "premium/items.yml");
    }

    @NotNull
    public ItemSettings getItemSettings() {
        return itemSettings;
    }

    @Override
    protected void onLoad() {
        try {
            itemSettings = ConfigHelper.readConfig(getSettings(), ConfigSchema.of(ItemSettings.class));
        } catch (InvalidValueException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onClean() {

    }
}
