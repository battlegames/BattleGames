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
import dev.anhcraft.abm.api.misc.CustomBossBar;
import dev.anhcraft.abm.api.misc.Skin;
import dev.anhcraft.abm.api.misc.SoundRecord;
import dev.anhcraft.abm.api.misc.info.InfoHolder;
import dev.anhcraft.abm.utils.EnumUtil;
import dev.anhcraft.jvmkit.lang.enumeration.RegEx;
import dev.anhcraft.jvmkit.utils.Pair;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GunModel extends WeaponModel {
    private Skin primarySkin;
    private Skin secondarySkin;
    private double weight;
    private MagazineModel defaultMagazine;
    private ScopeModel defaultScope;
    private int magazineMaxCapacity;
    private int inventorySlot;
    private SoundRecord shootSound;
    private SoundRecord reloadStartSound;
    private SoundRecord reloadEndSound;
    private Expression reloadTimeCalculator;
    private CustomBossBar reloadBar;
    private List<Pair<Double, Double>> sprayPattern = new ArrayList<>();

    public GunModel(@NotNull String id, @NotNull ConfigurationSection conf) {
        super(id, conf);

        String primaryMaterial = conf.getString("skin.primary.material");
        primarySkin = new Skin(primaryMaterial == null ? null : EnumUtil.getEnum(Material.values(), primaryMaterial), conf.getInt("skin.primary.damage"));
        String secondaryMaterial = conf.getString("skin.secondary.material");
        secondarySkin = new Skin(secondaryMaterial == null ? null : EnumUtil.getEnum(Material.values(), secondaryMaterial), conf.getInt("skin.secondary.damage"));

        weight = conf.getDouble("weight");
        magazineMaxCapacity = conf.getInt("magazine.max_capacity");

        String defaultMag = conf.getString("magazine.default");
        if(defaultMag == null) throw new NullPointerException("Default magazine must be specified");
        defaultMagazine = ApiProvider.consume().getMagazineModel(defaultMag);
        if(defaultMagazine == null) throw new IllegalStateException("Default magazine not found!");

        inventorySlot = conf.getInt("inventory_slot");
        String ss = conf.getString("sounds.on_shoot");
        shootSound = new SoundRecord(ss == null ? "$entity_arrow_shoot" : ss);
        String rss = conf.getString("sounds.on_start_reloading");
        if(rss != null) reloadStartSound = new SoundRecord(rss);
        String res = conf.getString("sounds.on_end_reloading");
        if(res != null) reloadEndSound = new SoundRecord(res);

        reloadBar = new CustomBossBar(true, null, BarColor.GREEN, BarStyle.SOLID);
        ConfigurationSection rbs = conf.getConfigurationSection("bossbar.on_reload");
        if(rbs != null){
            reloadBar.setPrimarySlot(rbs.getBoolean("primary", true));
            reloadBar.setTitle(rbs.getString("title"));
            String barColor = rbs.getString("color");
            if(barColor != null) reloadBar.setColor(EnumUtil.getEnum(BarColor.values(), barColor));
            String barStyle = rbs.getString("style");
            if(barStyle != null) reloadBar.setStyle(EnumUtil.getEnum(BarStyle.values(), barStyle));
        }

        String rtf = conf.getString("reload_time_formula");
        if(rtf == null) throw new NullPointerException("Reloading time formula must be specified");
        reloadTimeCalculator = new ExpressionBuilder(rtf).variables("a", "b").build();

        String defaultScp = conf.getString("scope.default");
        if(defaultScp != null) {
            defaultScope = ApiProvider.consume().getScopeModel(defaultScp);
        }

        conf.getStringList("spray_pattern").forEach(s -> {
            String[] args = s.trim().split(" ");
            double x = 0, y = 0;
            if(args.length >= 1){
                if(RegEx.DECIMAL.valid(args[0])) x = Double.parseDouble(args[0]);
                else Bukkit.getLogger().warning(String.format("Value X `%s` of spray pattern for gun `%s` is invalid.", args[0], id));
            }
            if(args.length >= 2){
                if(RegEx.DECIMAL.valid(args[1])) y = Double.parseDouble(args[1]);
                else Bukkit.getLogger().warning(String.format("Value Y `%s` of spray pattern for gun `%s` is invalid.", args[1], id));
            }
            sprayPattern.add(new Pair<>(x, y));
        });
        sprayPattern = Collections.unmodifiableList(sprayPattern);
    }

    @Override
    public @NotNull ItemType getItemType() {
        return ItemType.GUN;
    }

    @NotNull
    public Skin getPrimarySkin() {
        return primarySkin;
    }

    @NotNull
    public Skin getSecondarySkin() {
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
    public SoundRecord getShootSound() {
        return shootSound;
    }

    @NotNull
    public CustomBossBar getReloadBar() {
        return reloadBar;
    }

    @NotNull
    public Expression getReloadTimeCalculator() {
        return reloadTimeCalculator;
    }

    @Nullable
    public SoundRecord getReloadStartSound() {
        return reloadStartSound;
    }

    @Nullable
    public SoundRecord getReloadEndSound() {
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
}
