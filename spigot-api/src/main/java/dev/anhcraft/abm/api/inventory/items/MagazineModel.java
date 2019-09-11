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
import dev.anhcraft.abm.utils.EnumUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class MagazineModel extends BattleItemModel implements Attachable {
    private final Map<AmmoModel, Integer> ammunition = new HashMap<>();
    private Skin skin;

    public MagazineModel(@NotNull String id, @NotNull ConfigurationSection conf) {
        super(id, conf);

        String material = conf.getString("skin.material");
        skin = new Skin(material == null ? null : EnumUtil.getEnum(Material.values(), material), conf.getInt("skin.damage"));

        ConfigurationSection am = conf.getConfigurationSection("ammo");
        if(am != null){
            for(String a : am.getKeys(false)) ApiProvider.consume().getAmmoModel(a).ifPresent(ammo -> ammunition.put(ammo, am.getInt(a)));
        }
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
