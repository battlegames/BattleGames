package dev.anhcraft.abm.gui;

import com.google.common.collect.Multimap;
import dev.anhcraft.abif.PreparedItem;
import dev.anhcraft.abm.api.APIProvider;
import dev.anhcraft.abm.api.BattleAPI;
import dev.anhcraft.abm.api.events.ItemChooseEvent;
import dev.anhcraft.abm.api.gui.*;
import dev.anhcraft.abm.api.inventory.items.GunModel;
import dev.anhcraft.abm.api.inventory.items.ItemType;
import dev.anhcraft.abm.api.storage.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class GunInventory extends GuiHandler implements PaginationHandler {
    @Override
    public void pullData(Pagination pagination, Player player, Multimap<ItemStack, GuiListener<? extends SlotReport>> data) {
        BattleAPI api = APIProvider.get();
        Optional<PlayerData> pd = api.getPlayerData(player);
        pd.ifPresent(playerData -> {
            playerData.getInventory().getStorage(ItemType.GUN).list().forEach(ent -> {
                Optional<GunModel> ogm = api.getGunModel(ent.getKey());
                if (ogm.isPresent()) {
                    GunModel gm = ogm.get();
                    PreparedItem pi = api.getItemManager().make(gm);
                    if(pi == null) return;
                    ItemStack item = gm.getPrimarySkin().transform(pi).build();
                    data.put(item, new GuiListener<SlotClickReport>(SlotClickReport.class) {
                        @Override
                        public void call(SlotClickReport event) {
                            event.getClickEvent().setCancelled(true);
                            ItemChooseEvent e = new ItemChooseEvent(event.getPlayer(), event.getClickEvent().getCurrentItem(), gm);
                            Bukkit.getPluginManager().callEvent(e);
                        }
                    });
                }
            });
        });
    }
}
