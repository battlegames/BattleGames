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
import dev.anhcraft.abm.api.misc.BattleBar;
import dev.anhcraft.abm.api.misc.ItemSkin;
import dev.anhcraft.abm.api.misc.BattleSound;
import dev.anhcraft.abm.api.misc.info.InfoHolder;
import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.annotation.*;
import dev.anhcraft.confighelper.impl.TwoWayMiddleware;
import dev.anhcraft.jvmkit.lang.enumeration.RegEx;
import dev.anhcraft.jvmkit.utils.Pair;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Schema
public class GunModel extends WeaponModel implements TwoWayMiddleware {
    private static final BattleSound DEF_SHOOT_SOUND = new BattleSound("$entity_arrow_shoot");
    public static final ConfigSchema<GunModel> SCHEMA = ConfigSchema.of(GunModel.class);

    @Key("skin.primary")
    @Explanation("Set the primary skin")
    @Validation(notNull = true)
    private ItemSkin primarySkin;

    @Key("skin.secondary")
    @Explanation("Set the primary skin")
    @IgnoreValue(ifNull = true)
    private ItemSkin secondarySkin = new ItemSkin();

    @Key("weight")
    @Explanation({
            "Set the gun's weight",
            "This value reduces the speed while someone is holding the gun"
    })
    private double weight;

    @Key("magazine.default")
    @Explanation("The default magazine")
    @Validation(notNull = true)
    private MagazineModel defaultMagazine;

    @Key("magazine.max_capacity")
    @Explanation({
            "Set the maximum magazine's capacity",
            "This option has no effect with the default magazine"
    })
    private int magazineMaxCapacity;

    @Key("scope.default")
    @Explanation("The default scope")
    private ScopeModel defaultScope;

    @Key("inventory_slot")
    @Explanation({
            "The slot where the grenade is put into",
            "Only supported by a few game modes"
    })
    private int inventorySlot;

    @Key("sounds.on_shoot")
    @Explanation("Set the sound that is played when shooting")
    @IgnoreValue(ifNull = true)
    private BattleSound shootSound = DEF_SHOOT_SOUND;

    @Key("sounds.on_start_reloading")
    @Explanation("Set the sound that is played when starting to reload ammo")
    private BattleSound reloadStartSound;

    @Key("sounds.on_end_reloading")
    @Explanation("Set the sound that is played when finished reloading ammo")
    private BattleSound reloadEndSound;

    private String reloadTimeFormula;

    @Key("reload_time_formula")
    @Explanation("Set the formula used for calculating the reloading time")
    @Validation(notNull = true)
    private Expression reloadTimeCalculator;

    @Key("bossbar.on_reload")
    @Explanation("Set the boss bar used during the reloading time")
    @Validation(notNull = true)
    private BattleBar reloadBar;

    @Key("spray_pattern")
    @Explanation({
            "Set the spray pattern",
            "With two numbers represent two offsets on the X axis and Y axis",
            "<a href=https://anhcraft.dev/tools/battle/spray.html>Spray pattern generator tool</a>"
    })
    @IgnoreValue(ifNull = true)
    private List<Pair<Double, Double>> sprayPattern = new ArrayList<>();

    public GunModel(@NotNull String id) {
        super(id);
    }

    @Override
    public @NotNull ItemType getItemType() {
        return ItemType.GUN;
    }

    @NotNull
    public ItemSkin getPrimarySkin() {
        return primarySkin;
    }

    @NotNull
    public ItemSkin getSecondarySkin() {
        return secondarySkin;
    }

    public double getWeight() {
        return weight;
    }

    @NotNull
    public MagazineModel getDefaultMagazine() {
        return defaultMagazine;
    }

    public int getMagazineMaxCapacity() {
        return magazineMaxCapacity;
    }

    @Override
    public void inform(@NotNull InfoHolder holder){
        super.inform(holder);
        holder.inform("weight", weight)
                .inform("max_magazine_capacity", getMagazineMaxCapacity())
                .link(defaultMagazine.collectInfo("default_"));
    }

    public int getInventorySlot() {
        return inventorySlot;
    }

    @NotNull
    public BattleSound getShootSound() {
        return shootSound;
    }

    @NotNull
    public BattleBar getReloadBar() {
        return reloadBar;
    }

    @NotNull
    public Expression getReloadTimeCalculator() {
        return reloadTimeCalculator;
    }

    @Nullable
    public BattleSound getReloadStartSound() {
        return reloadStartSound;
    }

    @Nullable
    public BattleSound getReloadEndSound() {
        return reloadEndSound;
    }

    @Nullable
    public ScopeModel getDefaultScope() {
        return defaultScope;
    }

    @NotNull
    public List<Pair<Double, Double>> getSprayPattern() {
        return sprayPattern;
    }

    @Override
    public @Nullable Object conf2schema(ConfigSchema.Entry entry, @Nullable Object o) {
        if(o != null){
            if(entry.getKey().startsWith("sounds.")) {
                return new BattleSound((String) o);
            }
            switch (entry.getKey()){
                case "magazine.default": {
                    return ApiProvider.consume().getMagazineModel((String) o);
                }
                case "scope.default": {
                    return ApiProvider.consume().getScopeModel((String) o);
                }
                case "reload_time_formula": {
                    reloadTimeFormula = (String) o;
                    return new ExpressionBuilder(reloadTimeFormula).variables("a", "b").build();
                }
                case "spray_pattern": {
                    List<Pair<Double, Double>> sp = new ArrayList<>();
                    List<?> list = (List<?>) o;
                    for(Object object : list){
                        String[] args = String.valueOf(object).split(" ");
                        double x = 0, y = 0;
                        if(args.length >= 1){
                            if(RegEx.DECIMAL.valid(args[0]))
                                x = Double.parseDouble(args[0]);
                            else
                                Bukkit.getLogger().warning(String.format("Value X `%s` of spray pattern for gun `%s` is invalid.", args[0], getId()));
                        }
                        if(args.length >= 2){
                            if(RegEx.DECIMAL.valid(args[1]))
                                y = Double.parseDouble(args[1]);
                            else
                                Bukkit.getLogger().warning(String.format("Value Y `%s` of spray pattern for gun `%s` is invalid.", args[1], getId()));
                        }
                        sp.add(new Pair<>(x, y));
                    }
                    return sp;
                }
            }
        }
        return o;
    }

    @Override
    public @Nullable Object schema2conf(ConfigSchema.Entry entry, @Nullable Object o) {
        if(o != null){
            if(entry.getKey().startsWith("sounds.")) {
                return o.toString();
            }
            switch (entry.getKey()){
                case "scope.default":
                case "magazine.default": {
                    return ((BattleItemModel) o).getId();
                }
                case "reload_time_formula": {
                    return reloadTimeFormula;
                }
                case "spray_pattern": {
                    List<Pair<Double, Double>> sp = (List<Pair<Double, Double>>) o;
                    List<String> list = new ArrayList<>();
                    for(Pair<Double, Double> p : sp){
                        list.add(p.getFirst()+" "+p.getSecond());
                    }
                    return list;
                }
            }
        }
        return o;
    }
}
