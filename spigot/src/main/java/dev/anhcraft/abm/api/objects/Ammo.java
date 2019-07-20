package dev.anhcraft.abm.api.objects;

import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.ext.BattleItem;
import dev.anhcraft.abm.system.ItemTag;
import dev.anhcraft.abm.utils.info.InfoHolder;
import org.bukkit.inventory.meta.tags.CustomItemTagContainer;
import org.bukkit.inventory.meta.tags.ItemTagType;
import org.jetbrains.annotations.NotNull;

public class Ammo extends BattleItem<AmmoModel> {
    @Override
    public void save(CustomItemTagContainer container) {
        getModel().ifPresent(ammoModel ->
                container.setCustomTag(ItemTag.AMMO_ID, ItemTagType.STRING, ammoModel.getId()));
    }

    @Override
    public void load(CustomItemTagContainer container) {
        BattlePlugin.getAPI()
                .getAmmoModel(container.getCustomTag(ItemTag.AMMO_ID, ItemTagType.STRING))
                .ifPresent(this::setModel);
    }

    @Override
    public void inform(@NotNull InfoHolder holder) {
        getModel().ifPresent(ammoModel -> ammoModel.inform(holder));
    }
}
