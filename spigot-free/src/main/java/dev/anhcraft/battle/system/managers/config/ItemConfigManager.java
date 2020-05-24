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

import dev.anhcraft.battle.api.inventory.item.ItemType;
import dev.anhcraft.confighelper.ConfigHelper;
import dev.anhcraft.confighelper.exception.InvalidValueException;
import dev.anhcraft.craftkit.abif.PreparedItem;
import org.bukkit.configuration.ConfigurationSection;

public class ItemConfigManager extends ConfigManager {
    public ItemConfigManager() {
        super("items/items.yml");
    }

    @Override
    public void onLoad() {
        for(ItemType type : ItemType.values()){
            String k = type.name().toLowerCase();
            ConfigurationSection sec = getSettings().getConfigurationSection("model_"+k);
            if(sec != null) {
                try {
                    plugin.itemManager.defineModelTemplate(type, ConfigHelper.readConfig(sec, PreparedItem.SCHEMA));
                } catch (InvalidValueException e) {
                    e.printStackTrace();
                }
            }
            sec = getSettings().getConfigurationSection(k);
            if(sec != null) {
                try {
                    plugin.itemManager.defineItemTemplate(type, ConfigHelper.readConfig(sec, PreparedItem.SCHEMA));
                } catch (InvalidValueException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onClean() {

    }
}
