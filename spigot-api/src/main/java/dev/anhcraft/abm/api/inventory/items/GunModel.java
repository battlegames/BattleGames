package dev.anhcraft.abm.api.inventory.items;

import dev.anhcraft.abm.api.APIProvider;
import dev.anhcraft.abm.api.misc.CustomBossBar;
import dev.anhcraft.abm.api.misc.SoundRecord;
import dev.anhcraft.abm.api.misc.info.InfoHolder;
import dev.anhcraft.abm.api.misc.Skin;
import dev.anhcraft.abm.utils.EnumUtil;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

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
        try {
            defaultMagazine = APIProvider.get().getMagazineModel(defaultMag)
                    .orElseThrow((Supplier<Throwable>) () -> new NullPointerException("MagazineModel did not exist"));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

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
            try {
                defaultScope = APIProvider.get().getScopeModel(defaultScp).orElseThrow((Supplier<Throwable>) () -> new NullPointerException("Scope not found"));
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
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
}
