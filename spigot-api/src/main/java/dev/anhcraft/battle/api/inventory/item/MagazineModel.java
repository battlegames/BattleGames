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
package dev.anhcraft.battle.api.inventory.item;

import dev.anhcraft.battle.ApiProvider;
import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.annotation.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@Schema
public class MagazineModel extends SingleSkinItem implements Attachable {
    public static final ConfigSchema<MagazineModel> SCHEMA = ConfigSchema.of(MagazineModel.class);

    @Key("ammo")
    @Explanation("All ammo types can be stored in this magazine")
    @IgnoreValue(ifNull = true)
    @Example({
            "ammo:",
            "  7_62mm: 30 # Can hold up to x30 7.62 mm ammo"
    })
    private Map<AmmoModel, Integer> ammunition = new HashMap<>();

    public MagazineModel(@NotNull String id) {
        super(id);
    }

    @Override
    public @NotNull ItemType getItemType() {
        return ItemType.MAGAZINE;
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

    @Override
    protected @Nullable Object conf2schema(@Nullable Object o, ConfigSchema.Entry entry) {
        if(o != null && entry.getKey().equals("ammo")){
            ConfigurationSection cs = (ConfigurationSection) o;
            Map<AmmoModel, Integer> ammo = new HashMap<>();
            for(String a : cs.getKeys(false)){
                AmmoModel am = ApiProvider.consume().getAmmoModel(a);
                if(am != null) {
                    ammo.put(am, cs.getInt(a));
                }
            }
            return ammo;
        }
        return o;
    }

    @Override
    protected @Nullable Object schema2conf(@Nullable Object o, ConfigSchema.Entry entry) {
        if(o != null && entry.getKey().equals("ammo")){
            ConfigurationSection parent = new YamlConfiguration();
            Map<AmmoModel, Integer> map = (Map<AmmoModel, Integer>) o;
            for(Map.Entry<AmmoModel, Integer> x : map.entrySet()){
                parent.set(x.getKey().getId(), x.getValue());
            }
            return parent;
        }
        return o;
    }
}
