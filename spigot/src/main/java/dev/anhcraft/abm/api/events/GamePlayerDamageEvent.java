package dev.anhcraft.abm.api.events;

import dev.anhcraft.abm.api.ext.WeaponItem;
import dev.anhcraft.abm.api.objects.DamageReport;
import dev.anhcraft.abm.api.objects.Game;
import dev.anhcraft.abm.api.objects.GamePlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GamePlayerDamageEvent extends PlayerDamageEvent {
    public static final HandlerList handlers = new HandlerList();

    private GamePlayer gp1;
    private GamePlayer gp2;

    public GamePlayerDamageEvent(Game game, DamageReport report, LivingEntity entity, WeaponItem weapon, GamePlayer gp1, GamePlayer gp2) {
        super(game, report, entity, weapon);
        this.gp1 = gp1;
        this.gp2 = gp2;
    }

    public Player getPlayer(){
        return (Player) getEntity();
    }

    public GamePlayer getGameDamager() {
        return gp1;
    }

    public GamePlayer getGamePlayer() {
        return gp2;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
