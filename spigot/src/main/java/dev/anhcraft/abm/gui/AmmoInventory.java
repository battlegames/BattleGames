package dev.anhcraft.abm.gui;

import com.google.common.collect.Multimap;
import dev.anhcraft.abif.PreparedItem;
import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.enums.ItemType;
import dev.anhcraft.abm.api.ext.gui.GuiHandler;
import dev.anhcraft.abm.api.ext.gui.GuiListener;
import dev.anhcraft.abm.api.impl.gui.PaginationHandler;
import dev.anhcraft.abm.api.objects.AmmoModel;
import dev.anhcraft.abm.api.objects.PlayerData;
import dev.anhcraft.abm.api.objects.gui.Pagination;
import dev.anhcraft.abm.api.objects.gui.SlotClickReport;
import dev.anhcraft.abm.api.objects.gui.SlotReport;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class AmmoInventory extends GuiHandler implements PaginationHandler {
    public AmmoInventory(BattlePlugin plugin) {
        super(plugin);
    }

    @Override
    public void pullData(Pagination pagination, Player player, Multimap<ItemStack, GuiListener<? extends SlotReport>> data) {
        Optional<PlayerData> pd = plugin.getPlayerData(player);
        pd.ifPresent(playerData -> {
            playerData.getInventory().getStorage(ItemType.AMMO).list().forEach(ent -> {
                Optional<AmmoModel> oam = plugin.getAmmoModel(ent.getKey());
                if (oam.isPresent()) {
                    AmmoModel am = oam.get();
                    PreparedItem pi = plugin.itemManager.make(am);
                    if(pi == null) return;
                    ItemStack item = am.getSkin().transform(pi).build();
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
