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

import dev.anhcraft.battle.api.arena.game.Mode;
import dev.anhcraft.confighelper.ConfigHelper;
import dev.anhcraft.confighelper.exception.InvalidValueException;

import java.util.Objects;

public class ModeConfigManager extends ConfigManager {
    public ModeConfigManager() {
        super("Mode", "modes.yml");
    }

    @Override
    public void onLoad() {
        getSettings().getKeys(false).forEach(s -> {
            Mode.get(s, m -> {
                try {
                    ConfigHelper.readConfig(Objects.requireNonNull(getSettings().getConfigurationSection(s)), Mode.SCHEMA, m);
                } catch (InvalidValueException e) {
                    e.printStackTrace();
                }
            });
        });
    }

    @Override
    public void onClean() {

    }
}
