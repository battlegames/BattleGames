package dev.anhcraft.abm.api.ext;

import dev.anhcraft.abm.api.enums.ItemType;
import dev.anhcraft.abm.api.impl.Informative;
import dev.anhcraft.abm.utils.info.InfoHolder;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BattleItemModel implements Informative {
    private String id;
    private String name;

    protected BattleItemModel(@NotNull String id, @NotNull ConfigurationSection conf) {
        Validate.notNull(id, "Id must be non-null");
        Validate.isTrue(id.matches("[A-Za-z0-9_]+"), "Id must only contains A-Z,a-z, 0-9 and underscore only");
        Validate.notNull(conf, "Conf must be non-null");

        this.id = id;
        this.name = conf.getString("name", id);
    }

    @NotNull
    public abstract ItemType getItemType();

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @Override
    public void inform(@NotNull InfoHolder holder){
        holder.inform("id", id).inform("name", name);
    }

    public InfoHolder collectInfo(@Nullable String prefix) {
        InfoHolder h = new InfoHolder((prefix == null ? "" : prefix) +
                getItemType().name().toLowerCase() + "_");
        inform(h);
        return h;
    }
}
