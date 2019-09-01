package dev.anhcraft.abm.gui;

import dev.anhcraft.abm.api.APIProvider;
import dev.anhcraft.abm.api.BattleAPI;
import dev.anhcraft.abm.api.gui.*;
import dev.anhcraft.abm.api.inventory.items.ItemType;
import dev.anhcraft.abm.api.inventory.items.ScopeModel;
import dev.anhcraft.abm.api.storage.data.PlayerData;
import dev.anhcraft.craftkit.kits.abif.PreparedItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;

public class ScopeInventory extends GuiHandler implements PaginationHandler {
    @Override
    public void pullData(Pagination pagination, Player player, List<PaginationItem> data) {
        BattleAPI api = APIProvider.get();
        Optional<PlayerData> pd = api.getPlayerData(player);
        pd.ifPresent(playerData -> {
            playerData.getInventory().getStorage(ItemType.SCOPE).list().forEach(ent -> {
                Optional<ScopeModel> osm = api.getScopeModel(ent.getKey());
                if (osm.isPresent()) {
                    ScopeModel sm = osm.get();
                    PreparedItem pi = api.getItemManager().make(sm);
                    if(pi == null) return;
                    ItemStack item = sm.getSkin().transform(pi).build();
                    data.add(new PaginationItem(item, new GuiListener<SlotClickReport>(SlotClickReport.class) {
                        @Override
                        public void call(SlotClickReport event) {
                            event.getClickEvent().setCancelled(true);
                        }
                    }));
                }
            });
        });
    }
}