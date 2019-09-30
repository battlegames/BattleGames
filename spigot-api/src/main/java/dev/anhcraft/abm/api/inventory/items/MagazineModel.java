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
package dev.anhcraft.abm.api.inventory.items;

import dev.anhcraft.abm.api.ApiProvider;
import dev.anhcraft.abm.api.misc.Skin;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MagazineModel extends BattleItemModel implements Attachable {
    private Map<AmmoModel, Integer> ammunition = new HashMap<>();
    private Skin skin;

    public MagazineModel(@NotNull String id, @NotNull ConfigurationSection conf) {
        super(id, conf);

        skin = new Skin(conf.getConfigurationSection("skin"));

        ConfigurationSection ams = conf.getConfigurationSection("ammo");
        if(ams != null){
            for(String a : ams.getKeys(false)) {
                AmmoModel am = ApiProvider.consume().getAmmoModel(a);
                if(am != null) ammunition.put(am, ams.getInt(a));
            }
        }
        ammunition = Collections.unmodifiableMap(ammunition);
    }

    @Override
    public @NotNull ItemType getItemType() {
        return ItemType.MAGAZINE;
    }

    @NotNull
    public Skin getSkin() {
        return skin;
    }

    @NotNull
    public Map<AmmoModel, Integer> getAmmunition() {
        return ammunition;
    }

    @Override
    public ItemType[] getHolderTypes() {
        return new ItemType[]{
                ItemType.GUN
        };
    }
}
