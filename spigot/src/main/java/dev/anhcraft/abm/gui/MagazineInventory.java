package dev.anhcraft.abm.gui;

import com.google.common.collect.Multimap;
import dev.anhcraft.abif.PreparedItem;
import dev.anhcraft.abm.api.APIProvider;
import dev.anhcraft.abm.api.BattleAPI;
import dev.anhcraft.abm.api.gui.*;
import dev.anhcraft.abm.api.inventory.items.ItemType;
import dev.anhcraft.abm.api.inventory.items.MagazineModel;
import dev.anhcraft.abm.api.storage.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class MagazineInventory extends GuiHandler implements PaginationHandler {
    @Override
    public void pullData(Pagination pagination, Player player, Multimap<ItemStack, GuiListener<? extends SlotReport>> data) {
        BattleAPI api = APIProvider.get();
        Optional<PlayerData> pd = api.getPlayerData(player);
        pd.ifPresent(playerData -> {
            playerData.getInventory().getStorage(ItemType.MAGAZINE).list().forEach(ent -> {
                Optional<MagazineModel> omm = api.getMagazineModel(ent.getKey());
                if (omm.isPresent()) {
                    MagazineModel mm = omm.get();
                    PreparedItem pi = api.getItemManager().make(mm);
                    if(pi == null) return;
                    ItemStack item = mm.getSkin().transform(pi).build();
                    data.put(item, new GuiListener<SlotClickReport>(SlotClickReport.class) {
                        @Override
                        public void call(SlotClickReport event) {
                            event.getClickEvent().setCancelled(true);
                        }
                    });
                }
            });
        });
    }
}
