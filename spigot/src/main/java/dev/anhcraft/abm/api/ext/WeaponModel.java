package dev.anhcraft.abm.api.ext;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public abstract class WeaponModel extends BattleItemModel {
    protected WeaponModel(@NotNull String id, @NotNull ConfigurationSection conf) {
        super(id, conf);
    }
}
