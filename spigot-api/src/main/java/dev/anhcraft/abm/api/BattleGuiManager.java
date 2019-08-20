package dev.anhcraft.abm.api;

import dev.anhcraft.abm.api.gui.Gui;
import dev.anhcraft.abm.api.gui.GuiHandler;
import dev.anhcraft.abm.api.gui.PlayerGui;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface BattleGuiManager {
    void registerGui(@NotNull String id, @NotNull Gui gui);
    void registerGuiHandler(@NotNull String id, @NotNull GuiHandler handler);
    @NotNull PlayerGui getPlayerGui(@NotNull Player player);
    void setBottomInv(@NotNull Player player, @NotNull String name);
    void renderBottomInv(@NotNull Player player, @NotNull PlayerGui apg);
    void openTopInventory(@NotNull Player player, @NotNull String name);
    void renderTopInventory(@NotNull Player player, @NotNull PlayerGui apg);
    void destroyPlayerGui(@NotNull Player player);
}
