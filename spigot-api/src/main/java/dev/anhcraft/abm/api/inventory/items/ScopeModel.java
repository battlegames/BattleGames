package dev.anhcraft.abm.api.inventory.items;

import dev.anhcraft.abm.api.misc.Skin;
import dev.anhcraft.abm.utils.EnumUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ScopeModel extends BattleItemModel implements Attachable {
    private Skin skin;
    private List<Integer> zoomLevels;

    public ScopeModel(@NotNull String id, @NotNull ConfigurationSection conf) {
        super(id, conf);

        String material = conf.getString("skin.material");
        skin = new Skin(material == null ? null : EnumUtil.getEnum(Material.values(), material), conf.getInt("skin.damage"));
        zoomLevels = conf.getIntegerList("zoom_levels");
        zoomLevels.removeIf(integer -> {
            boolean b = integer < 1;
            if(b) Bukkit.getLogger().warning(String.format("Removed invalid zoom level `%s` in scope `%s`", integer, id));
            return b;
        });
    }

    @NotNull
    public Skin getSkin() {
        return skin;
    }

    @NotNull
    public List<Integer> getZoomLevels() {
        return zoomLevels;
    }

    @Override
    public ItemType[] getHolderTypes() {
        return new ItemType[]{
                ItemType.GUN
        };
    }

    @Override
    public @NotNull ItemType getItemType() {
        return ItemType.SCOPE;
    }
}
