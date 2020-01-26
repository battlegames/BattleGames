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

package dev.anhcraft.battle.system;

import dev.anhcraft.battle.BattleComponent;
import dev.anhcraft.battle.BattlePlugin;

public class PremiumConnector extends BattleComponent {
    private IPremiumModule premiumModule;

    public PremiumConnector(BattlePlugin plugin) {
        super(plugin);

        Class<?> c = null;
        try {
            c = Class.forName("dev.anhcraft.battle.premium.PremiumModule");
        } catch (ClassNotFoundException ignored) {
        }

        if(c != null){
            try {
                premiumModule = (IPremiumModule) c.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isSuccess() {
        return premiumModule != null;
    }

    public void onIntegrate() {
        if(premiumModule != null) {
            premiumModule.onIntegrate(plugin);
        }
    }

    public void onInitSystem() {
        if(premiumModule != null) {
            premiumModule.onInitSystem(plugin);
        }
    }

    public void onInitConfig() {
        if(premiumModule != null) {
            premiumModule.onInitConfig(plugin);
        }
    }

    public void onRegisterEvents() {
        if(premiumModule != null) {
            premiumModule.onRegisterEvents(plugin);
        }
    }

    public void onRegisterTasks() {
        if(premiumModule != null) {
            premiumModule.onRegisterTasks(plugin);
        }
    }

    public void onDisable() {
        if(premiumModule != null) {
            premiumModule.onDisable(plugin);
        }
    }
}
