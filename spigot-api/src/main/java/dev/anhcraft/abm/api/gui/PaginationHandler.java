package dev.anhcraft.abm.api.gui;

import org.bukkit.entity.Player;

import java.util.List;

public interface PaginationHandler {
    void pullData(Pagination pagination, Player player, List<PaginationItem> data);
}
