package dev.anhcraft.abm.api.objects;

import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.ext.BattleItem;
import dev.anhcraft.abm.api.impl.Informative;
import dev.anhcraft.abm.system.ItemTag;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.tags.CustomItemTagContainer;
import org.bukkit.inventory.meta.tags.ItemTagType;

import java.util.Map;

public class AmmoItem extends BattleItem<Ammo> implements Informative {
    @Override
    public void save(CustomItemTagContainer container) {
        container.setCustomTag(ItemTag.AMMO_ID, ItemTagType.STRING, getModel().getId());
    }

    @Override
    public void load(CustomItemTagContainer container) {
        BattlePlugin.getAPI()
                .getAmmo(container.getCustomTag(ItemTag.AMMO_ID, ItemTagType.STRING))
                .ifPresent(this::setModel);
    }

    @Override
    public void writeInfo(Map<String, String> map, ConfigurationSection localeConf) {
        if(getModel() != null) getModel().writeInfo(map, localeConf);
    }
}
