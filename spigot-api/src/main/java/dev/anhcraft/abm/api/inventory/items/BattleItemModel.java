package dev.anhcraft.abm.api.inventory.items;

import dev.anhcraft.abm.api.misc.info.Informative;
import dev.anhcraft.abm.api.misc.info.InfoHolder;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BattleItemModel implements Informative {
    private String id;
    private String name;
    private InfoHolder cachedInfoHolder;

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

    public synchronized InfoHolder collectInfo(@Nullable String prefix) {
        if(cachedInfoHolder != null) return cachedInfoHolder;
        inform(cachedInfoHolder = new InfoHolder((prefix == null ? "" : prefix) +
                getItemType().name().toLowerCase() + "_"));
        return cachedInfoHolder;
    }
}
