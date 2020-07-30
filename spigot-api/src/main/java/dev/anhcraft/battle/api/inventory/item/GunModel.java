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
import dev.anhcraft.battle.api.BattleSound;
import dev.anhcraft.battle.api.inventory.ItemSkin;
import dev.anhcraft.battle.api.misc.BattleBar;
import dev.anhcraft.battle.utils.info.InfoHolder;
import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.annotation.*;
import dev.anhcraft.jvmkit.lang.enumeration.RegEx;
import dev.anhcraft.jvmkit.utils.Pair;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldMayBeFinal")
@Schema
public class GunModel extends WeaponModel {
    private static final BattleSound DEF_SHOOT_SOUND = new BattleSound("$entity_arrow_shoot");
    public static final ConfigSchema<GunModel> SCHEMA = ConfigSchema.of(GunModel.class);

    @Key("skin.primary")
    @Explanation("Set the primary skin")
    @Validation(notNull = true)
    private ItemSkin primarySkin;

    @Key("skin.secondary")
    @Explanation("Set the primary skin")
    @IgnoreValue(ifNull = true)
    private ItemSkin secondarySkin = ItemSkin.EMPTY;

    @Key("weight")
    @Explanation({
            "Set the gun's weight",
            "This value reduces the speed while someone is holding the gun"
    })
    private double weight;

    @Key("muzzle_velocity")
    @Explanation("The initial velocity of a bullet when it is shot out of the gun")
    private double muzzleVelocity = 70;

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
    @Example({
            "spray_pattern:",
            "- 1.55 0.9600006103515625",
            "- 2.05 1.7600006103515624",
            "- 1.05 2.8600006103515625",
            "- -0.65 3.3600006103515625",
            "- -2.35 4.160000610351562",
            "- -3.95 4.960000610351562",
            "- -5.15 6.3600006103515625",
            "- -5.25 7.560000610351563",
            "- -4.15 9.360000610351562",
            "- -2.45 10.660000610351563",
            "- 0.25 12.160000610351563",
            "- 2.05 12.860000610351562",
            "- 3.95 14.160000610351563",
            "- 4.45 15.460000610351562",
            "- 4.55 17.660000610351563",
            "- 4.25 18.560000610351562",
            "- 2.85 19.660000610351563",
            "- 0.65 21.26000061035156",
            "- -0.95 21.660000610351563",
            "- -4.25 22.560000610351562",
            "- -5.65 22.860000610351562"
    })
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

    public double getMuzzleVelocity() {
        return muzzleVelocity;
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
                .inform("muzzle_velocity", muzzleVelocity)
                .inform("max_magazine_capacity", getMagazineMaxCapacity())
                .link(defaultMagazine.collectInfo("default_"));
    }

    @NotNull
    public BattleSound getShootSound() {
        return shootSound;
    }

    @NotNull
    public BattleBar getReloadBar() {
        return reloadBar;
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
    protected @Nullable Object conf2schema(@Nullable Object o, ConfigSchema.Entry entry) {
        if(o != null){
            switch (entry.getKey()){
                case "magazine.default": {
                    return ApiProvider.consume().getMagazineModel((String) o);
                }
                case "scope.default": {
                    return ApiProvider.consume().getScopeModel((String) o);
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
    protected @Nullable Object schema2conf(@Nullable Object o, ConfigSchema.Entry entry) {
        if(o != null){
            switch (entry.getKey()){
                case "scope.default":
                case "magazine.default": {
                    return ((BattleItemModel) o).getId();
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
