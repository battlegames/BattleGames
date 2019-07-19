package dev.anhcraft.abm.system.handlers;

import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.gui.core.BattleSlot;
import dev.anhcraft.abm.gui.core.PlayerGui;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class PlayerInventoryHandler extends Handler {
    public PlayerInventoryHandler(BattlePlugin plugin) {
        super(plugin);
    }

    public void handleSlot(Player p, Cancellable event, int slot) {
        PlayerGui apg = plugin.guiManager.getGui(p);
        BattleSlot[] x = apg.getInternalInventory().getSlots();
        if(slot < x.length) {
            BattleSlot s = x[slot];
            if(s != null && s.getHandler() != null)
                event.setCancelled(plugin.guiManager.callSlotHandler(p, apg, s));
        }
    }
}
