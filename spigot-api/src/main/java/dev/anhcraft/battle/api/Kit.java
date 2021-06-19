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
package dev.anhcraft.battle.api;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import dev.anhcraft.battle.api.inventory.Backpack;
import dev.anhcraft.battle.api.inventory.item.ItemType;
import dev.anhcraft.battle.api.storage.data.PlayerData;
import dev.anhcraft.battle.impl.Informative;
import dev.anhcraft.battle.utils.State;
import dev.anhcraft.battle.utils.info.InfoHolder;
import dev.anhcraft.config.annotations.*;
import dev.anhcraft.craftkit.abif.PreparedItem;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldMayBeFinal")
@Configurable
public class Kit implements Informative {
    private static final PreparedItem DEF_NO_ACCESS = new PreparedItem();

    static {
        DEF_NO_ACCESS.material(Material.BARRIER);
    }

    private final String id;

    @Setting
    @Description("The kit's icon (when players can get it)")
    @Validation(notNull = true)
    private PreparedItem icon;

    @Setting
    @Path("no_access_icon")
    @Description("The icon to be showed when players can't access the kit")
    @Validation(notNull = true, silent = true)
    private PreparedItem noAccessIcon = DEF_NO_ACCESS.duplicate();

    @Setting
    @Description("The permission that players must have to get the kit")
    private String permission;

    @Setting
    @Path("renew_time")
    @Description("The delay time that players have to wait before get the kit again")
    private int renewTime;

    @Setting
    @Path("items.vanilla")
    @Description("All vanilla items in this kit")
    @Validation(notNull = true, silent = true)
    @Example({
            "items:",
            "  vanilla:",
            "    '1':",
            "      material: bread",
            "      amount: 16"
    })
    private PreparedItem[] vanillaItems = new PreparedItem[0];

    @Setting
    @Path("items.battle")
    @Description({
            "All Battle items in this kit",
            "Example:",
            "<code>gun:",
            "- ak_47",
            "grenade:",
            "- grenade2",
            "ammo:",
            "- 7_62mm",
            "- _50_ae</code>"
    })
    @Validation(notNull = true, silent = true)
    @Example({
            "items:",
            "  battle:",
            "    gun: # gun, ammo, magazine, scope, grenade",
            "    - ak_47",
            "    - desert_eagle"
    })
    private Multimap<ItemType, String> battleItems = HashMultimap.create();

    @Setting
    @Description("The boosters to be given")
    @Validation(notNull = true, silent = true)
    private List<String> boosters = new ArrayList<>();

    @Setting
    @Path("first_join")
    @Description("Players receive the kit automatically on their first joins")
    private boolean firstJoin;

    public Kit(@NotNull String id) {
        Validate.notNull(id, "Id must be non-null");
        this.id = id;
    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public PreparedItem getIcon() {
        return icon;
    }

    @NotNull
    public PreparedItem getNoAccessIcon() {
        return noAccessIcon;
    }

    @Nullable
    public String getPermission() {
        return permission;
    }

    public int getRenewTime() {
        return renewTime;
    }

    @NotNull
    public Multimap<ItemType, String> getBattleItems() {
        return battleItems;
    }

    @NotNull
    public PreparedItem[] getVanillaItems() {
        return vanillaItems;
    }

    public boolean isFirstJoin() {
        return firstJoin;
    }

    @NotNull
    public List<String> getBoosters() {
        return boosters;
    }

    public void givePlayer(@NotNull Player player, @NotNull PlayerData playerData) {
        Location loc = player.getLocation();
        for (PreparedItem pi : vanillaItems) {
            int in = player.getInventory().firstEmpty();
            if (in == -1) {
                player.getWorld().dropItemNaturally(loc, pi.build());
            } else {
                player.getInventory().setItem(in, pi.build());
            }
        }
        battleItems.forEach((type, x) -> {
            Backpack.Compartment is = playerData.getBackpack().getStorage(type);
            is.put(x);
        });
        boosters.forEach(s -> playerData.getBoosters().putIfAbsent(s, System.currentTimeMillis()));
    }

    @Override
    public void inform(@NotNull InfoHolder holder) {
        holder.inform("id", id)
                .inform("renew_time", renewTime)
                .inform("vanilla_items", vanillaItems.length)
                .inform("battle_items", battleItems.size())
                .inform("boosters", boosters.size())
                .inform("first_join", State.TRUE.inCaseOf(firstJoin));
    }
}
